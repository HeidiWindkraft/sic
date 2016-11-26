package sicc.bparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/** This Parser builds blocks out of plain source code.
 *
 *  It isn't a preprocessor. It assumes that either the preprocessor was already run
 *  or the language isn't preprocessed.
 *  Remaining preprocessor directives are passed to the PreprocessorHandler object.
 *  In particular, this Parser can't handle comments in preprocessor directives, which wrap over newlines.
 *  For example: #define FOO BAR /+ This is a
 *                   comment, this parser can't handle. +/
 *  (In C99 it is specified, that comments are replaced by single spaces, before processing directives.)
 *
*/
public class Parser extends
	//LegacyParser
	ModularParser
{	
	public Parser(String filename, BufferedReader reader, PreprocessorHandler pph) {
		super(filename, reader, pph);
	}
	public Parser(String filename, Reader reader, PreprocessorHandler pph) {
		super(filename, reader, pph);
	}
}
