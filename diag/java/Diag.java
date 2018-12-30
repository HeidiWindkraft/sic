package sicc.diag;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Diag {
	public final static String KEY_UNKNOWN_SEVERITY = "UnknownSeverity";

	private Map<String, Severity> mSevMap = sDefaultSevMap;
	private final LinkedList<DiagException> mDiags;
	private final int[] mDiagCount;

	private static final Map<String, Severity> sDefaultSevMap;
	static {
		// sDefaultSevMap
		{
			final TreeMap<String, Severity> m = new TreeMap<String, Severity>();
			m.put(KEY_UNKNOWN_SEVERITY, Severity.FATAL);
			m.put(DiagException.KEY_INTERNAL, Severity.FATAL);
			m.put("ParserVlog", Severity.LOG);
			m.put("ParserError", Severity.FATAL);
			m.put("ParserFatal", Severity.FATAL);
			m.put("ParserUnexpectedException", Severity.FATAL);
			m.put("Log", Severity.LOG);
			sDefaultSevMap = java.util.Collections.unmodifiableMap(m);
		}
	}

	public Diag() {
		mDiags = new LinkedList<DiagException>();
		mDiagCount = new int[Severity.END_OF_SEVERITIES.ordinal()];
	}

	public void report(RuntimeException ex) {
		report(new DiagException(ex));
	}
	public void report(final DiagException ex) {
		mDiags.add(ex);
		final String key = ex.getKey();
		Severity sev = mSevMap.get(ex.getKey());
		if (sev == null) {
			sev = Severity.UFATAL;
			report(new DiagException(ex.refFPos(), KEY_UNKNOWN_SEVERITY, "Unknown diagnostics key: " + key));
		} else {
			ex.setDynamicSeverity(sev);
		}
		mDiagCount[sev.ordinal()] += 1;
	}

	public void printReport(PrintStream o, Severity min, Severity minStacktrace) {
		for (DiagException d : mDiags) {
			final int sev = mSevMap.get(d.getKey()).ordinal(); 
			if (sev >= min.ordinal()) {
				o.println(d.getMessage());
				if (sev >= minStacktrace.ordinal()) {
					d.printStackTrace(o);
				}
			}
		}
	}
	public void printReport(PrintStream o, Severity min) {
		printReport(o, min, Severity.END_OF_SEVERITIES);
	}
	public void printReport(PrintStream o) {
		printReport(o, Severity.DBGLOG, Severity.END_OF_SEVERITIES);
	}

	public int getDiagCount(Severity min, Severity max) {
		int sum = 0;
		for (int i = min.ordinal(); i <= max.ordinal(); ++i) {
			sum += mDiagCount[i];
		}
		return sum;
	}
	public int getDiagCount(Severity sev) {
		return getDiagCount(sev, sev);
	}
	public int getErrorCount() {
		return getDiagCount(Severity.ERROR, Severity.UFATAL);
	}
	public int getWarningCount() {
		return getDiagCount(Severity.WARNING);
	}
	public void clearReport() {
		mDiags.clear();
		for (int i = 0; i < mDiagCount.length; ++i) {
			mDiagCount[i] = 0;
		}
	}
}
