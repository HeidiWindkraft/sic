package sicc.bparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import sun.security.util.Length;

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
public class ModularParser {

	public static abstract class Input {
		char mCur = '\0';
		abstract String fetchCurString();
		abstract String fetchCurStringAndDropCurChar();
		abstract boolean inc(FilePos fpos);
		abstract CharSequence getCurrentChars();
	}
	public final static class PlainInput extends Input {
		private final String mData;
		private int mOffs;
		private int mPos;

		public PlainInput(String data, int offs) {
			mData = data;
			mOffs = offs;
			mPos = offs;
		}
		public PlainInput(Reader reader) {
			this(readToString(reader), 0);
		}

		@Override String fetchCurString() {
			final String res = getCurString();
			mOffs = mPos;
			return res;
		}
		@Override String fetchCurStringAndDropCurChar() {
			final String res = fetchCurString();
			++mOffs; // drop
			++mPos;
			return res;
		}
		@Override boolean inc(FilePos fpos) {
			boolean res;
			final char last = mCur;
			if (mPos >= mData.length()) {
				mCur = '\0';
				res = false;
			} else {
				mCur = mData.charAt(mPos);
				++mPos;
				res = true;
			}
			if (last != '\0') {
				//mBuf.append(last); //TODO
				if (last == '\n') {
					fpos.setLine(fpos.getLine() + 1);
					fpos.setColumn(0);
				}
				fpos.setColumn(fpos.getColumn() + 1);
				fpos.setTotal(fpos.getTotal() + 1);
			}
			return res;
		}
		@Override CharSequence getCurrentChars() {
			return getCurString();
		}

		private String getCurString() {
			// Here we assume an implementation of substring,
			// which doesn't copy the characters of the string. 
			return mData.substring(mOffs, mPos);
		}

