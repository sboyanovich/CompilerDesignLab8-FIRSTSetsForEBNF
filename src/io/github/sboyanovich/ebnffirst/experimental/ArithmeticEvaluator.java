package io.github.sboyanovich.ebnffirst.experimental;

import io.github.sboyanovich.scannergenerator.scanner.token.Domain;
import io.github.sboyanovich.scannergenerator.scanner.token.Token;
import io.github.sboyanovich.scannergenerator.scanner.token.TokenWithAttribute;

import java.util.Iterator;

import static io.github.sboyanovich.ebnffirst.experimental.data.DomainsWithIntegerAttribute.INTEGER_LITERAL;
import static io.github.sboyanovich.ebnffirst.experimental.data.SimpleDomains.*;

class ArithmeticEvaluator {
    private Token sym;
    private Iterator<Token> tokens;

    private ArithmeticEvaluator(Iterator<Token> tokens) {
        this.tokens = tokens;
        this.sym = tokens.next();
    }

    static int evaluate(Iterator<Token> tokens) {
        return new ArithmeticEvaluator(tokens).evaluate();
    }

    private int evaluate() {
        int val = e();
        expect(Domain.END_OF_PROGRAM);
        return val;
    }

    private int e() {
        int res = t();
        while (this.sym.getTag() == PLUS || this.sym.getTag() == MINUS) {
            Domain tag = this.sym.getTag();
            nextToken();
            int t1 = t();
            if (tag == PLUS) {
                res += t1;
            } else {
                res -= t1;
            }
        }
        return res;
    }

    private int t() {
        int res = f();
        while (this.sym.getTag() == MUL || this.sym.getTag() == DIV) {
            Domain tag = this.sym.getTag();
            nextToken();
            int f1 = f();
            if (tag == MUL) {
                res *= f1;
            } else {
                res /= f1;
            }
        }
        return res;
    }

    private int f() {
        if (this.sym.getTag() == INTEGER_LITERAL) {
            TokenWithAttribute leaf = (TokenWithAttribute) this.sym;
            nextToken();
            return (int) leaf.getAttribute();
        } else if (this.sym.getTag() == MINUS) {
            nextToken();
            int f = f();
            return -f;
        } else {
            expect(LPAREN);
            int e = e();
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
