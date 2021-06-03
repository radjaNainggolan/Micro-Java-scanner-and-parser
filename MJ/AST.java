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
            return "{ declarations=\n" + declarations + "}"+"\n { commandSequence=" + commandSequence + "}";
        }

    }

    public static class Declarations{
        LinkedList<Declarations> declarationsList;

        @Override
        public String toString() {
            String text = "";
            for(int i = 0; i < declarationsList.size(); i++) {
                text = text + " " + declarationsList.get(i).toString() + "\n";
            }
            return "{ declarationsList=\n" + text + "}";
        }
    }

    public static class Declaration{
        Type type;
        Identifier id;
        @Override
        public String toString() {
            return "Declaration { type=" + type + ", id=" + id + " }";
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
            return "\n{ commandList=\n" + text + " }";
        }
    }

    public static class Command{
        Command command;
        @Override
        public String toString() {
            return "Command { command=" + command + "}";
        }
    }

    public static class AssignCommand extends Command{
        Identifier id;
        Expression expression;
        @Override
        public String toString() {
            return "AssignCommand { id=" + id + ", expression=" + expression + " }";
        }
    }

    public static class IfCommand extends Command{
        Expression expression;
        CommandSequence ifCommandSequence;
        CommandSequence elseCommandSequence;
        @Override
        public String toString() {
            return "IfCommand { expression=" + expression + ", ifCommandSequence=" + ifCommandSequence
                    + ", elseCommandSequence=" + elseCommandSequence + " }";
        }
    }
    public static class WhileCommand extends Command{
        Expression expression;
        CommandSequence whileCommandSequence;
        @Override
        public String toString() {
            return "WhileCommand [expression=" + expression + ", whileCommandSequence=" + whileCommandSequence + "]";
        }
    }

    public static class ForCommand extends Command{
        Expression expression1;
        Expression expression2;
        Expression expression3;
        CommandSequence forCommandSequence;
        @Override
        public String toString() {
            return "ForCommand { expression1=" + expression1 + ", expression2=" + expression2
                    + ", expression3=" + expression3 + ", forCommandSequence=" + forCommandSequence + " }";
        }
    }


    public static class BreakCommand extends Command{
        boolean inLoop;
        @Override
        public String toString() {
            return "BreakCommand { inLoop=" + inLoop + " }";
        }
    }

    public static class ReadCommand extends Command{
        Type readType;
        public ReadCommand(Type t) {
            this.readType = t;
        }
        @Override
        public String toString() {
            return "ReadCommand { readType=" + readType.toString() + " }";
        }

    }

    public static class WriteCommand extends Command{
        Expression printExpression;
        @Override
        public String toString() {
            return "PrintCommand { printExpression=" + printExpression + " }";
        }
    }

    public static class Expression{
        Expression expression;
        @Override
        public String toString() {
            return "Expression { expression=" + "expression" + " }";
        }

    }

    public static class BinaryOperatorExpression extends Expression{
        Expression expression1;
        String binaryOperator;
        Expression expression2;
        @Override
        public String toString() {
            return "BinaryOperatorExpression { firstExpression=" + expression1 + ", binaryOperator=" + binaryOperator
                    + ", secondExpression=" + expression2 + " }";
        }

    }

    public static class UnaryOperatorExpression extends Expression{
        String unaryOperator;
        Expression expression;
        @Override
        public String toString() {
            return "UnaryOperatorExpression  { unaryOperator=" + unaryOperator + ", expression=" + expression + " }";
        }

    }

    public static class ReadExpression extends Expression{
        Type readType;
        public ReadExpression(Type t) {
            this.readType = t;
        }
        @Override
        public String toString() {
            return "ReadExpression { readType=" + readType.toString() + " }";
        }
    }

    public static class Constant extends Expression{
        Type constType;
        public Constant(Type t) {
            this.constType = t;
        }
        @Override
        public String toString() {
            return "Constant { constType=" + constType.toString() + " }";
        }
    }

    public static class Type {
        String type;
        public Type(String t) {
            this.type = new String(t);
        }
        @Override
        public String toString() {
            return "{ type=" + type + " }";
        }

    }

    public static class Identifier extends Expression{
        String name;
        public Identifier(String n) {
            this.name = n;
        }
        @Override
        public String toString() {
            return "{ name=" + name + " }";
        }


    }

}
