package io.github.sboyanovich.ebnffirst;

import io.github.sboyanovich.ebnffirst.data.StateTags;
import io.github.sboyanovich.scannergenerator.automata.NFA;
import io.github.sboyanovich.scannergenerator.scanner.LexicalRecognizer;
import io.github.sboyanovich.scannergenerator.scanner.StateTag;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.sboyanovich.ebnffirst.data.CommonCharClasses.alphanumerics;
import static io.github.sboyanovich.ebnffirst.data.CommonCharClasses.letters;
import static io.github.sboyanovich.ebnffirst.data.StateTags.*;
import static io.github.sboyanovich.scannergenerator.utility.Utility.*;

final class Utility {
    //can't instantiate this
    private Utility() {

    }

    static <K, V> Map<V, K> inverseMap(Map<K, V> map) {
        Map<V, K> resultMap = new HashMap<>();
        for (K key : map.keySet()) {
            V val = map.get(key);
            resultMap.put(val, key);
        }
        return resultMap;
    }

    static LexicalRecognizer buildMainTestRecognizer(int alphabetSize) {
        /// LEXICAL STRUCTURE

        /*
        // AUX
            IDENTIFIER      = ([a-z][A-Z])([a-z][A-Z][0-9][_])*
            SPECIAL         = [. | = ( ) \[ \] + * ?]

        // TOKEN TYPES
            TERMINAL        = {IDENTIFIER} | [\*\+-/] | (\\{SPECIAL})
            NON_TERMINAL    = \({IDENTIFIER}\)
            EQUALS          = =
            VERTICAL_BAR    = |
            LBRACKET        = [
            RBRACKET        = ]
            POS_ITER        = +
            STAR            = *
            OPTION          = ?
            DOT             = .
        */

        NFA whitespaceNFA = acceptsAllTheseSymbols(alphabetSize, Set.of(" ", "\t", "\n"))
                .union(acceptThisWord(alphabetSize, List.of("\r", "\n")))
                .positiveIteration()
                .setAllFinalStatesTo(WHITESPACE);

        NFA lettersNFA = acceptsAllTheseSymbols(alphabetSize, letters);
        NFA alphanumericsNFA = acceptsAllTheseSymbols(alphabetSize, alphanumerics);
        NFA underscoreNFA = acceptsAllTheseSymbols(alphabetSize, Set.of("_"));

        NFA identifierNFA = lettersNFA
                .concatenation(alphanumericsNFA.union(underscoreNFA).iteration());

        NFA lparenNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("("));
        NFA rparenNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint(")"));

        NFA nonTerminalNFA = lparenNFA
                .concatenation(identifierNFA)
                .concatenation(rparenNFA)
                .setAllFinalStatesTo(NON_TERMINAL);

        NFA equalsNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("="))
                .setAllFinalStatesTo(EQUALS);

        NFA vbarNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("|"))
                .setAllFinalStatesTo(VERTICAL_BAR);

        NFA dotNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("."))
                .setAllFinalStatesTo(DOT);

        NFA lbracketNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("["))
                .setAllFinalStatesTo(LBRACKET);
        NFA rbracketNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("]"))
                .setAllFinalStatesTo(RBRACKET);

        NFA starNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("*"))
                .setAllFinalStatesTo(STAR);

        NFA posIterNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("+"))
                .setAllFinalStatesTo(POS_ITER);

        NFA optionNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("?"))
                .setAllFinalStatesTo(OPTION);

        NFA specialNFA = acceptsAllTheseSymbols(
                alphabetSize,
                Set.of(".", "|", "=", "(", ")", "[", "]", "+", "*", "?"));

        NFA arithmOpNFA = acceptsAllTheseSymbols(alphabetSize, Set.of("-", "/"))
                .union(acceptThisWord(alphabetSize, List.of("\\", "*")))
                .union(acceptThisWord(alphabetSize, List.of("\\", "+")));
        NFA escapeNFA = acceptsAllTheseSymbols(alphabetSize, Set.of("\\"));

        NFA terminalNFA = identifierNFA
                .union(arithmOpNFA)
                .union(escapeNFA.concatenation(specialNFA))
                .setAllFinalStatesTo(StateTags.TERMINAL);

        NFA lang = whitespaceNFA
                .union(terminalNFA)
                .union(nonTerminalNFA)
                .union(equalsNFA)
                .union(vbarNFA)
                .union(dotNFA)
                .union(lbracketNFA)
                .union(rbracketNFA)
                .union(starNFA)
                .union(posIterNFA)
                .union(optionNFA);

        List<StateTag> priorityList = List.of(
                WHITESPACE,
                TERMINAL,
                NON_TERMINAL,
                DOT,
                VERTICAL_BAR,
                EQUALS,
                LBRACKET,
                RBRACKET,
                STAR,
                POS_ITER,
                OPTION
        );

        Map<StateTag, Integer> priorityMap = new HashMap<>();
        for (int i = 0; i < priorityList.size(); i++) {
            priorityMap.put(priorityList.get(i), i);
        }

        System.out.println("NFA has " + lang.getNumberOfStates() + " states.");

        Instant start = Instant.now();
        LexicalRecognizer recognizer = io.github.sboyanovich.scannergenerator.utility.Utility.createRecognizer(lang, priorityMap);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Recognizer built in " + timeElapsed + "ms!\n");

        return recognizer;
    }
}
