package sicc.bparser;

public class FilePos {
	private String mFile;
	private int mLine;
	private int mColumn;
	private int mTotal;
	
	public FilePos() {
		this("", 0);
	}
	public FilePos(String file, int line) {
		this (file, line, 0, 0);
	}
	public FilePos(String file, int line, int column, int total) {
		mFile = file;
		mLine = line;
		mColumn = column;
		mTotal = total;
	}
	public FilePos(final FilePos fpos) {
		this(fpos.getFile(), fpos.getLine(), fpos.getColumn(), fpos.getTotal());
	}

	public void setFile(String fnm) { mFile = fnm; }
	public void setLine(int lno) { mLine = lno; }
	public void setColumn(int cno) { mColumn = cno; }
	public void setTotal(int tno) { mTotal = tno; }
	
	public String getFile() { return mFile; }
	public int getLine() { return mLine; }
	public int getColumn() { return mColumn; }
	public int getTotal() { return mTotal; }

	public void assign(FilePos fp) {
		mFile = fp.mFile;
		mLine = fp.mLine;
		mColumn = fp.mColumn;
		mTotal = fp.mTotal;
	}
	
	@Override
	public String toString() {
		return mFile + ":" + mLine;
	}
}
