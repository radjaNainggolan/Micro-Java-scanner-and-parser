/*  MicroJava Parser (HM 06-12-28)
    ================
*/
package MJ;

import java.util.*;

public class Parser {
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

	private static final String[] name = { // token names for error messages
			"none", "LET", "IN", "END", "IF", "FI", "ELSE", "WHILE", "FOR", "BREAK",
			"PRINT", "THEN", "READINT()", "READSTRING()", "READBOOLEAN()", "READDOUBLE()",
			"identifier", "integer", "string", "boolean", "double", "+", "-", "*", "/", "%",
			"<", "<=", ">", ">=", "==", "!=", "=", "&&", "||", "!", ";", ",", ".", "(", ")",
			"integer constant", "string constant", "boolean constant", "double constant", "{", "}"

	};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// lookahead token
	private static int sym;			// always contains la.kind
	public  static int errors;  	// error counter
	private static int errDist;		// no. of correctly recognized tokens since last error
	private static HashMap<String, ArrayList<Integer>> map;
	//------------------- auxiliary methods ----------------------
	private static void scan() {
		t = la;
		la = Scanner.next();
		while(la == null){
			la = Scanner.next();
		}
		sym = la.kind;
		errDist++;
		/*
		System.out.print("line " + la.line + ", col " + la.col + ": " + name[sym]);
		if (sym == ident) System.out.print(" (" + la.string + ")");
		if (sym == number || sym == charCon) System.out.print(" (" + la.val + ")");
		System.out.println();*/
	}

	private static void check(int expected) {
		if (sym == expected){
			if(sym == identifier_ && la.string.length() > 31){
				error("Identifier have to many chars (31 is limit)");
			}
			scan();
		} else error(name[expected] + " expected");
	}

	public static void error(String msg) { // syntactic error at token la
		if (errDist >= 3) {
			System.out.println("-- line " + la.line + " col " + la.col + ": " + msg);
			errors++;
		}
		errDist = 0;
	}

	//-------------- parsing methods (in alphabetical order) -----------------

	// Program ::= LET Declarations IN CommandSequence END
	private static void Program() {
		check(let_);
		Declarations();
		check(in_);
		CommandSequence();
		check(end_);
	}

	//Declarations ::= Decl+
	private static void Declarations(){

		while (map.get("Declarations").contains(sym)){
			Decl();
		}
	}
	//Decl ::= Type ident
	private static void Decl(){
		Type();
		check(identifier_);
		check(semiColon_);

	}
	// Type ::= integer | string | boolean | double
	private static void Type(){
		if(sym == integer_ || sym == string_ || sym == boolean_ || sym == double_){
			scan();
		}else {
			error("Invalid varibale type (integer, srting, boolean, double  expected)");
		}
	}

	// CommandSequence ::= { Stmt+ }
	private static void CommandSequence(){
		check(leftCurlyPar_);
		while(map.get("CommandSequence").contains(sym)){
			Stmt();
		}
		check(rightCurlyPar_);
	}

	//Stmt ::= IfStmt | WhileStmt | ForStmt | BreakStmt | PrintStmt | AssignExpr; | CommandSequence
	private static void Stmt(){

		if(sym == if_){
			IfStmt();
		}else if(sym == while_){
			WhileStmt();
		}else if(sym == for_){
			ForStmt();
		}else if(sym == break_){
			BreakStmt();
		}else if(sym == print_){
			PrintStmt();
		}else if(sym == identifier_){
			AssignExpr();
			check(semiColon_);
		}else if(map.get("Expr").contains((sym))){
			Expr();
			check(semiColon_);
		}else{
			error("Statement error");
		}
	}

	// IfStmt ::= IF (Expr) CommandSequence IfStmtEnd
	private static void IfStmt(){
		check(if_);
		check(leftPar_);
		Expr();
		check(rightPar_);
		CommandSequence();
		IfStmtEnd();
	}

	private static void IfStmtEnd(){
		if(sym == else_){
			scan();
			CommandSequence();
			check(fi_);
		}else if(sym == fi_){
			scan();
		}else {
			error("Invalid end for IF statement");
		}
	}

	// WhileStmt ::= While (Expr) CommandSequence
	private static void WhileStmt(){
		check(while_);
		check(leftPar_);
		Expr();
		check(rightPar_);
		CommandSequence();
	}

	//ForStmt ::= FOR (Expr; Expr; Expr) CommandSequence
	private static void ForStmt(){
		check(for_);
		check(leftPar_);
		AssignExpr();
		check(semiColon_);
		Expr();
		check(semiColon_);
		AssignExpr();
		check(rightPar_);
		CommandSequence();
	}

	// BreaksStmt ::= BREAK;
	private static void BreakStmt(){
		check(break_);
		check(semiColon_);
	}

