package sicc.bparser;

public interface PreprocessorHandler {
	public void on(StringBuilder buffer, int totalBegin, FilePos fpos);
}
