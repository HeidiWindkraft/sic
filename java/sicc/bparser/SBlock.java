package sicc.bparser;

/**
 * A simple block.
*/
public final class SBlock extends Block<SBlock> {
	private static final long serialVersionUID = -961890860943822495L;

	@Override
	public SBlock newBlock() {
		return new SBlock();
	}
}
