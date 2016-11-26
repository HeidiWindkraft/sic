package sicc.bparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/** This Parser builds blocks out of plain source code.
 *
 *  It isn't a preprocessor. It assumes that either the preprocessor was already run
 *  or the language isn't preprocessed.
 *  Remaining preprocessor directives are passed to the PreprocessorHandler object.
 *  In particular, this Parser can't handle comments in preprocessor directives, which wrap over newlines.
 *  For example: #define FOO BAR /+ This is a
 *                   comment, this parser can't handle. +/
 *  (In C99 it is specified, that comments are replaced by single spaces, before processing directives.)
 *
*/
public class LegacyParser {
	
	private final char
		mOpen = '{', mClose = '}',
		mStr = '\"', mCh = '\'',
		mPpb = '#', mEsc = '\\',
		mLCo1 = '/', mLCo2 = '/',
		mBCo1 = '/', mBCo2 = '*';
	private char mLast = '\0', mCur = '\0';
	private final FilePos mFpos;
	private StringBuilder mBuf;
	private BufferedReader mReader;
	private PreprocessorHandler mPPH;
	private boolean mKeepPPDirs = false;
	
	public LegacyParser(String filename, BufferedReader reader, PreprocessorHandler pph) {
		mBuf = new StringBuilder();
		mFpos = new FilePos(filename, 1);
		mReader = reader;
		mPPH = pph;
	}
	public LegacyParser(String filename, Reader reader, PreprocessorHandler pph) {
		this(filename, new BufferedReader(reader), pph);
	}

	public void setKeepPPDirs() {
		mKeepPPDirs = true;
	}
	public void setDropPPDirs() {
		mKeepPPDirs = false;
	}
	
	private String fetchCurString() {
		String res = mBuf.toString();
		mBuf = new StringBuilder();
		return res;
	}
	private boolean inc() {
		boolean res;
		mLast = mCur;
		int nch = -1;
		try {
			nch = mReader.read();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (nch == -1) {
			mCur = '\0';
			res = false;
		} else {
			mCur = (char) nch;
			res = true;
		}
		if (mLast != '\0') {
			mBuf.append(mLast);
			if (mLast == '\n') {
				mFpos.setLine(mFpos.getLine() + 1);
				mFpos.setColumn(0);
			}
			mFpos.setColumn(mFpos.getColumn() + 1);
			mFpos.setTotal(mFpos.getTotal() + 1);
		}
		return res;
	}
	private void drop() {
		mCur = '\0';
	}
	private char last() {
		return mLast;
	}
	private char ch() {
		return mCur;
	}

	private boolean isStrEsc() {
		boolean res;
		if (last() == mEsc) {
			// Check if there is this is really an escape (it might be escaped itself)
			// If there is an odd number of \ before ch(), then ch() is escaped.
			// "\\\"", "\\\\", "\\\\\"", "\\\\\\", "\\\\\nabc\\", "\\\\\nabc\""
			int count = 0;
			for (int i = mBuf.length() - 1; i != -1; --i) {
				if (mBuf.charAt(i) == mEsc) {
					++count;
				} else {
					break;
				}
			}
			res = ((count % 2) == 1);
		} else {
			res = false;
		}
		return res;
	}
	
	public <B extends Block<B>> void fillBlock(Block<B> block) {
		fillBlock(block, 0);
	}

	private <B extends Block<B>> void fillBlock(Block<B> block, int depth) {
		block.assignFilePos(mFpos);
		BlockEntry<B> entry = new BlockEntry<B>();
		entry.assignFilePos(mFpos);
		block.add(entry);
		while (inc()) {
			if (ch() == mClose) {
				entry.setText(fetchCurString());
				drop();
				return;
			} else if (ch() == mOpen) {
				entry.setText(fetchCurString());
				drop();
				B sub = block.newBlock();
				sub.assignFilePos(mFpos);
				sub.setOuter(sub);
				entry.setSubBlock(sub);
				fillBlock(sub, depth + 1);
				entry = new BlockEntry<B>();
				entry.assignFilePos(mFpos);
				block.add(entry);
			} else if ((ch() == mPpb) && ((last() == '\n') || (mFpos.getTotal() == 0))) {
				goOverPP();
			} else if ((ch() == mStr)) {
				goOverStrLit();
			} else if (ch() == mCh) {
				goOverChLit();
			} else if ((ch() == mLCo2) && (last() == mLCo1)) {
				goOverLineComment();
			} else if ((ch() == mBCo2) && (last() == mBCo1)) {
				goOverBlockComment();
			} else {
				// just add this char...
			}
		}
		// reached EOF
		if (depth != 0) {
			throw new IllegalArgumentException("encountered EOF while reading a block");
		}
		if (entry.getText() != null) {
			throw new RuntimeException("[internal] entry already has a text");
		}
		entry.setText(fetchCurString());
		
	}
	private void goOverPP() {
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == '\n') && (last() != mEsc)) {
				if (mPPH != null) {
					mPPH.on(mBuf, begin.getTotal(), mFpos);
					if (!mKeepPPDirs) {
						if ((mFpos.getTotal() - begin.getTotal()) == 1) {
							// found "#\n" - replace it by " \n";
							mBuf.replace(mBuf.length() - 1, mBuf.length(), " ");
						} else {
							final String lcostr = "" + mLCo1 + "" + mLCo2;
							// Replace preprocessor statement by a comment
							mBuf.replace(begin.getTotal(), begin.getTotal() + 2, lcostr);
						}
					}
				}
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a preprocessor directive (at "+begin+")");
	}
	private void goOverStrLit() {
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == mStr) && (!isStrEsc())) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a string literal (at "+begin+")");
	}
	private void goOverChLit() {
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == mCh) && (!isStrEsc())) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a single-quoted literal (at "+begin+")");
	}
	private void goOverLineComment() {
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == '\n') /*&& (last() != mEsc)*/) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a line comment (at "+begin+")");
	}
	private void goOverBlockComment() {
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == mBCo1) && (last() == mBCo2)) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a block comment (at "+begin+")");
	}
}
