package io.github.sboyanovich.ebnffirst.experimental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.sboyanovich.scannergenerator.utility.Utility.*;

public abstract class Expression {
    public abstract int evaluate();

    int number;

    abstract StringBuilder dotVisit();

    static StringBuilder labelNode(int number, String label) {
        StringBuilder result = new StringBuilder();
        result.append(TAB).append(number).append(SPACE + "[label=\"").append(label).append("\"]" + NEWLINE);
        return result;
    }

    static StringBuilder edgeString(int from, int to) {
        StringBuilder result = new StringBuilder();
        result.append(TAB)
                .append(from)
                .append(SPACE + DOT_ARROW + SPACE)
                .append(to)
                .append(NEWLINE);
        return result;
    }

    // sealing class
    private Expression() {
    }

    public final int getNumber() {
        return this.number;
    }

    final String toGraphVizDotString() {
        return ("digraph expression {" + NEWLINE +
                TAB + "rankdir=TD;" + NEWLINE) +
                dotVisit() +
                "}" + NEWLINE;
    }

    static class Val extends Expression {
        private int value;

        Val(int value, int number) {
            this.number = number;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public int evaluate() {
            return value;
        }

        @Override
        protected StringBuilder dotVisit() {
            return labelNode(number, String.valueOf(this.value));
        }
    }

    static class Sum extends Expression {
        // guaranteed to have at least two elements
        private List<Expression> operands;

        Sum(List<Expression> operands, int number) {
            this.operands = new ArrayList<>(operands);
            this.number = number;
        }

        public List<Expression> getOperands() {
            return Collections.unmodifiableList(this.operands);
        }

        @Override
        public int evaluate() {
            int result = operands.get(0).evaluate();
            for (int i = 1; i < operands.size(); i++) {
                result += operands.get(i).evaluate();
            }
            return result;
        }

        @Override
        protected StringBuilder dotVisit() {
            StringBuilder result = new StringBuilder();

            result.append(labelNode(this.number, "+"));
            for (Expression operand : operands) {
                result.append(edgeString(this.number, operand.number));
            }

            for (Expression operand : operands) {
                result.append(operand.dotVisit());
            }

            return result;
        }
    }

    static class Product extends Expression {
        // guaranteed to have at least two elements
        private List<Expression> operands;

        Product(List<Expression> operands, int number) {
            this.operands = new ArrayList<>(operands);
            this.number = number;
        }

        public List<Expression> getOperands() {
            return Collections.unmodifiableList(this.operands);
        }

        @Override
        public int evaluate() {
            int result = operands.get(0).evaluate();
            for (int i = 1; i < operands.size(); i++) {
                result *= operands.get(i).evaluate();
            }
            return result;
        }

        @Override
        protected StringBuilder dotVisit() {
            StringBuilder result = new StringBuilder();

            result.append(labelNode(this.number, "*"));
            for (Expression operand : operands) {
                result.append(edgeString(this.number, operand.number));
            }

            for (Expression operand : operands) {
                result.append(operand.dotVisit());
            }

            return result;
        }
    }

    static class Difference extends Expression {
        private Expression a;
        private Expression b;

        Difference(Expression a, Expression b, int number) {
            this.a = a;
            this.b = b;
            this.number = number;
        }

        public Expression getA() {
            return a;
        }

        public Expression getB() {
            return b;
        }

        @Override
        public int evaluate() {
            return a.evaluate() - b.evaluate();
        }

        @Override
        protected StringBuilder dotVisit() {
            StringBuilder result = new StringBuilder();

            result.append(labelNode(this.number, "-"));
            result.append(edgeString(this.number, a.number));
            result.append(edgeString(this.number, b.number));

            result.append(a.dotVisit());
            result.append(b.dotVisit());

            return result;
        }
    }

    static class Quotient extends Expression {
        private Expression a;
        private Expression b;

        Quotient(Expression a, Expression b, int number) {
            this.a = a;
            this.b = b;
            this.number = number;
        }

        public Expression getA() {
            return a;
        }

        public Expression getB() {
            return b;
        }

        @Override
        public int evaluate() {
            return a.evaluate() / b.evaluate();
        }

        @Override
        protected StringBuilder dotVisit() {
            StringBuilder result = new StringBuilder();

            result.append(labelNode(this.number, "/"));
            result.append(edgeString(this.number, a.number));
            result.append(edgeString(this.number, b.number));

            result.append(a.dotVisit());
            result.append(b.dotVisit());

            return result;
        }
    }

    static class Negation extends Expression {
        private Expression e;

        Negation(Expression e, int number) {
            this.e = e;
            this.number = number;
        }

        public Expression getE() {
            return e;
        }

        @Override
        public int evaluate() {
            return -e.evaluate();
        }

        @Override
        protected StringBuilder dotVisit() {
            StringBuilder result = new StringBuilder();

            result.append(labelNode(this.number, "-"));
            result.append(edgeString(this.number, e.number));

            result.append(e.dotVisit());

            return result;
        }
    }
}
