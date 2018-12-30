package sicc.bparser;

/** BlockEntry.
 *
 * A block entry contains a text paragraph of a block.
 * Such a text paragraph is either terminated by a sub block, a raw string or the end of the block.
 * We store raw strings, because they are hard to tokenize using simple regular expressions.
 *
 * Example - BlockEntry terminated by SubBlock:
 * {
 *   Hello, I'm the first block entry of this block
 *   and this is my text paragraph.
 *   {
 *     And I'm the sub block terminating the text paragraph.
 *   }
 *   I'm the second block entry.
 * 
 * Example - BlockEntry terminated by RawString:
 * Hello, I'm the
 * text paragraph R"raw(
 *  And I'm the raw string terminating the text paragraph
 *  (starting at R"raw)
 * )raw"
 * I'm the block entry following the above one.
 *
 * Example - BlockEntry terminated by end of block.
 *   I'm the last entry of this block
 * }
 *
 * @see Block
*/
public class BlockEntry<B extends Block<B>> {
	private final FilePos mFPos;
	private String mText;
	private B mSubBlock;
	private String mRawString;
	
	public BlockEntry() {
		mFPos = new FilePos();
	}
	
	public void assignFilePos(FilePos fp) { mFPos.assign(fp); }
	public void setText(String t) { mText = t; }
	public void setSubBlock(B b) { mSubBlock = b; }
	public void setRawString(String rawString) { mRawString = rawString; }
	
	public FilePos getFilePos() { return mFPos; }
	public String getText() { return mText; }
	public B getSubBlock() { return mSubBlock; }
	public String getRawString() { return mRawString; }
	
	public boolean hasSubBlock() { return mSubBlock != null; }
	public boolean hasRawString() { return mRawString != null; }
	public boolean hasSuffix() { return hasSubBlock() || hasRawString(); }
}
