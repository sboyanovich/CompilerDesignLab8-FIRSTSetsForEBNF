package io.github.sboyanovich.ebnffirst;

import io.github.sboyanovich.ebnffirst.data.SimpleDomains;
import io.github.sboyanovich.scannergenerator.scanner.Compiler;
import io.github.sboyanovich.scannergenerator.scanner.*;
import io.github.sboyanovich.scannergenerator.scanner.Scanner;
import io.github.sboyanovich.scannergenerator.scanner.token.Domain;
import io.github.sboyanovich.scannergenerator.scanner.token.Token;
import io.github.sboyanovich.scannergenerator.utility.Utility;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        int alphabetSize = Character.MAX_CODE_POINT + 1;
        alphabetSize = 256; // special case hack for faster recognizer generation

        LexicalRecognizer recognizer = io.github.sboyanovich.ebnffirst.Utility.buildMainTestRecognizer(alphabetSize);

        String dot = recognizer.toGraphvizDotString(Object::toString, true);
        System.out.println(dot);

        String text = Utility.getText("grammar.txt");

        Compiler compiler = new Compiler(recognizer);
        Scanner scanner = compiler.getScanner(text);

        Set<Domain> ignoredTokenTypes = Set.of(
                SimpleDomains.WHITESPACE,
                Domain.END_OF_PROGRAM,
                Domain.ERROR
        );

        int errCount = 0;

        List<Token> tokensToParse = new ArrayList<>();

        Token t = scanner.nextToken();
        while (t.getTag() != Domain.END_OF_PROGRAM) {
            if (!ignoredTokenTypes.contains(t.getTag())) {
                tokensToParse.add(t);
                System.out.println(t);
            }
            if (t.getTag() == Domain.ERROR) {
                errCount++;
                System.out.println(t.getCoords());
            }
            t = scanner.nextToken();
        }
        tokensToParse.add(t);

        System.out.println();
        System.out.println("Errors: " + errCount);
        System.out.println("Compiler messages: ");
        SortedMap<Position, Message> messages = compiler.getSortedMessages();
        for (Map.Entry<Position, Message> entry : messages.entrySet()) {
            System.out.println(entry.getValue() + " at " + entry.getKey());
        }

        System.out.println();

        EBNFGrammar grammar = Parser.parse(tokensToParse.iterator());
        String epsilon = "<epsilon>";

        Map<String, Set<String>> ntsFirst = grammar.firstSets(epsilon);

        for (String nonTerminal : ntsFirst.keySet()) {
            Set<String> first = ntsFirst.get(nonTerminal);
            System.out.println("FIRST(" + nonTerminal + ") = " + first);
        }
    }
}
