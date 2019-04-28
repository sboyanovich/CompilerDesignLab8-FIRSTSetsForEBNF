package io.github.sboyanovich.ebnffirst;

class EBNFRule {
    private int nonTerminal;
    private EBNF rhs;

    EBNFRule(int nonTerminal, EBNF rhs) {
        this.nonTerminal = nonTerminal;
        this.rhs = rhs;
    }

    int getNonTerminal() {
        return nonTerminal;
    }

    EBNF getRhs() {
        return rhs;
    }
}