		private static String readToString(Reader reader) {
			try {
				return Util.toString(reader);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	public final static class BufferedInput extends Input {
		private StringBuilder mBuf;
		private final BufferedReader mReader;
		
		public BufferedInput(Reader reader) {
			if (reader == null) {
				throw new IllegalArgumentException("Given reader is null");
			}
			BufferedReader br = null;
			if (reader instanceof BufferedReader) {
				br = (BufferedReader) reader;
			} else {
				br = new BufferedReader(reader);
			}
			mReader = br;
			mBuf = new StringBuilder();
		}

		@Override String fetchCurString() {
			String res = mBuf.toString();
			mBuf = new StringBuilder();
			return res;
		}
		@Override String fetchCurStringAndDropCurChar() {
			final String res =  fetchCurString();
			mCur = '\0'; // drop
			return res;
		}

		@Override boolean inc(FilePos fpos) {
			boolean res;
			final char last = mCur;
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
			if (last != '\0') {
				mBuf.append(last);
				if (last == '\n') {
					fpos.setLine(fpos.getLine() + 1);
					fpos.setColumn(0);
				}
				fpos.setColumn(fpos.getColumn() + 1);
				fpos.setTotal(fpos.getTotal() + 1);
			}
			return res;
		}

		@Override CharSequence getCurrentChars() {
			return mBuf;
		}
	}

	public static final class Language {
		public static final class Args {
			public static enum Builtin {
				C90,
				Cxx03,
				Cxx11
			};
			
			// For field documentation see Language class.
			public char
				mOpen, mClose,
				mStr1, mStr2,
				mPpb, mEsc,
				mLCo1, mLCo2,
				mBCo1, mBCo2,
				mRawPre, mRawStrBeg, mRawStrEnd;
			public boolean
				mHasStr,
				mRawNeedsEsc;

			public Args() {
				mOpen = '{'; mClose = '}';
				mStr1 = '\"'; mStr2 = '\'';
				mPpb = '#'; mEsc = '\\';
				mLCo1 = '/'; mLCo2 = '/';
				mBCo1 = '/'; mBCo2 = '*';

				mRawPre = 'R';
				mRawStrBeg = '('; mRawStrEnd = ')';

				mHasStr = true;
				mRawNeedsEsc = false;
			}
			public Args(Builtin bl) {
				mOpen = '{'; mClose = '}';
				mStr1 = '\"'; mStr2 = '\'';
				mPpb = '#'; mEsc = '\\';
				mBCo1 = '/'; mBCo2 = '*';
				
				// The raw string chars are not used, if !mRawPre.
				mRawStrBeg = '('; mRawStrEnd = ')';
				mHasStr = true;
				mRawNeedsEsc = false;
				
				char rawPre = '\0', lco1 = '\0', lco2 = '\0';
				switch (bl) {
				case Cxx11:
					rawPre = 'R';
					/* continue */
				case Cxx03:
					lco1 = '/'; lco2 = '/';
					/* continue */
				case C90:
					/* nothing to do */
					break;
				default:
					throw new IllegalArgumentException("Unsupported builtin language:" + bl);
				}

				mRawPre = rawPre;
				mLCo1 = lco1;
				mLCo2 = lco2;
			}
		}

		public final char
			mOpen, mClose,  //< The characters for opening and closing a block.
			mStr1, mStr2,   //< The characters beginning and ending string or character literals.
			mPpb,           //< The character starting a preprocessor line.
			mEsc,           //< The character used to escape newlines, delimiters etc.
			mLCo1, mLCo2,   //< The characters starting a line comment.
			mBCo1, mBCo2,   //< The characters starting and ending a block comment.
			mRawPre, mRawStrBeg, mRawStrEnd;    //< Prefix, begin and end of a raw string.
		public final boolean
			mHasStr,
			mRawNeedsEsc; //< Allows raw strings to start with \R instead of R

		public Language(Args a) {
			mOpen = a.mOpen; mClose = a.mClose;
			mStr1 = a.mStr1; mStr2 = a.mStr2;
			mPpb = a.mPpb;
			mEsc = a.mEsc;
			mLCo1 = a.mLCo1; mLCo2 = a.mLCo2;
			mBCo1 = a.mBCo1; mBCo2 = a.mBCo2;
			mRawPre = a.mRawPre; mRawStrBeg = a.mRawStrBeg; mRawStrEnd = a.mRawStrEnd;
			mRawNeedsEsc = a.mRawNeedsEsc;
			mHasStr = a.mHasStr;
		}
		
		static final Language DEFAULT = new Language();
		private Language() {
			this (new Args());
		}

		static final Language C90 = new Language(new Args(Args.Builtin.C90));
		static final Language Cxx03 = new Language(new Args(Args.Builtin.Cxx03));
		static final Language Cxx11 = new Language(new Args(Args.Builtin.Cxx11));

		public boolean hasRawStrings() {
			return mRawPre != '\0';
		}
		public boolean hasLineComments() {
			return mLCo1 != '\0';
		}
		public boolean hasBlockComments() {
			return mBCo1 != '\0';
		}
		public boolean isQuote(char c) {
			// Not sure whether it is possible to pass '\0'...
			return ((mStr1 != '\0') && (mStr1 == c))
					|| ((mStr2 == '\0') && (mStr2 == c));
			
		}
	}

	private final Language mLang;
	private char mLast = '\0';
	private final FilePos mFpos;
	private PreprocessorHandler mPPH;
	private boolean mKeepPPDirs = false;
	private final Input mIn;

	public ModularParser(String filename, Input in, PreprocessorHandler pph, Language lang) {
		if (in == null) {
			throw new IllegalArgumentException("Given input is null");
		}
		if (lang == null) {
			throw new IllegalArgumentException("No language given");
		}
		mLang = lang;
		mFpos = new FilePos(filename, 1);
		mPPH = pph;
		mIn = in;
	}

	public ModularParser(String filename, Input in, PreprocessorHandler pph) {
		this(filename, in, pph, new Language());
	}
	public ModularParser(String filename, Reader reader, PreprocessorHandler pph) {
		this(filename, new BufferedInput(reader), pph);
	}

	public void setKeepPPDirs() {
		mKeepPPDirs = true;
	}
	public void setDropPPDirs() {
		mKeepPPDirs = false;
	}
	
	private String fetchCurString() {
		return mIn.fetchCurString();
	}
	private String fetchCurStringAndDropCurChar() {
		return mIn.fetchCurStringAndDropCurChar();
	}
	private boolean inc() {
		mLast = mIn.mCur;
		boolean res = mIn.inc(mFpos);
		return res;
	}
	/*private void drop() {
		mIn.mCur = '\0';
	}*/
	private char last() {
		return mLast;
	}
	private char ch() {
		return mIn.mCur;
	}

	private boolean isStrEsc() {
		boolean res;
		if (last() == mLang.mEsc) {
			// Check if there is this is really an escape (it might be escaped itself)
			// If there is an odd number of \ before ch(), then ch() is escaped.
			// "\\\"", "\\\\", "\\\\\"", "\\\\\\", "\\\\\nabc\\", "\\\\\nabc\""
			int count = countLeadingEscapes(1);
			res = ((count % 2) == 1);
		} else {
			res = false;
		}
		return res;
	}
	private int countLeadingEscapes(int offset) {
		int count = 0;
		final CharSequence mChars = mIn.getCurrentChars();
		for (int i = mChars.length() - offset; i != -1; --i) {
			if (mChars.charAt(i) == mLang.mEsc) {
				++count;
			} else {
				break;
			}
		}
		return count;
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
			if (ch() == mLang.mClose) {
				entry.setText(fetchCurStringAndDropCurChar());
				return;
			} else if (ch() == mLang.mOpen) {
				entry.setText(fetchCurStringAndDropCurChar());
				B sub = block.newBlock();
				sub.assignFilePos(mFpos);
				sub.setOuter(sub);
				entry.setSubBlock(sub);
				fillBlock(sub, depth + 1);
				entry = new BlockEntry<B>();
				entry.assignFilePos(mFpos);
				block.add(entry);
			} else if ((ch() == mLang.mPpb) && ((last() == '\n') || (mFpos.getTotal() == 0))) {
				goOverPP();
			} else if (mLang.isQuote(ch())) {
				if (mLang.hasRawStrings() && last() == mLang.mRawPre
						&& (!mLang.mRawNeedsEsc || ((countLeadingEscapes(2) % 2) == 1) ))
				{
					endBlockGoingOverRawStrLit(entry, ch());
					entry = new BlockEntry<B>();
					entry.assignFilePos(mFpos);
					block.add(entry);
				} else if (mLang.mHasStr) {
					goOverStrLit(ch());
				} else {
					// This isn't the begin of a raw string no raw string and the language
					// has no normal strings. So just go on...
				}
			} else if (mLang.hasLineComments() && (ch() == mLang.mLCo2) && (last() == mLang.mLCo1)) {
				goOverLineComment();
			} else if (mLang.hasBlockComments() && (ch() == mLang.mBCo2) && (last() == mLang.mBCo1)) {
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
		//TODO Preprocessor statements end a block entry!
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == '\n') && (last() != mLang.mEsc)) {
				if (mPPH != null) {
					/*mPPH.on(mBuf, begin.getTotal(), mFpos);
					if (!mKeepPPDirs) {
						if ((mFpos.getTotal() - begin.getTotal()) == 1) {
							// found "#\n" - replace it by " \n";
							mBuf.replace(mBuf.length() - 1, mBuf.length(), " ");
						} else {
							final String lcostr = "" + mLCo1 + "" + mLCo2;
							// Replace preprocessor statement by a comment
							mBuf.replace(begin.getTotal(), begin.getTotal() + 2, lcostr);
						}
					}*/
				}
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a preprocessor directive (at "+begin+")");
	}
	private void goOverStrLit(char endChar) {
		if (!mLang.isQuote(endChar)) {
			throw new RuntimeException("[INTERNAL]: endChar isn't a quote mark: " + endChar);
		}
		final FilePos begin = new FilePos(mFpos);
		while (inc()) {
			if ((ch() == endChar) && (!isStrEsc())) {
				return;
			}
		}
		throw new IllegalArgumentException(
			"reached EOF while reading a string/char literal (at "
			+ begin + ", staring with {" + endChar + "})");
	}

	private void goUntilChar(char c, FilePos begin, String message) {
		while (inc()) {
			if (ch() == c) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF searching for '" + c + "' (started at "+begin+") " + message);
	}

	private <B extends BlockEntry<?>> void endBlockGoingOverRawStrLit(B entry, char endChar) {
		// We just got the 'R', so this will be the last chars of this block.
		entry.setText(fetchCurString());
		goOverRawStrLit(endChar);
		// Store the rest of the raw string, starting with '"delim(' and ending with ')delim"'. 
		entry.setRawString(fetchCurString());
	}

	private void goOverRawStrLit(char endChar) {
		/* If raw strings are collected, they end blocks.
		 * This is because
		 *  - The char sequence has to be modified: R"(blabla)" becomes R"@1".
		 *  - The raw string might span multiple lines and therefore mess up line information.
		*/
		// #              R"delim(
		// # We are here   ^
		final FilePos begin = new FilePos(mFpos);
		// Find opening bracket.
		goUntilChar(mLang.mRawStrBeg, begin, "to find the begin of a raw string");
        // #        R"delim(
        // # We are here   ^
		final FilePos openingPos = new FilePos(mFpos);
		// Everything between " and ( is the delimiter.
		final int delimiterLength = openingPos.getTotal() - begin.getTotal() - 1;
		CharSequence curseq = mIn.getCurrentChars();
		int seqend = curseq.length();
		int seqbegin = seqend - delimiterLength;
		final CharSequence delimiter = curseq.subSequence(seqbegin, seqend);
		// Get the rest of the raw string ...
		curseq = null;
		for (;;) {
			// Find the next ".
			goUntilChar(endChar, openingPos, "to find the end of a raw string");
			// #      R"delim(...)delim"
			// # We might be here      ^
			// If this '"' followed a ')delimiter', then this is the end of the string.
			curseq = mIn.getCurrentChars();
			seqend = curseq.length();
			seqbegin = seqend - delimiterLength;
			if (curseq.charAt(seqbegin - 1) != mLang.mRawStrEnd) {
				continue;
			}
			final CharSequence tail = curseq.subSequence(seqbegin, seqend);
			if (tail.equals(delimiter)) {
				break;
			}
			// If this '"' didn't follow a ')delimiter', search on.
		}
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
			if ((ch() == mLang.mBCo1) && (last() == mLang.mBCo2)) {
				return;
			}
		}
		throw new IllegalArgumentException("reached EOF while reading a block comment (at "+begin+")");
	}
}
