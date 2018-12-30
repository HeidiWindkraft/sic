package siccTESTS.bparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

//import org.junit.Test;

import sicc.bparser.*;

public class Test0 extends TestCase {
	
	static class TBlock extends Block<TBlock> {
		private static final long serialVersionUID = -2835368246431766512L;

		@Override
		public TBlock newBlock() {
			return new TBlock();
		}
		
	}
	
	static void report(TBlock block) {
		final StringBuilder buf = new StringBuilder();
		final SpecialTokens st = new SpecialTokens();
		block.appendToStringBuilder(buf, st);
		System.out.println(block.getFilePos());
		System.out.println(buf.toString());
		int i = 0;
		for (BlockEntry<TBlock> e : block) {
			System.out.println(i + " (" + e.getFilePos() + "):");
			final TBlock sub = e.getSubBlock();
			if (sub != null) {
				report(sub);
			}
			++i;
		}
	}
	
	private static void genTest(String str) {
		System.out.println("\n[[STRING_TEST]]");
		System.out.println(str);
		System.out.println("[RESULT]");
		final TBlock block = test(new StringReader(str));
		final StringBuilder buf = new StringBuilder();
		block.appendToStringBuilder(buf, false);
		final String rstr = buf.toString();
		final boolean
			correctEcho = str.equals(rstr),
			correctBlockRead = testBlockReader(block);
		System.out.println("[EQ : " + (correctEcho?"ok":"FAIL!") + "]");
		System.out.println("[TBR: " + (correctBlockRead?"ok":"FAIL!") + "]");
		System.out.println(rstr);
		assertTrue("EchoCorrectnessCheck", correctEcho);
		assertTrue("BlockReaderCheck", correctBlockRead);
	}
	private static boolean testBlockReader(TBlock block) {
		boolean res = false;
		/* GC... */ {
			final SpecialTokens toks = new SpecialTokens();
			final String s = readAll(block.newStringReader(toks));
			final String b = readAll(block.newBlockReader(toks));
			final String bb = readAll(new BufferedReader(block.newBlockReader(toks)));
			final boolean s_eq_b = s.equals(b);
			final boolean s_eq_bb = s.equals(bb);
			res = s_eq_b & s_eq_bb;
		}
		for (BlockEntry<TBlock> sub : block) {
			if (sub.hasSubBlock()) {
				res &= testBlockReader(sub.getSubBlock());
			}
		}
		return res;
	}
	private static String readAll(Reader r) {
		final StringBuilder buf = new StringBuilder();
		int c;
		try {
			while ((c = r.read()) != -1) {
				buf.append((char)c); 
			}
			r.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return buf.toString();
	}
	
	private static TBlock test(Reader r) {
		final Parser parser = new Parser("stdin", r, new GCCPreprocessorHandler());
		parser.setKeepPPDirs();
		final TBlock root = new TBlock();
		parser.fillBlock(root);
		report(root);
		return root;
	}
	
	public static void readStdinAndTest() {
		test(new InputStreamReader(System.in));
	}

	//@Test
	public void test00() {
		genTest("a{b{c}d}e");
		genTest("a{{b}{c}}d");
		genTest("{a}{b}{c}{d}{e}{f}{g}{h}{i}{j}{k}{l}{m}{n}{o}{p}{q}{r}{s}");
	}
	//@Test
	public void test01_1() {
		genTest("{}");
		genTest("");
	}
	//@Test
	public void test01_2() {
		genTest("{\"}\"}");
	}
	//@Test
	public void test01_3() {
		genTest("{\"\\\"}\"}");
	}
	//@Test
	public void test01_4() {
		genTest("{\"\\\"}\"}");
	}
	//@Test
	public void test01_5() {
		genTest("{\n#}\n}");
		genTest("{\n//}\n}");
		genTest("{/*}*/}");
	}
	//@Test
	public void test02() {
		genTest("#include<stdio.h>\n\n/* Program entry */\nint main(int argc, char **argv) {\n\tprintf(\"Hello, World!\");\n}\n");
		genTest("{\n# 5 \"bla.h\"\n{a}}");
		genTest("{\n# 5 \"bla.h\" garbage\n{a}}");
		genTest("{\n#pragma whatever\n{a}}");
		genTest("{\n# pragma whatever\n{a}}");
	}

	private static void genTestFromFile(String path) {
		final String OFFS = "../../test/";
		final StringBuilder buf = new StringBuilder();
		try {
			Util.append(new FileReader(OFFS + path), buf);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		genTest(buf.toString());
	}

	//@Test
	public void test03LongBlocks() {
		genTestFromFile("LongBlocks.txt");
	}

	//@Test
	public void test04RawStrings() {
		genTestFromFile("RawStrings.txt");
	}

	/*public static void main(String[] arg) {
		//System.out.println("\n[[STDIN]]");
		//testStdin();
	}*/
}
