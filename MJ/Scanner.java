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
		if_ = 25, // IF
		else_ = 26, // ELSE
		fi = 27, // FI
		while_ = 28, // WHILE
		for_ = 29,	// FOR
		print_ = 30, // PRINT
		let_ = 31, // LET
		in_ = 32, // IN
		end_ = 33, // END
		read = 34, // READ
		eof = 35; // end of file


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
			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i': case 'j': case 'k':
			case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
			case 'v': case 'w': case 'x': case 'y': case 'z': case 'A': case 'B': case 'C': case 'D': case 'E':
			case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M': case 'N': case 'O':
			case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y':
			case 'Z':
				readName(t);
				break;
			case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
				readNumber(t);
				break;
			case ';':
				nextCh();
				t.kind = semicolon;
				break;
			case '.':
				nextCh();
				t.kind = period;
				break;
			case eofCh:
				t.kind = eof;
				break; // no nextCh() any more beacues it's end of file
			case '=':
				nextCh();
				if (ch == '=') {
					nextCh();
					t.kind = eql; // it can be equality condition
				} else t.kind = assign; // or assign
				break;

			case '/': nextCh();
				if (ch == '/') {
					do nextCh();
					while (ch != '\n' && ch != eofCh);
					t = next(); // call scanner recursively ??? do not understand this part
				} else t.kind = slash;
				break;
			case '+':
				nextCh();
				t.kind = plus;
				break;
			case '-':
				nextCh();
				t.kind = minus;
				break;
			case '*':
				nextCh();
				t.kind = times;
				break;
			case '<':
				nextCh();
				if (ch == '=') {
					nextCh();
					t.kind = leq; // it can bee lowr or equal
				}else t.kind = lss; // or just lower
				break;
			case '>':
				nextCh();
				if (ch == '=') {
					nextCh();
					t.kind = geq; // it can be grater or equal
				} else t.kind = gtr; // or just greater
				break;
			case '!':
				nextCh();
				if (ch == '=') {
					nextCh();
					t.kind = neq; // not equal
				} else{
					t.kind = none; // or error
					Parser.error("The entered symbol is invalid");
				}
				break;
			case ',':
				nextCh();
				t.kind = comma;
				break;
			case '%':
				nextCh();
				t.kind = rem;
				break;
			case '(':
				nextCh();
				t.kind = lpar;
				break;
			case ')':
				nextCh();
				t.kind = rpar;
				break;
			case '[':
				nextCh();
				t.kind = lbrack;
				break;
			case ']':
				nextCh();
				t.kind = rbrack;
				break;
			case '{':
				nextCh();
				t.kind = lbrace;
				break;
			case '}':
				nextCh();
				t.kind = rbrace;
				break;
			case '\'':
				readCharCon(t);
				break;
			default:
				nextCh();
				t.kind = none; // error
				Parser.error("The token is not recognized");
				break;
		}
		return t;
	}

	private static int searchToken(String [] names , String token){

		int first = 0;
		int last = names.length-1;

		while(first <= last){
			int mid = (first + last) / 2;

			if(token.compareTo( names[mid] ) < 0){
				last = mid - 1;
			}else if (token.compareTo( names[mid] ) > 0 ){
				first = mid + 1;
			}else {
				return  mid;
			}
		}

		return  -1;
	}

	private static void readName(Token t){
		String name = "";
		while (Character.isLetterOrDigit(ch) || ch == '_'){ // while ch is digit or char or '_'
			name += ch; // append char
			nextCh(); // load new char
		}

		int index = searchToken(key , name);  // search through keywords to check if token is matching one of them
		if(index >= 0){
			t.kind = keyVal[index]; // if method returns number greater or equal to 0 token matched one of keywords
		}else{
			t.kind = ident; // else case its ID
		}

		t.string = name;

	}
	private static void readNumber(Token t){
		String number = "";

		while(Character.isDigit(ch)){ // while char is digit
			number += ch; // append
			nextCh(); // load new char
		}

		try {
			t.val = Integer.parseInt(number); // try to convert it into integer
		}catch (Exception e){
			Parser.error(" The entered number doesn't feet int capacity"); // report error
		}


	}
}







