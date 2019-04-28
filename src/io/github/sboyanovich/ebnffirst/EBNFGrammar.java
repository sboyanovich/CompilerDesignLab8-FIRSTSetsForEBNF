package io.github.sboyanovich.ebnffirst;

import java.util.*;
import java.util.function.Function;

public class EBNFGrammar {
    private int terminalAlphabetSize;
    private int nonTerminalAlphabetSize;

    private Map<Integer, EBNF> rules;

    private Function<Integer, String> nativeTai;
    private Function<Integer, String> nativeNTai;

    EBNFGrammar(
            int terminalAlphabetSize,
            int nonTerminalAlphabetSize,
            Map<Integer, EBNF> rules,
            Function<Integer, String> nativeTai,
            Function<Integer, String> nativeNTai
    ) {
        this.terminalAlphabetSize = terminalAlphabetSize;
        this.nonTerminalAlphabetSize = nonTerminalAlphabetSize;
        this.rules = rules;
        this.nativeTai = nativeTai;
        this.nativeNTai = nativeNTai;
    }

    public EBNFGrammar(int terminalAlphabetSize, int nonTerminalAlphabetSize, Map<Integer, EBNF> rules) {
        this(terminalAlphabetSize, nonTerminalAlphabetSize, rules, Objects::toString, Objects::toString);
    }

    public Function<Integer, String> getNativeTai() {
        return nativeTai;
    }

    public Function<Integer, String> getNativeNTai() {
        return nativeNTai;
    }

    public int getTerminalAlphabetSize() {
        return terminalAlphabetSize;
    }

    public int getNonTerminalAlphabetSize() {
        return nonTerminalAlphabetSize;
    }

    public Map<Integer, EBNF> getRules() {
        return rules;
    }

    private Map<Integer, Set<Integer>> firstSetsAux() {
        Map<Integer, Set<Integer>> ntsFirst = new HashMap<>();

        for (int i = 0; i < this.nonTerminalAlphabetSize; i++) {
            ntsFirst.put(i, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < this.nonTerminalAlphabetSize; i++) {
                Set<Integer> ntFirst = ntsFirst.get(i);
                changed |= ntFirst.addAll(this.rules.get(i).first(ntsFirst));
            }
        } while (changed);

        return ntsFirst;
    }

    Map<String, Set<String>> firstSets(String epsilon) {
        Map<String, Set<String>> result = new HashMap<>();

        Map<Integer, Set<Integer>> aux = firstSetsAux();

        for (int nt : aux.keySet()) {
            String ntName = nativeNTai.apply(nt);
            Set<String> terminals = new HashSet<>();

            Set<Integer> first = aux.get(nt);

            for (int firstSetElement : first) {
                if (firstSetElement == EBNF.EPSILON) {
                    terminals.add(epsilon);
                } else {
                    terminals.add(nativeTai.apply(firstSetElement));
                }
            }
            result.put(ntName, terminals);
        }

        return result;
    }

}
