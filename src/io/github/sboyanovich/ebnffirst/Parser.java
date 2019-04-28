package io.github.sboyanovich.ebnffirst;

import io.github.sboyanovich.scannergenerator.scanner.token.Domain;
import io.github.sboyanovich.scannergenerator.scanner.token.Token;
import io.github.sboyanovich.scannergenerator.scanner.token.TokenWithAttribute;

import java.util.*;
import java.util.function.Function;

import static io.github.sboyanovich.ebnffirst.Utility.inverseMap;
import static io.github.sboyanovich.ebnffirst.data.DomainsWithStringAttribute.NON_TERMINAL;
import static io.github.sboyanovich.ebnffirst.data.DomainsWithStringAttribute.TERMINAL;
import static io.github.sboyanovich.ebnffirst.data.SimpleDomains.*;

class Parser {
    // FIRST NON_TERMINAL IS AXIOM

    private static Map<String, Set<Domain>> first;

    // not all of this are really necessary
    static {
        first = new HashMap<>();

        first.put("(Lang)", Set.of(NON_TERMINAL));
        first.put("(Rule)", Set.of(NON_TERMINAL));
        first.put("(E)", Set.of(TERMINAL, NON_TERMINAL, LBRACKET));
        first.put("(C)", Set.of(TERMINAL, NON_TERMINAL, LBRACKET));
        first.put("(F)", Set.of(TERMINAL, NON_TERMINAL, LBRACKET));
        first.put("(L)", Set.of(TERMINAL, NON_TERMINAL, LBRACKET));
    }

    private Token sym;
    private Iterator<Token> tokens;

    private Map<String, Integer> terminalNames;
    private Map<String, Integer> nonTerminalNames;
    private int tCounter;
    private int ntCounter;
    private List<EBNFRule> rulesList;

    private Parser(Iterator<Token> tokens) {
        this.tokens = tokens;
        this.sym = tokens.next();

        this.tCounter = 0;
        this.ntCounter = 0;
        this.terminalNames = new HashMap<>();
        this.nonTerminalNames = new HashMap<>();

        this.rulesList = new ArrayList<>();
    }

    // encapsulating actual object
    // last element in tokens is understood to be END_OF_PROGRAM token
    static EBNFGrammar parse(Iterator<Token> tokens) {
        return new Parser(tokens).parse();
    }

    private EBNFGrammar parse() {
        lang();
        expect(Domain.END_OF_PROGRAM);

        Map<Integer, EBNF> rules = new HashMap<>();

        Map<Integer, String> termInt = inverseMap(this.terminalNames);
        Map<Integer, String> nonTermInt = inverseMap(this.nonTerminalNames);

        Function<Integer, String> nativeTai = termInt::get;
        Function<Integer, String> nativeNtai = nonTermInt::get;

        for (EBNFRule rule : this.rulesList) {
            int nt = rule.getNonTerminal();
            boolean present = rules.containsKey(nt);
            if (!present) {
                rules.put(nt, rule.getRhs());
            } else {
                throw new IllegalStateException(
                        "Rule redefinition encountered for nonterminal " + nativeNtai.apply(nt) + "."
                );
            }
        }

        Set<String> useless = new HashSet<>();
        for (int i = 0; i < this.ntCounter; i++) {
            if (!rules.containsKey(i)) {
                useless.add(nativeNtai.apply(i));
            }
        }

        if (!useless.isEmpty()) {
            throw new IllegalStateException(
                    "There are useless nonterminals: " + useless
            );
        }

        return new EBNFGrammar(this.tCounter, this.ntCounter, rules, nativeTai, nativeNtai);
    }

    private void lang() {
        do {
            rule();
        } while (first.get("(Lang)").contains(sym.getTag()));
    }

    private void rule() {
        Token nt = this.sym;
        expect(NON_TERMINAL);
        TokenWithAttribute ntName = (TokenWithAttribute) nt;
        int nonTerminal = addToNonTerminalNamesTable((String) ntName.getAttribute());

        expect(EQUALS);

        EBNF rhs = e();
        this.rulesList.add(new EBNFRule(nonTerminal, rhs));

        expect(DOT);
    }

    private EBNF e() {
        EBNF c = c();
        while (this.sym.getTag() == VERTICAL_BAR) {
            nextToken();
            EBNF c1 = c();
            c = new EBNF.Alternative(c, c1);
        }
        return c;
    }

    private EBNF c() {
        EBNF f = f();
        while (first.get("(F)").contains(this.sym.getTag())) {
            EBNF f1 = f();
            f = new EBNF.Concatenation(f, f1);
        }
        return f;
    }

    private EBNF f() {
        EBNF l = l();
        if (this.sym.getTag() == POS_ITER) {
            nextToken();
            l = new EBNF.PosIteration(l);
        } else if (this.sym.getTag() == STAR) {
            nextToken();
            l = new EBNF.Iteration(l);
        } else if (this.sym.getTag() == OPTION) {
            nextToken();
            l = new EBNF.Option(l);
        }
        return l;
    }

    private EBNF l() {
        EBNF l;
        if (this.sym.getTag() == TERMINAL) {
            TokenWithAttribute tName = (TokenWithAttribute) this.sym;
            nextToken();
            int t = addToTerminalNamesTable((String) tName.getAttribute());
            l = new EBNF.Terminal(t);
        } else if (this.sym.getTag() == NON_TERMINAL) {
            TokenWithAttribute ntName = (TokenWithAttribute) this.sym;
            nextToken();
            int nt = addToNonTerminalNamesTable((String) ntName.getAttribute());
            l = new EBNF.NonTerminal(nt);
        } else {
            expect(LBRACKET);
            l = e();
            expect(RBRACKET);
        }
        return l;
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

    // adds name to table if absent and returns mapped index
    private int addToNonTerminalNamesTable(String name) {
        if (!this.nonTerminalNames.containsKey(name)) {
            int index = this.ntCounter;
            this.nonTerminalNames.put(name, this.ntCounter++);
            return index;
        }
        return this.nonTerminalNames.get(name);
    }

    // adds name to table if absent and returns mapped index
    private int addToTerminalNamesTable(String name) {
        if (!this.terminalNames.containsKey(name)) {
            int index = this.tCounter;
            this.terminalNames.put(name, this.tCounter++);
            return index;
        }
        return this.terminalNames.get(name);
    }
}
