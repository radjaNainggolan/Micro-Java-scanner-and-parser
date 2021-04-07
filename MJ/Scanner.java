/* MicroJava Scanner (HM 06-12-28)
   =================
*/
package MJ;
import java.io.*;

public class Scanner {
	private static final char eofCh = '\u0080';
	private static final char eol = '\n';
	private static final int  // token codes
		none = 0,
		ident = 1,
		number = 2,
		charCon = 3,
		plus = 4, // +
		minus = 5, // -
		times = 6, // *
		slash = 7, // /
		rem = 8 , // %
		eql = 9, // ==
		neq = 10, // !=
		lss = 11 , // <
		leq = 12, // <=
		gtr = 13 , // >
		geq = 14, // >=
		assign = 15 , // =
		semicolon = 16 , // ;
		comma = 17 , // ,
		period = 18, // .
		lpar = 19 , // (
		rpar = 20 , // )
		lbrack = 21 , // [
		rbrack = 22 , // ]
		lbrace = 23 , // {
		rbrace = 24 , // }

		// keywords
		if_ = 25,
		else_ = 26,
		fi = 27,
		while_ = 28,
		for_ = 29,
		print_ = 30,
		let_ = 31,
		in_ = 32,
		end_ = 33,
		read = 34,
		eof = 35;


	private static final String key[] = { // sorted list of keywords
		"LET", "IN", "END","FOR" ,"WHILE", "IF" , "FI" ,"PRINT", "READ"
	};
	private static final int keyVal[] = {
		let_, in_, end_ , for_, while_, if_, fi, print_ , read
	};

	private static char ch;			// lookahead character
	public  static int col;			// current column
	public  static int line;		// current line
	private static int pos;			// current position from start of source file
	private static Reader in;    	// source file reader
	private static char[] lex;	    // current lexeme (token string)

	//----- ch = next input character
	private static void nextCh() {
		try {
			ch = (char)in.read(); col++; pos++;
			if (ch == eol) {line++; col = 0;}
			else if (ch == '\uffff') ch = eofCh;
		} catch (IOException e) {
			ch = eofCh;
		}
	}

	//--------- Initialize scanner
	public static void init(Reader r) {
		in = new BufferedReader(r);
		lex = new char[64];
		line = 1; col = 0;
		nextCh();
	}

	//---------- Return next input token
	public static Token next() {
		// add your code here
		while (ch <= ' ') nextCh(); 
		Token t = new Token(); t.line = line; t.col = col;
		switch (ch) {
				case  eofCh:
					t.kind = eof; 
					break;
				default: nextCh(); t.kind = none; break;
		}
		return t;
	}
}







