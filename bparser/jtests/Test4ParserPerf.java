package siccTESTS.bparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import sicc.bparser.Util;

public class Test4ParserPerf {
	private final static String OFFS = "../../test/";

	private static final class TUtil extends Util {
		public static void bufferedAppend(Reader r, StringBuilder buf) throws IOException {
			buffered_append(r, buf);
		}
		public static void chunkAppend(Reader r, StringBuilder buf) throws IOException {
			chunks_append(r, buf);
		}
	}

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
		public Reader create() throws IOException;
	}

	private abstract static class Test {
		final String name;
		long runs = 0;
		long totalTime = 0;
		long avgTime = -1;
		int hash = 0;

		Test(String name) {
			this.name = name;
		}
		
		void run(ReaderCtor rdctor) {
			long start = 0, stop = 0;
			System.gc();
			try {
				final Reader rd = rdctor.create();
				start = System.nanoTime();
				Object result = actualRun(rd);
				stop = System.nanoTime();
				updateHash(result);
				rd.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			final long duration = stop - start;
			totalTime += duration;
			avgTime = ((runs * avgTime) + duration) / (runs + 1);
			runs++;
		}
		
		abstract Object actualRun(Reader rd) throws IOException;
		abstract void updateHash(Object o);

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
	
	private  abstract static class TestReadToString extends Test {
		public TestReadToString(String name) {
			super(name);
		}

		@Override
		void updateHash(Object o) {
			final StringBuilder buf = (StringBuilder)o;
			for (int i = 0; i < buf.length(); ++i) {
				hash = (31 * hash) ^ (int) buf.charAt(i);
			}
		}
	}
	
	private final static class TestReadChunksToString extends TestReadToString {
		public TestReadChunksToString() {
			super("ChunksToString");
		}
		
		@Override
		Object actualRun(Reader r) throws IOException {
			final StringBuilder buf = new StringBuilder();
			TUtil.chunkAppend(r, buf);
			return buf;
		}
	}
	private final static class TestReadBuffrdToString extends TestReadToString {
		public TestReadBuffrdToString() {
			super("BuffrdToString");
		}
		
		@Override
		Object actualRun(Reader r) throws IOException {
			final StringBuilder buf = new StringBuilder();
			TUtil.bufferedAppend(r, buf);
			return buf;
		}
	}
	private final static class TestReadUtlStdToString extends TestReadToString {
		public TestReadUtlStdToString() {
			super("UtlStdToString");
		}
		
		@Override
		Object actualRun(Reader r) throws IOException {
			final StringBuilder buf = new StringBuilder();
			Util.append(r, buf);
			return buf;
		}
	}

	private final static int N_STRMUL = 30;
	private final static int N_REPEAT = 1000;

	private static void testReader(String name, ReaderCtor r) {
		final long start = System.nanoTime();
		final Test
			chunksToStr = new TestReadChunksToString(),
			buffrdToStr = new TestReadBuffrdToString(),
			utlstdToStr = new TestReadUtlStdToString();
		final Test[] tests = new Test[] {
			chunksToStr,
			buffrdToStr,
			utlstdToStr
		};

		for (long run = 0; run < N_REPEAT; ++run) {
			for (Test test : tests) {
				test.run(r);
			}
		}

		final long stop = System.nanoTime();
		final long total = stop - start;
		System.out.println("Reader     : " + name);
		System.out.println("total time : " + total + " =\t" + timestr(total));
		long totalTest = 0;
		for (Test test : tests) {
			totalTest += test.totalTime;
		}
		System.out.println("test time  : " + totalTest + " =\t" + timestr(totalTest));
		System.out.println("test          \t#runs\tavgTime\ttotalTime\thash\ttotalTimeStr");
		for (Test test : tests) {
			test.println(System.out);
		}
		System.out.println();
	}
	
	private static void testFile(final String path) {
		final long start = System.nanoTime();
		final String contentOnce = readWholeTextFile(OFFS + path);
		final String contentMul = mul(N_STRMUL, contentOnce);
		final String SEP = "==== ==== ==== ==== ==== ==== ==== ====";
		
		System.out.println(SEP);
		
		System.out.println("File       : " + OFFS + path);
		System.out.println("N_STRMUL   : " + N_STRMUL);
		System.out.println("N_REPEAT   : " + N_REPEAT);
		
		System.out.println(SEP);
		
		testReader("StringReaderOnce", new ReaderCtor() {
			@Override
			public Reader create() { return new StringReader(contentOnce); }
		});
		testReader("StringReader*" + N_STRMUL, new ReaderCtor() {
			@Override
			public Reader create() { return new StringReader(contentMul); }
		});
		testReader("FileInputStreamReader", new ReaderCtor() {
			@Override
			public Reader create() throws IOException {
				return new InputStreamReader(new FileInputStream(OFFS + path));
			}
		});
		
		System.out.println(SEP);
		
		final long stop = System.nanoTime();
		final long total = stop - start;
		System.out.println("total time : " + total + " =\t" + timestr(total));
		
		System.out.println(SEP);
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

	public static void main(String[] args) {
		testFile("LongBlocks.txt");
	}
}