	//PrintStmt ::= Print(Expr)
	private static void PrintStmt(){
		check(print_);
		check(leftPar_);
		Expr();
		check(rightPar_);
		check(semiColon_);
	}

	// AssignExpr ::= ident = Expr
	private static void AssignExpr(){
		check(identifier_);
		check(assign_);
		Expr();
	}
	// Expr ::= Expr1 ExprL
	private static void Expr(){
		Expr1();
		ExprL();
	}
	// ExprL ::= LogicalOp Expr1 ExprL | eps
	private static void ExprL(){

		if(map.get("LogicalOp").contains(sym)){
			scan();
			Expr1();
			ExprL();
		}

	}

	// Expr1 ::= Expr2 Expr1End
	private static void Expr1(){
		Expr2();
		Expr1End();
	}

	// Expr1End ::= EqualityOp Expr2 | eps ------------- EqualityOp ::= == | !=
	private static void Expr1End(){
		if(map.get("EqualityOp").contains(sym)){
			scan();
			Expr2();
		}
	}
	// Expr2 ::= Expr3 Expr2End
	private static void Expr2(){
		Expr3();
		Expr2End();
	}

	// Expr2End ::= CompareOp Expr3 | eps ----------------- CompareOp ::= < | <= | > | >=
	private static void Expr2End(){
		if(map.get("CompareOp").contains(sym)){
			scan();
			Expr3();
		}
	}
	// Expr3 ::= Expr4 Expr3End
	private static void Expr3(){
		Expr4();
		Expr3L();
	}
	// Expr3L ::= AddOp Expr4 Expr3L | eps ---------------- AddOp ::= + | -
	private static void Expr3L(){
		if(map.get("AddOp").contains(sym)){
			scan();
			Expr4();
			Expr3L();
		}
	}

	// Expr4 ::= Expr5 Expr4L
	private static void Expr4(){
		Expr5();
		Expr4L();
	}

	// Expr4L ::= MuloOp Expr5 Expr4L | eps ---------------- MuloOp ::= * | / | %
	private static void Expr4L(){
		if(map.get("MuloOp").contains(sym)){
			scan();
			Expr5();
			Expr4L();
		}
	}

	// Expr5 ::= !Expr6 | -Expr6 | Expr6
	private static void Expr5(){
		if(sym == not_ || sym == minus_){
			scan();
		}
		Expr6();
	}

	private static void Expr5L(){
		if(map.get("NegationOp").contains(sym)){
			scan();
			Expr6();
			Expr5L();
		}
	}

	private static void Expr6(){
		if (map.get("Constant").contains(sym)) {
			scan();
		} else if (sym == identifier_)
			scan();
		else if (sym == leftPar_) {
			scan();
			Expr();
			check(rightPar_);
		} else if (map.get("ReadOperations").contains(sym)) {
			scan();
		}
		else {
			error("Expression expected");
			scan();
		}
	}



	//TODO  // add parsing methods for all productions

	public static void parse() {
		map = new HashMap<>();
		map.put("Declarations", new ArrayList<>(Arrays.asList(integer_, boolean_, string_, double_)));
		map.put("Type", new ArrayList<>(Arrays.asList(integer_, boolean_, string_, double_)));
		map.put("CommandSequence", new ArrayList<>(Arrays.asList(if_, while_, for_, break_, print_, identifier_,
				integerConst_, booleanConst_, stringConst_, doubleConst_, leftPar_, readInt_, readString_,
				readDouble_, readBoolean_)));
		map.put("Expr", new ArrayList<>(Arrays.asList(identifier_, integerConst_, booleanConst_,
				stringConst_, doubleConst_, leftPar_, readInt_, readString_, readDouble_, readBoolean_)));
		map.put("LogicalOp", new ArrayList<>(Arrays.asList(and_, or_)));
		map.put("EqualityOp", new ArrayList<>(Arrays.asList(eql_, neq_)));
		map.put("CompareOp", new ArrayList<>(Arrays.asList(less_, leq_, gtr_, geq_)));
		map.put("AddOp", new ArrayList<>(Arrays.asList(plus_, minus_)));
		map.put("MuloOp", new ArrayList<>(Arrays.asList(mult_, div_, mod_)));
		map.put("NegationOp", new ArrayList<>(Arrays.asList(not_, minus_)));
		map.put("Constant", new ArrayList<>(Arrays.asList(integerConst_, booleanConst_, stringConst_, doubleConst_)));
		map.put("ReadOperations", new ArrayList<>(Arrays.asList(readInt_, readString_, readBoolean_, readDouble_)));
		// start parsing
		errors = 0; errDist = 3;
		scan();
		Program();
		if (sym != eof_) error("end of file found before end of program");
	}

}








