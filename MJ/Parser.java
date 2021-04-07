/*  MicroJava Parser (HM 06-12-28)
    ================
*/
package MJ;

import java.util.*;

public class Parser {
	private static final int  // token codes
		none	  = 0,
		eof       = 1,
		let_      = 2,
		in_	  	  = 3,
		end_	  = 4;
	private static final String[] name = { // token names for error messages
		"none", "eof", "let", "in", "end"
		};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// lookahead token
	private static int sym;			// always contains la.kind
	public  static int errors;  	// error counter
	private static int errDist;		// no. of correctly recognized tokens since last error

	//------------------- auxiliary methods ----------------------
	private static void scan() {
		t = la;
		la = Scanner.next();
		sym = la.kind;
		errDist++;
		/*
		System.out.print("line " + la.line + ", col " + la.col + ": " + name[sym]);
		if (sym == ident) System.out.print(" (" + la.string + ")");
		if (sym == number || sym == charCon) System.out.print(" (" + la.val + ")");
		System.out.println();*/
	}

	private static void check(int expected) {
		if (sym == expected) scan();
		else error(name[expected] + " expected");
	}

	public static void error(String msg) { // syntactic error at token la
		if (errDist >= 3) {
			System.out.println("-- line " + la.line + " col " + la.col + ": " + msg);
			errors++;
		}
		errDist = 0;
	}

	//-------------- parsing methods (in alphabetical order) -----------------

	// Program = LET Declarations IN CommandSequence END
	private static void Program() {
		return; 
	}

	//TODO  // add parsing methods for all productions

	public static void parse() {
		
		// start parsing
		errors = 0; errDist = 3;
		scan();
		Program();
		if (sym != eof) error("end of file found before end of program");
	}

}








