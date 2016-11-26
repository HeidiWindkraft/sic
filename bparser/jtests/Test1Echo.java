package siccTESTS.bparser;

import java.io.InputStreamReader;

import sicc.bparser.Block;
import sicc.bparser.GCCPreprocessorHandler;
import sicc.bparser.Parser;

public class Test1Echo {
	static class TBlock extends Block<TBlock> {
		private static final long serialVersionUID = -4559491898836804409L;
		@Override
		public TBlock newBlock() {
			return new TBlock();
		}
	}

	public static void main(String[] args) {
		final TBlock root = new TBlock();
		final Parser parser = new Parser("<stdin>", new InputStreamReader(System.in), new GCCPreprocessorHandler());
		parser.fillBlock(root);
		final StringBuilder buf = new StringBuilder();
		root.appendToStringBuilder(buf, false);
		System.out.print(buf);
	}
}
