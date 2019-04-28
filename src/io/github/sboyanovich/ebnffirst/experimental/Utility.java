package io.github.sboyanovich.ebnffirst.experimental;

import io.github.sboyanovich.ebnffirst.data.CommonCharClasses;
import io.github.sboyanovich.scannergenerator.automata.NFA;
import io.github.sboyanovich.scannergenerator.scanner.LexicalRecognizer;
import io.github.sboyanovich.scannergenerator.scanner.StateTag;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.sboyanovich.ebnffirst.experimental.data.StateTags.*;
import static io.github.sboyanovich.scannergenerator.utility.Utility.*;

final class Utility {
    //can't instantiate this
    private Utility() {

    }

    static LexicalRecognizer buildArithmeticExpressionRecognizer(int alphabetSize) {
        /// LEXICAL STRUCTURE

        /*
        // TOKEN TYPES
            INTEGER_LITERAL     = [0-9]+
            LPAREN              = \(
            RPAREN              = \)
            MUL                 = [*]
            PLUS                = [+]
            MINUS               = [-]
            DIV                 = [/]
        */

        NFA whitespaceNFA = acceptsAllTheseSymbols(alphabetSize, Set.of(" ", "\t", "\n"))
                .union(acceptThisWord(alphabetSize, List.of("\r", "\n")))
                .positiveIteration()
                .setAllFinalStatesTo(WHITESPACE);

        NFA lparenNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("("))
                .setAllFinalStatesTo(LPAREN);
        NFA rparenNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint(")"))
                .setAllFinalStatesTo(RPAREN);

        NFA mulNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("*"))
                .setAllFinalStatesTo(MUL);

        NFA plusNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("+"))
                .setAllFinalStatesTo(PLUS);

        NFA divNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("/"))
                .setAllFinalStatesTo(DIV);

        NFA minusNFA = NFA.singleLetterLanguage(alphabetSize, asCodePoint("-"))
                .setAllFinalStatesTo(MINUS);

        NFA digitNFA = acceptsAllTheseSymbols(alphabetSize, CommonCharClasses.digits);
        NFA unsignedIntegerNFA = digitNFA.positiveIteration();

        NFA integerLiteralNFA = unsignedIntegerNFA
                .setAllFinalStatesTo(INTEGER_LITERAL);

        NFA lang = whitespaceNFA
                .union(lparenNFA)
                .union(rparenNFA)
                .union(plusNFA)
                .union(minusNFA)
                .union(mulNFA)
                .union(divNFA)
                .union(integerLiteralNFA);

        List<StateTag> priorityList = List.of(
                WHITESPACE,
                LPAREN,
                RPAREN,
                PLUS,
                MINUS,
                MUL,
                DIV,
                INTEGER_LITERAL
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
