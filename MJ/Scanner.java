/* MicroJava Scanner (HM 06-12-28)
   =================
*/
package MJ;
import java.io.*;
import java.util.HashMap;

public class Scanner {
	private static final char eofCh = '\u0080';
	private static final char eol = '\n';
	private static final int  // token codes
		none = 0,
		let_ = 1, //LET
		in_ = 2, //IN
		end_ = 3, //END
		if_ = 4,// IF
		fi_ = 5, //FI
		else_ = 6,	//ELSE
		while_ = 7,	//WHILE
		for_ = 8, // FOR
		then_ = 9, // THEN
		break_ = 10, //BREAK
		print_ = 11, //PRINT
		readInt_ = 12, //READINT
		readString_ = 13, // READSTRING
		readBoolean_ = 14, // READBOOLEAN
		readDouble_ = 15, // READDOUBLE
		identifier_ = 16, // identifier
		integer_ = 17, // integer
		string_ = 18, // string
		boolean_ = 19 , // boolean
		double_ = 20, // double
		plus_ = 21, //+
		minus_ = 22, //-
		mult_ = 23, //*
		div_ = 24, // /
		mod_ = 25, // %
		less_ = 26, // <
		leq_ = 27, // <=
		gtr_ = 28, // >
		geq_ = 29, // >=
		eql_ = 30, //==
		neq_ = 31, // !=
		assign_ = 32, // =
		and_ = 33, // &&
		or_ = 34, // ||
		not_ = 35, // !
		semiColon_ = 36, // ;
		comma_ = 37, // ,
		period_ = 38, // .
		leftPar_ = 39, // (
		rightPar_ = 40, // )
		leftCurlyPar_ = 41, // {
		rightCurlyPar_ = 42, // }
		integerConst_ = 43, // int
		stringConst_ = 44, // string
		doubleConst_ = 45, // double
		booleanConst_ = 46, // boolean
		eof_ = 47; // end of file

	public static HashMap<String , Integer> keywords = new HashMap<String, Integer>(); // keywords map
	public static HashMap<String , Integer> tokens = new HashMap<String, Integer>(); // tokens map
	public static HashMap<String , Integer> dataTypes = new HashMap<String , Integer>(); // data types map
	public static HashMap<String , Integer> operators = new HashMap<String, Integer>(); // operators map

	private static void fillKeywords(){
		keywords.put("LET", let_);
		keywords.put("IN", in_);
		keywords.put("END", end_);
		keywords.put("IF", if_);
		keywords.put("THEN", then_);
		keywords.put("FI", fi_);
		keywords.put("ELSE", else_);
		keywords.put("WHILE", while_);
		keywords.put("FOR", for_);
		keywords.put("BREAK", break_);
		keywords.put("PRINT", print_);
		keywords.put("READINT", readInt_);
		keywords.put("READSTRING", readString_);
		keywords.put("READBOOL", readBoolean_);
		keywords.put("READDOUBLE", readDouble_);
	}

	private static void fillTokens(){
		tokens.put("Identifier", identifier_);
		tokens.put("+",plus_);
		tokens.put("-",minus_);
		tokens.put("*",mult_);
		tokens.put("/",div_);
		tokens.put("%",mod_);
		tokens.put("<",less_);
		tokens.put("<=",leq_);
		tokens.put(">",gtr_);
		tokens.put(">=",geq_);
		tokens.put("==",eql_);
		tokens.put("!=",neq_);
		tokens.put("=",assign_);
		tokens.put("&&",and_);
		tokens.put("||",or_);
		tokens.put("!",not_);
		tokens.put(";",semiColon_);
		tokens.put(",",comma_);
		tokens.put(".",period_);
		tokens.put("(",leftPar_);
		tokens.put(")",rightPar_);
		tokens.put("{",leftCurlyPar_);
		tokens.put("}",rightCurlyPar_);
	}

	private static void fillDatatypes() {
		dataTypes.put("integer", integer_);
		dataTypes.put("string", string_);
		dataTypes.put("bool", boolean_);
		dataTypes.put("double", double_);
	}

	private static void fillOperators(){

		operators.put("Plus", plus_);
		operators.put("Minus", minus_);
		operators.put("Mult", mult_);
		operators.put("Div", div_);
		operators.put("Mod", mod_);
		operators.put("less", less_);
		operators.put("LesOrEqual",less_);
		operators.put("Greater", gtr_);
		operators.put("GreaterOrEqual", geq_);
		operators.put("Equal", eql_);
		operators.put("NotEqual", neq_);
		operators.put("And", and_);
		operators.put("Or", or_);
		operators.put("Assign", assign_);

	}


