package sicc.bparser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public interface IPreprocessor {
	public final static String ENVK_PREPROCESSOR = "SIC_PREPROCESSOR";

	public static class Settings {
		public boolean keeptemp;
		public String bin;
		public String[] includes;
		public Map<String, String> defines;
		public String tempdir;
		public PrintStream err;
		public boolean useenv;
		// TODO add logger

		public String getTempDir() {
			if (tempdir == null) {
				// TODO get a tempdir
			}
			return tempdir;
		}
		public Path getTempDirPath() {
			return Paths.get(getTempDir());
		}
	}

	public static interface IPreprocessedFile {
		public InputStreamReader getInputStreamReader();
		public void delete() throws IOException;
	}

	public PreprocessorHandler getHandler();
	public IPreprocessedFile preprocess(Settings settings, String path);
}
