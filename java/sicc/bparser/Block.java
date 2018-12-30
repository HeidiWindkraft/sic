package sicc.bparser;

import java.io.StringReader;
import java.util.ArrayList;

/** Block.
 *
 * A block is built of several block entries (see BlockEntry).
 * Each block entry is either terminated by a sub block, a raw string or the end of the block. 
 *
 * Example (indentation is ignored):
 * {       -----------------------+---------------------------
 *   bla {                        | Block 0: text: "\nbla ", suffix: sub-block.
 *     blu                        | Subblock: text "\nblu\n", suffix: null.
 *   }     -----------------------+---------------------------
 *   foo {                        | Block 1: text: "\nfoo ", suffix: sub-block.
 *     bar                        | SubBlock: text: "\nbar\n", suffix: null.
 *   }      ----------------------+---------------------------
 *   const char[] str = R"eos(    | Block 2: text: "\nconst char[] str = R\"", suffix: raw string.
 *     Hello, World!              | RawString: "eos(\nHello, World!\n)eos\"".
 *   )eos"; -----------------------+---------------------------
 *   muh                          | Block 3: text: ";\nmuh\n", suffix: null.
 *         -----------------------+---------------------------
 * }
 * 
 * @see BlockEntry 
*/
public abstract class Block<B extends Block<B>> extends ArrayList<BlockEntry<B>> {
	private static final long serialVersionUID = -5741971729110552976L;

	private final FilePos mFpos;
	private B mOuter;
	private String mPrefixToken, mSuffixToken;
	
	public Block() {
		mFpos = new FilePos();
	}

	public void assignFilePos(FilePos fp) { mFpos.assign(fp); }
	public void setOuter(B b) { mOuter = b; }
	public void setPrefixToken(String t) { mPrefixToken = t; }
	public void setSuffixToken(String t) { mSuffixToken = t; }

	public FilePos getFilePos() { return mFpos; }
	public String getFileName() { return mFpos.getFile(); }
	public int getLineNum() { return mFpos.getLine(); }
	public B getOuter() { return mOuter; }
	public String getPrefixToken() { return mPrefixToken; }
	public String getSuffixToken() { return mSuffixToken; }

	public abstract B newBlock();

	public void appendToStringBuilder(final StringBuilder buf, boolean topbrackets)
	{
		if (topbrackets) { buf.append('{'); }
		for (BlockEntry<B> entry : this)
		{
			buf.append(entry.getText());
			if (entry.getSubBlock() != null) {
				entry.getSubBlock().appendToStringBuilder(buf, true);
			} else if (entry.hasRawString()) {
				buf.append(entry.getRawString());
			} else {
				// Nothing to do.
			}
		}
		if (topbrackets) { buf.append('}'); }
	}
	public void appendToStringBuilder(final StringBuilder buf, final SpecialTokens st)
	{
		if (mPrefixToken != null) {
			buf.append(mPrefixToken);
		}
		int i = 0;
		for (BlockEntry<B> entry : this)
		{
			buf.append(entry.getText());
			if (entry.getSubBlock() != null) {
				buf.append(st.getBlockString(i));
			} else if (entry.hasRawString()) {
				buf.append(st.getRawStringString(entry.getRawString(), i));
			} else {
				// Nothing to do.
			}
			++i;
		}
		if (mSuffixToken != null) {
			buf.append(mSuffixToken);
		}
	}

	/*public void appendDotToStringBuilder(final StringBuilder buf, final String prefix) {
		for (int i = 0; i < this.size(); ++i) {
			final BlockEntry<B> entry = this.get(i);
			final String entryPrefix = prefix + "_" + Integer.toHexString(i);
			// TODO Text
			final B subb = entry.getSubBlock();
			if (subb != null) {
				subb.appendDotToStringBuilder(buf, entryPrefix + "b");
			}
		}
	}*/

	public UBlockReader newBlockReader(SpecialTokens toks) {
		return new UBlockReader(this, toks);
	}
	public StringReader newStringReader(SpecialTokens toks) {
		final StringBuilder buf = new StringBuilder();
		appendToStringBuilder(buf, toks);
		return new StringReader(buf.toString());
	}
}
