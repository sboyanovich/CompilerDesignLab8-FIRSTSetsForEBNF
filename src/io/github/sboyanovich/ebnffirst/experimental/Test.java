package io.github.sboyanovich.ebnffirst.experimental;

import io.github.sboyanovich.ebnffirst.experimental.data.SimpleDomains;
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

        LexicalRecognizer recognizer = io.github.sboyanovich.ebnffirst.experimental
                .Utility.buildArithmeticExpressionRecognizer(alphabetSize);

        String dot = recognizer.toGraphvizDotString(Object::toString, true);
        System.out.println(dot);

        String text = Utility.getText("ae.txt");

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

        // EVALUATE EXPRESSION IN TWO WAYS
        //  1) evaluate during parsing
        //  2) convert to Expression tree and then evaluate

        int value = ArithmeticEvaluator.evaluate(tokensToParse.iterator());

        Expression e = ArithmeticParser.parse(tokensToParse.iterator());
        String dotAST = e.toGraphVizDotString();
        System.out.println(dotAST);

        int valueAST = e.evaluate();
        System.out.println("value = " + value);
        System.out.println("valueAST = " + valueAST);
    }
}