	public static final String tokenNames[] = {
			"None", "LET", "IN", "END", "IF", "FI", "ELSE",
			"WHILE", "FOR", "THEN", "BREAK", "PRINT", "READINT",
			"READSTRING", "READBOOLEAN", "READDOUBLE", "Identifier",
			"INTEGER", "STRING", "BOOLEAN", "DOUBLE", "PLUS", "MINUS",
			"MULT", "DIV", "MOD", "Less", "LessOrEqual", "Greater", "GreaterOrEqual",
			"Equal", "NotEqual", "Assign", "And", "Or", "Not", "Semicolon", "Comma",
			"Period", "LeftParentheses", "RightParentheses", "LeftCurlyParentheses",
			"RightCurlyParentheses", "IntegerConstant", "StringConstant", "DoubleConstant",
			"BooleanConstant", "End of file"
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
		fillKeywords();
		fillTokens();
		fillDatatypes();
		fillOperators();
		nextCh();
	}

	//---------- Return next input token
	public static Token next() {
		// add your code here
		while (ch <= ' ') nextCh(); 
		Token t = new Token(); t.line = line; t.col = col;
		String chStr = Character.toString(ch);
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
			case eofCh:
				t.kind = eof_;
				break; // no nextCh() any more becaues it's end of file
			case '+': case'-': case '*': case  ';': case ',': case '.': case'(': case ')': case '%': case '{': case '}':
				nextCh();
				t.kind = tokens.get(chStr);
				t.string = chStr;
				break;  //

			case '/':

				nextCh();
				if (ch == '/') { // it can be comment or dividing
					do nextCh();
					while (ch != eol && ch != eofCh);
					nextCh();
					t = null;
				} else if(ch == '*'){
					char prev;
					nextCh();

					while (ch != eofCh && ch != '/'){
						prev = ch;
						nextCh();

						if(ch =='/'){
							if(prev == '*'){
								break;
							}else{
								nextCh();
							}
							while (ch =='/') nextCh();
						}
					}

					if(ch == eofCh){
						t.kind = none;
					}else{
						nextCh();
					}
					t = null;

				}else{
					t.kind = div_;
					t.string = chStr;

				}
				break;
			case '<': case '>': case '=': case '!':
				nextCh();
				if(ch == '='){ // it can be lower or lower or equal -||-
					nextCh();
					chStr += "=";
				}
				t.kind = tokens.get(chStr);
				t.string = chStr;
				break;
			case '&':
				nextCh(); // next must be & to
				if(ch == '&'){
					nextCh();
					t.kind = and_;
					t.string = "&&";
				}else{
					t.kind = none;
				}
				break;
			case '|':
				nextCh();
				if(ch == '|'){ // next must be |
					nextCh();
					t.kind = or_;
					t.string = "||";
				}else{
					t.kind = none;
				}
				break;
			default:
				nextCh();
				t.kind = none; // error
				Parser.error("The token is not recognized");
				break;
		}
		System.out.println(t.string);
		return t;
	}



	private static void readName(Token t){
		t.string = Character.toString(ch);
		nextCh();

		while (Character.isLetterOrDigit(ch) || ch == '_'){
			t.string += Character.toString(ch);
			nextCh();
		}

		if(t.string.compareTo("true") == 0 || t.string.compareTo("false") == 0){
			t.kind = booleanConst_;
			return;
		}

		for(String keyword : keywords.keySet()){

			if(t.string.compareTo(keyword) == 0){

				if(t.string.startsWith("READ")){

					if(ch == '('){
						nextCh();
						if(ch == ')'){
							nextCh();
							t.kind = keywords.get(keyword);
							return;
						}else {
							Parser.error("READ operation exspression is invalid, missing )");
							t.kind = none;
							return;
						}
					}else {
						Parser.error("READ operation exspression is invalid, missing (");
						t.kind = none;
						return;
					}

				}else{
					t.kind = keywords.get(keyword);
					return;
				}
			}
		}

		for (String type : dataTypes.keySet()){
			if(t.string.compareTo(type) == 0){
				t.kind = dataTypes.get(type);
				return;
			}
		}

		t.kind = identifier_;
	}


	private static void readNumber(Token t){
		t.string = Character.toString(ch);
		nextCh();
		while (Character.isDigit(ch) || ch == '.' || Character.toString(ch).equalsIgnoreCase("x") || ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F') || ch == '+' || ch == '-'){
			t.string += Character.toString(ch);
			nextCh();
		}

		if(t.string.matches("\\d+") || t.string.matches("^(0[xX])[a-fA-F\\d]+")){
			t.kind = integerConst_;
			if (t.string.startsWith("0X") || t.string.startsWith("0x")) {
				t.intValue = Integer.parseInt(t.string.substring(2), 16);
			} else {
				t.intValue = Integer.parseInt(t.string);
			}
		}else if (t.string.matches("\\d+\\.\\d*([eE][+-]?\\d+)?")) {
			t.kind = doubleConst_;
			t.doubleValue = Double.parseDouble(t.string);
		} else {
			Parser.error("Number constant is invalid");
			t.kind = none;
		}

	}

	private static void readCharCon(Token t){
		nextCh();
		t.string = "";

		while(ch != '\"' && ch != '\n'){
			t.string += Character.toString(ch);
			nextCh();
		}

		if(ch == '\"'){
			t.kind = stringConst_;
		}else {
			t.kind = none;
			Parser.errors++;
			Parser.error("String constant is invalid, missing \"");
		}

	}
}







