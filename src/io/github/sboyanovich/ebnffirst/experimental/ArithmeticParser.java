package io.github.sboyanovich.ebnffirst.experimental;

import io.github.sboyanovich.scannergenerator.scanner.token.Domain;
import io.github.sboyanovich.scannergenerator.scanner.token.Token;
import io.github.sboyanovich.scannergenerator.scanner.token.TokenWithAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.github.sboyanovich.ebnffirst.experimental.data.DomainsWithIntegerAttribute.INTEGER_LITERAL;
import static io.github.sboyanovich.ebnffirst.experimental.data.SimpleDomains.*;

class ArithmeticParser {
    private Token sym;
    private Iterator<Token> tokens;
    private int nodeNames;

    private ArithmeticParser(Iterator<Token> tokens) {
        this.tokens = tokens;
        this.sym = tokens.next();
        this.nodeNames = 0;
    }

    static Expression parse(Iterator<Token> tokens) {
        return new ArithmeticParser(tokens).parse();
    }

    private Expression parse() {
        Expression expression = e();
        expect(Domain.END_OF_PROGRAM);
        return expression;
    }

    private Expression e() {
        Expression res = t();

        List<Expression> operands;
        while (this.sym.getTag() == PLUS || this.sym.getTag() == MINUS) {
            Domain tag = this.sym.getTag();
            Expression t1;
            if (tag == PLUS) {
                operands = new ArrayList<>();
                operands.add(res);
                do {
                    nextToken();
                    t1 = t();
                    operands.add(t1);
                    tag = this.sym.getTag();
                } while (tag == PLUS);
                res = new Expression.Sum(operands, nodeNames++);
            } else {
                nextToken();
                t1 = t();
                res = new Expression.Difference(res, t1, nodeNames++);
            }
        }
        return res;
    }

    private Expression t() {
        Expression res = f();
        List<Expression> operands;
        while (this.sym.getTag() == MUL || this.sym.getTag() == DIV) {
            Domain tag = this.sym.getTag();
            Expression f1;
            if (tag == MUL) {
                operands = new ArrayList<>();
                operands.add(res);
                do {
                    nextToken();
                    f1 = f();
                    operands.add(f1);
                    tag = this.sym.getTag();
                } while (tag == MUL);
                res = new Expression.Product(operands, nodeNames++);
            } else {
                nextToken();
                f1 = f();
                res = new Expression.Quotient(res, f1, nodeNames++);
            }
        }
        return res;
    }

    private Expression f() {
        if (this.sym.getTag() == INTEGER_LITERAL) {
            TokenWithAttribute leaf = (TokenWithAttribute) this.sym;
            nextToken();
            int val = (int) leaf.getAttribute();
            return new Expression.Val(val, nodeNames++);
        } else if (this.sym.getTag() == MINUS) {
            nextToken();
            Expression f = f();
            return new Expression.Negation(f, nodeNames++);
        } else {
            expect(LPAREN);
            Expression e = e();
            expect(RPAREN);
            return e;
        }
    }

    private void nextToken() {
        if (this.tokens.hasNext()) {
            this.sym = this.tokens.next();
        }
    }

    private void expect(Domain tag) {
        if (this.sym.getTag() == tag) {
            nextToken();
        } else {
            throw new IllegalStateException(
                    "Syntax error at token " + this.sym + ". Expected " + tag + ", got " + this.sym.getTag() + "!");
        }
    }
}
