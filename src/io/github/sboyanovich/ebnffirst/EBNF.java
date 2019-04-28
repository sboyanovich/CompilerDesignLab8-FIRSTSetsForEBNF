package io.github.sboyanovich.ebnffirst;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class EBNF {
    static final int EPSILON = -1;

    public abstract Set<Integer> first(Map<Integer, Set<Integer>> ntFirst);

    // sealing abstract class
    private EBNF() {
    }

    static class Empty extends EBNF {
        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            return Set.of(EPSILON);
        }
    }

    static class Terminal extends EBNF {
        private int symbol;

        Terminal(int symbol) {
            this.symbol = symbol;
        }

        public int getSymbol() {
            return this.symbol;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            return Set.of(this.symbol);
        }
    }

    static class NonTerminal extends EBNF {
        private int symbol;

        NonTerminal(int symbol) {
            this.symbol = symbol;
        }

        public int getSymbol() {
            return this.symbol;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            return new HashSet<>(ntFirst.get(this.symbol));
        }
    }

    static class Alternative extends EBNF {
        private EBNF a;
        private EBNF b;

        Alternative(EBNF a, EBNF b) {
            this.a = a;
            this.b = b;
        }

        public EBNF getA() {
            return this.a;
        }

        public EBNF getB() {
            return this.b;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            Set<Integer> result = new HashSet<>(this.a.first(ntFirst));
            result.addAll(this.b.first(ntFirst));
            return result;
        }
    }

    static class Concatenation extends EBNF {
        private EBNF a;
        private EBNF b;

        Concatenation(EBNF a, EBNF b) {
            this.a = a;
            this.b = b;
        }

        public EBNF getA() {
            return this.a;
        }

        public EBNF getB() {
            return this.b;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            Set<Integer> result = new HashSet<>(this.a.first(ntFirst));
            if (result.contains(EPSILON)) {
                result.remove(EPSILON);
                result.addAll(this.b.first(ntFirst));
            }
            return result;
        }
    }

    static class Iteration extends EBNF {
        private EBNF e;

        Iteration(EBNF e) {
            this.e = e;
        }

        public EBNF getE() {
            return this.e;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            Set<Integer> result = new HashSet<>(this.e.first(ntFirst));
            result.add(EPSILON);
            return result;
        }
    }

    static class PosIteration extends EBNF {
        private EBNF e;

        PosIteration(EBNF e) {
            this.e = e;
        }

        public EBNF getE() {
            return this.e;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            return new HashSet<>(this.e.first(ntFirst));
        }
    }

    static class Option extends EBNF {
        private EBNF e;

        Option(EBNF e) {
            this.e = e;
        }

        public EBNF getE() {
            return this.e;
        }

        @Override
        public Set<Integer> first(Map<Integer, Set<Integer>> ntFirst) {
            Set<Integer> result = new HashSet<>(this.e.first(ntFirst));
            result.add(EPSILON);
            return result;
        }
    }
}
