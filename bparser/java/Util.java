package sicc.bparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class Util {

	// ---- ---- ---- ---- ---- ---- ---- ----
	// IO utility
	// ---- ---- ---- ---- ---- ---- ---- ----
	
	private static final int CHARBUFSIZE = 0x400;

	protected static void chunks_append(Reader r, StringBuilder buf) throws IOException {
		final char[] chunk = new char[CHARBUFSIZE];
		for (;;) {
			final int n = r.read(chunk);
			if (n == -1) { break; }
			buf.append(chunk, 0, n);
		}
	}
	protected static void buffered_append(Reader r, StringBuilder buf) throws IOException {
		buffered_append(new BufferedReader(r), buf);
	}
	protected static void buffered_append(BufferedReader r, StringBuilder buf) throws IOException {
		int c;
		while ((c = r.read()) != -1) {
			buf.append((char)c);
		}
	}
	
	public static void append(Reader r, StringBuilder buf) throws IOException {
		chunks_append(r, buf);
	}
	public static void append(BufferedReader r, StringBuilder buf) throws IOException {
		chunks_append(r, buf);
	}
	public static String toString(Reader r) throws IOException {
		final StringBuilder buf = new StringBuilder();
		append(r, buf);
		return buf.toString();
	}
	public static String toString(BufferedReader r) throws IOException {
		final StringBuilder buf = new StringBuilder();
		append(r, buf);
		return buf.toString();
	}
}
