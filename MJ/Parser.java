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
			eof_ = 47, // end of file
			do_ = 48,
			quest_ = 49,
			exp_ = 50,
			twoDots = 51,
			switch_ = 52,
			case_ = 53,
			default_ = 54,
			sl_ = 55;

	private static final String[] name = { // token names for error messages
			"none", "LET", "IN", "END", "IF", "FI", "ELSE", "WHILE", "FOR", "BREAK",
			"PRINT", "THEN", "READINT()", "READSTRING()", "READBOOLEAN()", "READDOUBLE()",
			"identifier", "integer", "string", "boolean", "double", "+", "-", "*", "/", "%",
			"<", "<=", ">", ">=", "==", "!=", "=", "&&", "||", "!", ";", ",", ".", "(", ")",
			"integer constant", "string constant", "boolean constant", "double constant", "{", "}", "^", "DO", "QUEST", "^", ":"

	};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// lookahead token
	private static int sym;			// always contains la.kind
	public  static int errors;  	// error counter
	private static int errDist;		// no. of correctly recognized tokens since last error
	private static AST ast;
	private static int inLoop = 0;
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
	private static AST.Program Program() {
		AST.Program prog = new AST.Program();
		check(let_);
		prog.declarations =  Declarations();
		check(in_);
		prog.commandSequence = CommandSequence();
		check(end_);
		return prog;
	}

	//Declarations ::= Decl+
	private static AST.Declarations Declarations(){
		AST.Declarations decls = new AST.Declarations();
		decls.declarationsList = new LinkedList<AST.Declaration>();
		while (map.get("Declarations").contains(sym)){
			decls.declarationsList.add(Decl());
		}
		return decls;
	}

	//Decl ::= Type ident
	private static AST.Declaration Decl(){
		AST.Declaration decl = new AST.Declaration();
		decl.type =  Type();
		check(identifier_);
		decl.id = new AST.Identifier(t.string);
		check(semiColon_);
		return decl;
	}

	// Type ::= integer | string | boolean | double
	private static AST.Type Type(){

		if(sym == integer_) {
			scan();
			return new AST.Type("integer");
		}else if(sym == string_){
			scan();
			return new AST.Type("string");
		}else if(sym == boolean_){
			scan();
			return new AST.Type("boolean");
		}else if(sym == double_){
			scan();
			return new AST.Type("double");
		}else {
			error("Invalid varibale type (integer, srting, boolean, double  expected)");
			return null;
		}
	}

	// CommandSequence ::= { Stmt+ }
	private static AST.CommandSequence CommandSequence(){
		AST.CommandSequence cs = new AST.CommandSequence();
		cs.commandsList = new LinkedList<AST.Command>();
		check(leftCurlyPar_);
		while(map.get("CommandSequence").contains(sym)){
			cs.commandsList.add(Stmt());
		}
		check(rightCurlyPar_);
		return cs;
	}

	//Stmt ::= IfStmt | WhileStmt | ForStmt | BreakStmt | PrintStmt | AssignExpr; | CommandSequence
	private static AST.Command Stmt(){
		AST.Command cmd;
		if(sym == if_){
			cmd = IfStmt();
		}else if(sym == while_){
			cmd = WhileStmt();
		}else if(sym == do_){
			cmd = DoWhileStmt();
		}else if(sym == switch_){
			cmd = SwitchStmt();
		}
		else if(sym == for_){
			cmd = ForStmt();
		}else if(sym == break_){
			cmd = BreakStmt();
		}else if(sym == print_){
			cmd = PrintStmt();
		}else if(sym == identifier_){
			cmd = AssignStmt();

			check(semiColon_);
		}/*else if(map.get("Expr").contains((sym))){
			Expr();
			check(semiColon_);
		}*/else{
			error("Statement error");
			cmd = null;
		}
		return cmd;
	}

	// IfStmt ::= IF (Expr) CommandSequence IfStmtEnd
	private static AST.IfCommand IfStmt(){
		AST.IfCommand ifCmd = new AST.IfCommand();
		check(if_);
		check(leftPar_);
		ifCmd.expression =  Expr();
		check(rightPar_);
		ifCmd.ifCommandSequence = CommandSequence();
		if(sym == else_){
			scan();
			ifCmd.elseCommandSequence = CommandSequence();
			check(fi_);
		}else if(sym == fi_){
			scan();
		}else {
			error("Invalid end for IF statement");
			return  null;
		}
		return ifCmd;
	}

	private static AST.SwitchCommand SwitchStmt(){
		AST.SwitchCommand sc = new AST.SwitchCommand();
		check(switch_);
		check(leftPar_);
		sc.expression = Expr6();
		check(rightPar_);
		check(leftCurlyPar_);

		sc.cs = Case(new LinkedList<AST.Case>());

		check(rightCurlyPar_);
		return sc;
	}

	private static LinkedList<AST.Case> Case(LinkedList<AST.Case> l){

		if(sym == default_){
			check(default_);
			check(twoDots);
			check(leftCurlyPar_);
			AST.Case c = new AST.Case();
			c.cmd = CommandSequence();
			check(break_);
			check(semiColon_);
			check(rightCurlyPar_);
			l.add(c);
			return l;
		}

		AST.Case c = new AST.Case();
		check(case_);
		c.expression = Expr6();
		check(twoDots);
		check(leftCurlyPar_);
		c.cmd = CommandSequence();
		check(break_);
		check(semiColon_);
		check(rightCurlyPar_);
		l.add(c);
		return Case(l);

	}


	// WhileStmt ::= While (Expr) CommandSequence
	private static AST.WhileCommand WhileStmt(){
		AST.WhileCommand wcmd = new AST.WhileCommand();
		check(while_);
		check(leftPar_);
		wcmd.expression =  Expr();
		check(rightPar_);
		inLoop++;
		wcmd.whileCommandSequence =  CommandSequence();
		inLoop--;
		return wcmd;
	}

	private  static  AST.DoWhileCommand DoWhileStmt(){
		AST.DoWhileCommand dwcmd = new AST.DoWhileCommand();
		check(do_);
		inLoop++;
		dwcmd.doWhileCommandSequence = CommandSequence();
		inLoop--;
		check(while_);
		check(leftPar_);
		dwcmd.expression1 = AssignExpr();
		check(semiColon_);
		dwcmd.expression2 = Expr();
		check(semiColon_);
		dwcmd.expression3 = AssignExpr();
		check(semiColon_);
		check(rightPar_);
		check(semiColon_);
		return dwcmd;

	}



	//ForStmt ::= FOR (Expr; Expr; Expr) CommandSequence
	private static AST.ForCommand ForStmt(){
		AST.ForCommand fcmd = new AST.ForCommand();
		check(for_);
		check(leftPar_);
		fcmd.expression1 =  AssignExpr();
		check(semiColon_);
		fcmd.expression2 = Expr();
		check(semiColon_);
		fcmd.expression3 = AssignExpr();
		check(rightPar_);
		inLoop++;
		fcmd.forCommandSequence = CommandSequence();
		inLoop--;
		return  fcmd;
	}

	// BreaksStmt ::= BREAK;
	private static AST.BreakCommand BreakStmt(){
		AST.BreakCommand brk = new AST.BreakCommand();
		if(inLoop > 0){
			check(break_);
			check(semiColon_);
			brk.inLoop = true;
		}else{
			error("Break statement can not be called outside of loop");
			brk.inLoop = false;
		}
		return brk;
	}

	//PrintStmt ::= Print(Expr)
	private static AST.WriteCommand PrintStmt(){
		AST.WriteCommand  wrt = new AST.WriteCommand();
		check(print_);
		check(leftPar_);
		wrt.writeExpression = Expr();
		check(rightPar_);
		check(semiColon_);
		return  wrt;
	}

	// AssignExpr ::= ident = Expr

	private static AST.Expression AssignExpr(){
		check(identifier_);
		check(assign_);
		return Expr();

	}
	private static AST.Command AssignStmt() {
		AST.AssignCommand acmd = new AST.AssignCommand();
		acmd.id = new AST.Identifier(la.string);
		acmd.expression = AssignExpr();
		if(sym == quest_) {
			AST.QuestOperator temp = new AST.QuestOperator();
			temp.questExpression = acmd.expression;
			check(quest_);
			temp.expression1 = Expr();
			check(twoDots);
			temp.expression2 = Expr();
			acmd.expression = temp;
		}
		return acmd;
	}




	// Expr ::= Expr1 ExprL
	private static AST.Expression Expr(){
		AST.Expression e2 = Expr1();
		if(e2 == null) return null;
		String operator = "";
		if(sym == and_) operator = "And";
		if(sym == or_) operator = "Or";
		AST.Expression e1 = ExprL();
		if(e1 == null) return e2;
		AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
		temp.expression1 = e2;
		temp.expression2 = e1;
		temp.binaryOperator = new String(operator);
		return temp;
	}
	// ExprL ::= LogicalOp Expr1 ExprL | eps
	private static AST.Expression ExprL(){
		if(map.get("LogicalOp").contains(sym)){
			scan();
			AST.Expression e2 = Expr1();
			if(e2 == null) return null;
			String operator = "";
			if(sym == and_) operator = "And";
			if(sym == or_) operator = "Or";
			AST.Expression e1 = ExprL();
			if(e1 == null) return e2;
			AST.BinaryOperatorExpression temp = new AST.BinaryOperatorExpression();
			temp.expression1 = e2;
			temp.expression2 = e1;
			temp.binaryOperator = new String(operator);
			return temp;
		} else {
			return null;
		}

	}

	// Expr1 ::= Expr2 Expr1End
	private static AST.Expression Expr1(){
		AST.Expression e2 = Expr2();
		if(e2 == null){
			return null;
		}
		String operator = "";
		if(sym == eql_){
			operator = "==";
		}
		if(sym == neq_){
			operator = "!=";
		}
		AST.Expression e1 =  Expr1End();
		if(e1 == null){
			return e2;
		}
		AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
		binExpr.expression1 = e2;
		binExpr.expression2 = e1;
		binExpr.binaryOperator = new String(operator);
		return binExpr;

	}

	// Expr1End ::= EqualityOp Expr2 | eps ------------- EqualityOp ::= == | !=
	private static AST.Expression Expr1End(){
		if(map.get("EqualityOp").contains(sym)){
			scan();
			return Expr2();
		}else {
			return null;
		}
	}
	// Expr2 ::= Expr3 Expr2End
	private static AST.Expression Expr2(){
		AST.Expression e2 = Expr3();
		if(e2 == null){
			return null;
		}
		String operator = "";
		if (sym == less_) operator="<";
		if (sym == leq_) operator="<=";
		if (sym == gtr_) operator=">";
		if (sym == geq_) operator=">=";

		AST.Expression e1 = Expr2End();
		if(e1 == null){
			return e2;
		}

		AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
		binExpr.expression1 = e2;
		binExpr.expression2 = e1;
		binExpr.binaryOperator = new String(operator);
		return binExpr;
	}

	// Expr2End ::= CompareOp Expr3 | eps ----------------- CompareOp ::= < | <= | > | >=
	private static AST.Expression Expr2End(){
		if(map.get("CompareOp").contains(sym)){
			scan();
			return Expr3();
		}else{
			return null;
		}
	}
	// Expr3 ::= Expr4 Expr3End
	private static AST.Expression Expr3(){
		AST.Expression e2 =  Expr4();
		if(e2 == null){
			return null;
		}

		String operator = "";
		if(sym == plus_) operator="+";
		if(sym == minus_) operator="-";

		AST.Expression e1 = Expr3L();
		if(e1 == null){
			return e2;
		}

		AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
		binExpr.expression1 = e2;
		binExpr.expression2 = e1;
		binExpr.binaryOperator = new String(operator);
		return  binExpr;

	}
	// Expr3L ::= AddOp Expr4 Expr3L | eps ---------------- AddOp ::= + | -
	private static AST.Expression Expr3L(){
		if(map.get("AddOp").contains(sym)){
			scan();
			AST.Expression e2 =  Expr4();
			if(e2 == null){
				return null;
			}

			String operator = "";
			if(sym == plus_) operator="+";
			if(sym == minus_) operator="-";

			AST.Expression e1 = Expr3L();
			if(e1 == null){
				return e2;
			}

			AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
			binExpr.expression1 = e2;
			binExpr.expression2 = e1;
			binExpr.binaryOperator = new String(operator);
			return  binExpr;
		}else{
			return null;
		}
	}

	// Expr4 ::= Expr5 Expr4L
	private static AST.Expression Expr4(){
		AST.Expression e2 = Expr5();
		if(e2 == null) return null;

		String operator = "";
		if(sym == mult_) operator="*";
		else if(sym == mod_) operator="%";
		else if(sym == div_) operator="/";
		AST.Expression e1 = Expr4L();
		if(e1 == null) return e2;

		AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
		binExpr.expression1 = e2;
		binExpr.expression2 = e1;
		binExpr.binaryOperator = new String(operator);

		return binExpr;

	}

	// Expr4L ::= MuloOp Expr5 Expr4L | eps ---------------- MuloOp ::= * | / | %
	private static AST.Expression Expr4L(){
		if(map.get("MuloOp").contains(sym)){

			scan();
			AST.Expression e2 = Expr5();
			if(e2 == null) return null;

			String operator = "";
			if(sym == mult_) operator="*";
			if(sym == mod_) operator="%";
			if(sym == div_) operator="/";

			AST.Expression e1 = Expr4L();
			if(e1 == null) return e2;

			AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
			binExpr.expression1 = e2;
			binExpr.expression2 = e1;
			binExpr.binaryOperator = new String(operator);

			return binExpr;

		}else{
			return null;
		}
	}

	// Expr5 ::= !Expr6 | -Expr6 | Expr6
	private static AST.Expression Expr5(){
		if(sym == not_ || sym == minus_){
			scan();
			AST.UnaryOperatorExpression e = new AST.UnaryOperatorExpression();
			if(t.kind == not_) e.unaryOperator = "!";
			else e.unaryOperator = "-";
			e.expression = Exp();

			return e;
		}else return Exp();
	}
	private static AST.Expression Exp(){
		AST.Expression e2 = Expr6();
		if(e2 == null) return null;

		String operator = "";
		if(sym == exp_) {
			operator="^";
		}
		AST.Expression e1 = ExpL();
		if(e1 == null) return e2;

		AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
		binExpr.expression1 = e2;
		binExpr.expression2 = e1;
		binExpr.binaryOperator = new String(operator);

		return binExpr;

	}

	private static AST.Expression ExpL(){
		if(sym == exp_) {
			scan();
			AST.Expression e2 = Expr6();
			if(e2 == null) return null;

			String operator = "";
			if(sym == exp_) operator="^";
			AST.Expression e1 = ExpL();
			if(e1 == null) return e2;
			AST.BinaryOperatorExpression binExpr = new AST.BinaryOperatorExpression();
			binExpr.expression1 = e2;
			binExpr.expression2 = e1;
			binExpr.binaryOperator = new String(operator);

			return binExpr;

		}else return null;
	}
	/*private static void Expr5L(){
		if(map.get("NegationOp").contains(sym)){
			scan();
			Expr6();
			Expr5L();
		}
	}*/

	private static AST.Expression Expr6(){
		AST.Expression expr;
		if (map.get("Constant").contains(sym)) {
			scan();
			AST.Type typ;

			if(t.kind == integerConst_) typ = new AST.Type("IntegerConst");
			else if(t.kind == doubleConst_) typ = new AST.Type("DoubleConst");
			else if(t.kind == booleanConst_) typ = new AST.Type("BooleanConst");
			else typ = new AST.Type("StringConst");

			expr = new AST.Constant(typ);
		} else if (sym == identifier_) {
			scan();
			expr = new AST.Identifier(t.string);
		}else if (sym == leftPar_) {
			scan();
			expr = Expr();
			check(rightPar_);
		} else if (map.get("ReadOperations").contains(sym)) {
			scan();
			AST.Type typ;

			if(t.kind == readInt_) typ = new AST.Type("ReadInteger");
			else if(t.kind == readDouble_) typ = new AST.Type("ReadDouble");
			else if(t.kind == readBoolean_) typ = new AST.Type("ReadBoolean");
			else typ = new AST.Type("ReadString");

			expr = new AST.ReadExpression(typ);
		} else {
			error("Expression expected");
			scan();
			expr = null;
		}

		return expr;
	}



	//TODO  // add parsing methods for all productions

	public static void parse() {
		map = new HashMap<>();
		map.put("Declarations", new ArrayList<>(Arrays.asList(integer_, boolean_, string_, double_)));
		map.put("Type", new ArrayList<>(Arrays.asList(integer_, boolean_, string_, double_)));
		map.put("CommandSequence", new ArrayList<>(Arrays.asList(if_, while_, for_, break_, print_, identifier_,
				integerConst_, booleanConst_, stringConst_, doubleConst_, leftPar_, readInt_, readString_,
				readDouble_, readBoolean_, do_ , switch_ ,case_,default_)));
		map.put("Expr", new ArrayList<>(Arrays.asList(identifier_, integerConst_, booleanConst_,
				stringConst_, doubleConst_, leftPar_, readInt_, readString_, readDouble_, readBoolean_)));
		map.put("LogicalOp", new ArrayList<>(Arrays.asList(and_, or_ )));
		map.put("EqualityOp", new ArrayList<>(Arrays.asList(eql_, neq_)));
		map.put("CompareOp", new ArrayList<>(Arrays.asList(less_, leq_, gtr_, geq_)));
		map.put("AddOp", new ArrayList<>(Arrays.asList(plus_, minus_)));
		map.put("MuloOp", new ArrayList<>(Arrays.asList(mult_, div_, mod_)));
		map.put("NegationOp", new ArrayList<>(Arrays.asList(not_, minus_)));
		map.put("Constant", new ArrayList<>(Arrays.asList(integerConst_, booleanConst_, stringConst_, doubleConst_)));
		map.put("ReadOperations", new ArrayList<>(Arrays.asList(readInt_, readString_, readBoolean_, readDouble_)));
		// start parsing
		ast = new AST();
		errors = 0; errDist = 3;
		scan();
		ast.program = Program();
		System.out.println(ast);
		if (sym != eof_) error("end of file found before end of program");
	}

}








