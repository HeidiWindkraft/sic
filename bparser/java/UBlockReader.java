package sicc.bparser;

import java.io.IOException;

/**
 * Unsynchronized block reader
*/
public final class UBlockReader extends java.io.Reader {

	private Block<?> mBlock;
	private SpecialTokens mSpecToks;
	private BlockEntry<?> mEntry;
	private int mNextEntry;
	private boolean mReachedSuffix;

	private String mCurStr;
	private int mNextCharIdx;
	private boolean mReadingSuffix;

	public UBlockReader(Block<?> block, SpecialTokens spectoks) {
		mBlock = block;
		mSpecToks = spectoks;
		mEntry = null;
		mNextEntry = 0;
		mReachedSuffix = false;

		mCurStr = strnevernull(mBlock.getPrefixToken());
		mNextCharIdx = 0;
		mReadingSuffix = false;
	}

	@Override
	public final int read(char[] cbuf, int off, int len) {
		int pos = off;
		int rem = len;
		for(;;) {
			final int directAvail = mCurStr.length() - mNextCharIdx;
			if (directAvail >= rem) {
				mCurStr.getChars(mNextCharIdx, mNextCharIdx + rem, cbuf, pos);
				mNextCharIdx += rem;
				pos += rem;
				rem = 0;
				return len;
			}

			// There are not enough characters in the current string.
			// Copy as much as available.
			mCurStr.getChars(mNextCharIdx, mCurStr.length(), cbuf, pos);
			mNextCharIdx = mCurStr.length();
			pos += directAvail;
			rem -= directAvail;
			// Get the next string (might reach EOF)
			if (mReachedSuffix) {
				// Reached EOF
				final int n = pos - off;
				return (n != 0)? n : -1;
			}
			nextString();
		}
		/*for (int i = off; i < len; ++i) {
			int c = read();
			if (c != -1) {
				cbuf[i] = (char) c;
			} else {
				int n = i - off;
				n = (n == 0)? -1 : n;
				return n;
			}
		}
		return len - off;*/
	}

	@Override
	public final int read() {
		while (mNextCharIdx >= mCurStr.length()) {
			if (mReachedSuffix) {
				return -1;
			}
			nextString();
		}
		char resch = mCurStr.charAt(mNextCharIdx);
		mNextCharIdx++;
		return (int) resch;
	}

	private void nextString() {
		// In any case we start at the beginning of the next string.
		mNextCharIdx = 0;
		
		// If the suffix of the current entry hasn't been read, yet,
		// then go there.
		if (mNextEntry != 0) {
			if (mEntry.hasSuffix() && !mReadingSuffix) {
				if (mEntry.hasSubBlock()) {
					mCurStr = mSpecToks.getBlockString(mNextEntry - 1);
				} else {
					mCurStr = mSpecToks.getRawStringString(
								mEntry.getRawString(), mNextEntry - 1);
				}
				mReadingSuffix = true;
				return;
			}
		}
		// We won't read a sub-block, but the next entry (or the suffix).
		mReadingSuffix = false;
		
		// Check whether the next entry exists.
		if (mNextEntry >= mBlock.size()) {
			// if not, go to the suffix.
			mCurStr = strnevernull(mBlock.getSuffixToken());
			mReachedSuffix = true;
			return;
		}

		// There is another block, so read it.
		mEntry = mBlock.get(mNextEntry);
		mNextEntry++;
		mCurStr = mEntry.getText();
		return;
	}

	private static String strnevernull(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	@Override
	public void close() throws IOException {
		// There's nothing to do...
	}
}