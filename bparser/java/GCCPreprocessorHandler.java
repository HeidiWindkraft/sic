package sicc.bparser;

import java.util.regex.*;

public class GCCPreprocessorHandler implements PreprocessorHandler {

	@Override
	public void on(StringBuilder buffer, int totalBegin, FilePos fpos) {
		/*
			# 1 "test.c"
			# 1 "<eingebaut>"
			# 1 "<Kommandozeile>"
			# 1 "test.c"
			# 1 "a.h" 1
			# 2 "test.c" 2
			# 1 "b.h" 1
			# 3 "test.c" 2
		*/
		
		final int begin = buffer.length() - (fpos.getTotal() - totalBegin);
		if (buffer.charAt(begin) != '#') {
			throw new IllegalArgumentException("Preprocessor statement not found");
		}
		final CharSequence directive = buffer.subSequence(begin, buffer.length());
		
		// only continue if this is a line number directive
		if (!Pattern.matches("#\\s*\\d+.*", directive)) {
			return;
		}
		
		final Pattern patLineNo = Pattern.compile("#\\s*(\\d+)\\s+\"(.*?)\".*");
		final Matcher match = patLineNo.matcher(directive);
		if (!match.matches()) {
			throw new IllegalArgumentException("couldn't match line-number directive");
		}
		final int lineNo = Integer.parseInt(match.group(1));
		final String fileNm = match.group(2);
		
		fpos.setFile(fileNm);
		fpos.setLine(lineNo - 1);
		
		/*if (buffer.charAt(begin+1) != ' ') {
			return;
		}
		
		int firstDigit = begin + 2;
		int beyondDigit = firstDigit;
		while (Character.isDigit(buffer.charAt(beyondDigit)) ) {
			++beyondDigit;
		}
		final String lineNoStr = buffer.substring(firstDigit, beyondDigit);
		final int lineNo = Integer.parseInt(lineNoStr, 10);
		
		if (buffer.charAt(beyondDigit))
		*/
	}

}
