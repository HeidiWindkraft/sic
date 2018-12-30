package siccTESTS.bparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import sicc.bparser.GCCPreprocessorHandler;
import sicc.bparser.Parser;
import sicc.bparser.SBlock;
import sicc.bparser.SpecialTokens;

public class Test3ReaderPerf {
	private final static String OFFS = "../../test/";

	private static String readWholeTextFile(String path) {
		final StringBuilder buf = new StringBuilder();
		try {
			final Reader ir = new InputStreamReader(new FileInputStream(path));
			int c;
			while ((c = ir.read()) != -1) {
				buf.append((char)c);
			}
			ir.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return buf.toString();
	}
	private static String mul(int n, String str) {
		final StringBuilder buf = new StringBuilder();
		buf.ensureCapacity(n * str.length());
		for (int i = 0; i < n; ++i) {
			buf.append(str);
		}
		return buf.toString();
	}
	
	private static interface ReaderCtor {
		public Reader create(SBlock block);
	}

	private abstract static class Test {
		final String name;
		final ReaderCtor ctor;
		long runs = 0;
		long totalTime = 0;
		long avgTime = -1;
		int hash = 0;

		Test(String name, ReaderCtor ctor) {
			this.name = name;
			this.ctor = ctor;
		}
		
		abstract void run(SBlock block);

		PrintStream println(PrintStream o) {
			o.println(
				name + "\t" +
				runs + "\t" +
				avgTime + "\t" +
				totalTime + "\t" +
				hash + "\t" +
				timestr(totalTime)
			);
			return o;
		}
	}
	
	private final static class UnbufTest extends Test {
		public UnbufTest(String name, ReaderCtor ctor) {
			super(name, ctor);
		}
		
		void run(SBlock block) {
			long start = 0, stop = 0;
			System.gc();
			try {
				int c;
				start = System.nanoTime();
				final Reader rd = ctor.create(block);
				while ((c = rd.read()) != -1) {
					hash ^= (char) c;
				}
				stop = System.nanoTime();
				rd.close();
			} catch (Exception ex ) {
				throw new RuntimeException(ex);
			}
			final long duration = stop - start;
			totalTime += duration;
			avgTime = ((runs * avgTime) + duration) / (runs + 1);
			runs++;
		}
	}

	private final static class BufferedTest extends Test {
		public BufferedTest(String name, ReaderCtor ctor) {
			super(name, ctor);
		}
		
		void run(SBlock block) {
			long start = 0, stop = 0;
			System.gc();
			try {
				int c;
				start = System.nanoTime();
				final BufferedReader rd = new BufferedReader(ctor.create(block));
				while ((c = rd.read()) != -1) {
					hash ^= (char) c;
				}
				stop = System.nanoTime();
				rd.close();
			} catch (Exception ex ) {
				throw new RuntimeException(ex);
			}
			final long duration = stop - start;
			totalTime += duration;
			avgTime = ((runs * avgTime) + duration) / (runs + 1);
			runs++;
		}
	}
	private final static class BufferedWoCtorTest extends Test {
		public BufferedWoCtorTest(String name, ReaderCtor ctor) {
			super(name, ctor);
		}
		
		void run(SBlock block) {
			long start = 0, stop = 0;
			System.gc();
			try {
				int c;
				final BufferedReader rd = new BufferedReader(ctor.create(block));
				start = System.nanoTime();
				while ((c = rd.read()) != -1) {
					hash ^= (char) c;
				}
				stop = System.nanoTime();
				rd.close();
			} catch (Exception ex ) {
				throw new RuntimeException(ex);
			}
			final long duration = stop - start;
			totalTime += duration;
			avgTime = ((runs * avgTime) + duration) / (runs + 1);
			runs++;
		}
	}

	private final static int N_STRMUL = 30;
	private final static int N_REPEAT = 1000;

	/*private static long testReader(Reader rd, char[] arr, long run, long val) {
		long start = 0, stop = 0;
		System.gc();
		try {
			int idx = 0;
			int c;
			start = System.nanoTime();
			while ((c = rd.read()) != -1) {
				arr[idx++] = (char) c;
			}
			stop = System.nanoTime();
			rd.close();
		} catch (Exception ex ) {
			throw new RuntimeException(ex);
		}
		long duration = stop - start;
		long res = ((run * val) + duration) / (run + 1);
		return res;
	}
	private static int hash(int h, char[] arr) {
		for (char c : arr) {
			h ^= (int) c;
		}
		return h;
	}*/
	
	private static void testFile(String path) {
		final long start = System.nanoTime();
		final SpecialTokens toks = new SpecialTokens();
		final ReaderCtor
			strrdctor = new ReaderCtor() {
				@Override
				public Reader create(SBlock block) {
					return block.newStringReader(toks);
				}
			},
			blkrdctor = new ReaderCtor() {
				@Override
				public Reader create(SBlock block) {
					return block.newBlockReader(toks);
				}
			};
		final Test
			tstr = new UnbufTest("_StrRd", strrdctor),
			tbstr = new BufferedTest("BStrRd",strrdctor),
			tblk = new UnbufTest("_BlkRd", blkrdctor),
			tbblk = new BufferedTest("BBlkRd",blkrdctor),
			tnstr = new	BufferedWoCtorTest("NStrRd", strrdctor);
		final Test[] tests = new Test[] {
				tstr,
				tbstr,
				tblk,
				tbblk,
				tnstr
		};
		final String content0 = readWholeTextFile(OFFS + path);
		final String content1 = mul(N_STRMUL, content0);
		final Reader ir = new StringReader(content1);
		final SBlock block = new SBlock();
		final Parser par = new Parser(path, ir, new GCCPreprocessorHandler());
		par.fillBlock(block);

		for (long run = 0; run < N_REPEAT; ++run) {
			for (Test test : tests) {
				test.run(block);
			}
		}
		final long stop = System.nanoTime();
		final long total = stop - start;
		System.out.println("File       : " + OFFS + path);
		System.out.println("N_STRMUL   : " + N_STRMUL);
		System.out.println("N_REPEAT   : " + N_REPEAT);
		System.out.println("total time : " + total + " =\t" + timestr(total));
		long totalTest = 0;
		for (Test test : tests) {
			totalTest += test.totalTime;
		}
		System.out.println("test time  : " + totalTest + " =\t" + timestr(totalTest));
		System.out.println("test\t#runs\tavgTime\ttotalTime\thash\ttotalTimeStr");
		for (Test test : tests) {
			test.println(System.out);
		}
		System.out.println();
	}

	private static String timestr(long nanos) {
		final int
			US = 1000,
			MS = US * 1000,
			S = MS * 1000;
		
		final StringBuilder buf = new StringBuilder();
		long val = nanos;
		
		buf.append(val / S);
		val %= S;
		buf.append("s ");
		buf.append(val / MS);
		val %= MS;
		buf.append("ms ");
		buf.append(val / US);
		val %= US;
		buf.append("us ");
		buf.append(val);
		buf.append("ns ");
		return buf.toString();
			
	}
	
	/*private static void testFile(String path) {
		final String content0 = readWholeTextFile(OFFS + path);
		final String content1 = mul(N_STRMUL, content0);
		final Reader ir = new StringReader(content1);
		final SBlock block = new SBlock();
		final Parser par = new Parser(path, ir, new GCCPreprocessorHandler());
		final SpecialTokens toks = new SpecialTokens();
		par.fillBlock(block);

		final char[] chararr = new char[2 * content1.length()];

		long avgStringRead = -1;
		long avgBufStringRead = -1;
		long avgBlockRead = -1;
		long avgBufBlockRead = -1;
		int h = 0;
		for (long run = 0; run < N_REPEAT; ++run) {
			avgStringRead = testReader(block.newStringReader(toks), chararr, run, avgStringRead);
			h = hash(h, chararr);
			avgBufStringRead = testReader(new BufferedReader(block.newStringReader(toks)), chararr, run, avgBufStringRead);
			h = hash(h, chararr);
			avgBlockRead = testReader(block.newBlockReader(toks), chararr, run, avgBlockRead);
			h = hash(h, chararr);
			avgBufBlockRead = testReader(new BufferedReader(block.newBlockReader(toks)), chararr, run, avgBufBlockRead);
			h = hash(h, chararr);
		}
		System.out.println("File       : " + OFFS + path);
		System.out.println("N_STRMUL   : " + N_STRMUL);
		System.out.println("N_REPEAT   : " + N_REPEAT);
		System.out.println("Hash       : " + h);
		System.out.println("StringRd   : " + avgStringRead);
		System.out.println("BufStringRd: " + avgBufStringRead);
		System.out.println("BlockRd    : " + avgBlockRead);
		System.out.println("BufBlockRd : " + avgBufBlockRead);
		System.out.println();
	}*/

	public static void main(String[] args) {
		testFile("LongBlocks.txt");
	}
}
