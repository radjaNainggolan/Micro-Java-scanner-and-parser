package MJ;

import java.util.LinkedList;

public class AST {

    Program program;
    @Override
    public String toString() {
        return "AST ::= { program=\n" + program + "}";
    }
    public static class Program{
        Declarations declarations;
        CommandSequence commandSequence;
        @Override
        public String toString() {
            return "{ declarations ::=\n" + declarations + "}"+"\n { commandSequence ::=" + commandSequence + "}";
        }

    }

    public static class Declarations{
        LinkedList<Declaration> declarationsList;

        @Override
        public String toString() {
            String text = "";
            for(int i = 0; i < declarationsList.size(); i++) {
                text = text + " " + declarationsList.get(i).toString() + "\n";
            }
            return "{ declarationsList ::=\n" + text + "}";
        }
    }

    public static class Declaration{
        Type type;
        Identifier id;
        @Override
        public String toString() {
            return "\tDeclaration { type ::=" + type + ", id ::=" + id + " }";
        }
    }

    public static class CommandSequence{
        LinkedList<Command> commandsList;
        @Override
        public String toString() {
            String text = "";
            for(int i = 0; i < commandsList.size(); i++) {
                text = text + " " +commandsList.get(i) + "\n";
            }
            return "{ commandList ::=\n"  + text  + " }\n";
        }
    }

    public static class Command{
        Command command;
        @Override
        public String toString() {
            return "\tCommand { command ::= " + command + "}\n";
        }
    }

    public static class AssignCommand extends Command{
        Identifier id;
        Expression expression;
        @Override
        public String toString() {
            return "\tAssignCommand { id ::=" + id + ", expression ::=" + expression + " }";
        }
    }

    public static class IfCommand extends Command{
        Expression expression;
        CommandSequence ifCommandSequence;
        CommandSequence elseCommandSequence;
        @Override
        public String toString() {
            return "\tIfCommand { expression ::=" + expression + ", \n\tifCommandSequence ::=" + ifCommandSequence + ", \n\t{ elseCommandSequence ::=" + elseCommandSequence + " }\n";
        }
    }

    public static class SwitchCommand extends Command{
        Expression expression;
        LinkedList<Case> cs;
        @Override
        public String toString(){
            String text = "";
            for(int i = 0; i < cs.size(); i++) {
                text = text + " " +cs.get(i) + "\n";
            }
            return "\tSwitchCommand { Expression ::= "+ expression+ " { Cases ::=" +text + " }" +  " }\n";
        }
    }

    public static class Case extends Command{
        Expression expression;
        CommandSequence cmd;
        @Override
        public String toString(){
            return "\t{ Expression ::= "+ expression + "{ CommandSequence ::= "+ cmd+" }\n";
        }
    }

    public static class WhileCommand extends Command{
        Expression expression;
        CommandSequence whileCommandSequence;
        @Override
        public String toString() {
            return "\tWhileCommand { expression ::=" + expression + ", whileCommandSequence ::=" + whileCommandSequence + "\n" + " }";
        }
    }

    public static class DoWhileCommand extends Command{
        Expression expression1;
        Expression expression2;
        Expression expression3;
        CommandSequence doWhileCommandSequence;
        @Override
        public String toString(){
            return "\tDoWhileCommand { expression1 ::= "+ expression1  +" , expression2 ::= "+expression2 + " , expression3 ::= " +expression3 + " , doWhileCommandSequence ::= " + doWhileCommandSequence +"\n" +" }";
        }

    }

    public static class ShiftLeft extends Expression{
        Expression id;
        Expression int_;
        String operator;
        @Override
        public String toString(){
            return "\tShiftLeft { Identifier ::= "+id+" , IntegerConstant ::= "+int_+" }\n";
        }


    }



    public static class ForCommand extends Command{
        Expression expression1;
        Expression expression2;
        Expression expression3;
        CommandSequence forCommandSequence;
        @Override
        public String toString() {
            return "\tForCommand { expression1 ::=" + expression1 + ", expression2 ::=" + expression2
                    + ", expression3 ::=" + expression3 + ", forCommandSequence ::=" + forCommandSequence + " }";
        }
    }


    public static class BreakCommand extends Command{
        boolean inLoop;
        @Override
        public String toString() {
            return " \tBreakCommand { inLoop ::=" + inLoop + " }";
        }
    }

    public static class ReadCommand extends Command{
        Type readType;
        public ReadCommand(Type t) {
            this.readType = t;
        }
        @Override
        public String toString() {
            return "\tReadCommand { readType ::=" + readType.toString() + " }";
        }

    }

    public static class WriteCommand extends Command{
        Expression writeExpression;
        @Override
        public String toString() {
            return "\tPrintCommand { writeExpression ::=" + writeExpression + " }";
        }
    }

    public static class Expression{
        Expression expression;
        @Override
        public String toString() {
            return "(" + "expression" + ")";
        }

    }

    public static class QuestOperator extends  Expression{
        Expression questExpression;
        Expression expression1;
        Expression expression2;
        @Override
        public String toString() {
            return " ( QuestOperator ::= { expression ::="+ questExpression + " ?" +" expression1 ::=" + expression1 + " expression2 ::= "+ expression2 +" }"+ " )";
        }

    }

    public static class BinaryOperatorExpression extends Expression{
        Expression expression1;
        String binaryOperator;
        Expression expression2;
        @Override
        public String toString() {
            return "(" + expression1 + " " + binaryOperator
                    + " " + expression2 + " )";
        }

    }

    public static class UnaryOperatorExpression extends Expression{
        String unaryOperator;
        Expression expression;
        @Override
        public String toString() {
            return "UnaryOperatorExpression  { unaryOperator ::=" + unaryOperator + ", expression ::=" + expression + " }";
        }

    }

    public static class ReadExpression extends Expression{
        Type readType;
        public ReadExpression(Type t) {
            this.readType = t;
        }
        @Override
        public String toString() {
            return "ReadExpression { readType ::=" + readType.toString() + " }";
        }
    }

    public static class Constant extends Expression{
        Type constType;
        public Constant(Type t) {
            this.constType = t;
        }
        @Override
        public String toString() {
            return "Constant" ;//+// constType.toString() + " }";
        }
    }

    public static class Type {
        String type;
        public Type(String t) {
            this.type = new String(t);
        }
        @Override
        public String toString() {
            return "{ type ::=" + type + " }";
        }

    }

    public static class Identifier extends Expression{
        String name;
        public Identifier(String n) {
            this.name = n;
        }
        @Override
        public String toString() {
            return "{ name ::=" + name + " }";
        }


    }

}
