package io.github.sboyanovich.ebnffirst.experimental.data;

import io.github.sboyanovich.scannergenerator.scanner.Fragment;
import io.github.sboyanovich.scannergenerator.scanner.Text;
import io.github.sboyanovich.scannergenerator.scanner.token.BasicToken;
import io.github.sboyanovich.scannergenerator.scanner.token.Domain;
import io.github.sboyanovich.scannergenerator.scanner.token.Token;

public enum SimpleDomains implements Domain {
    WHITESPACE {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, WHITESPACE);
        }
    },
    LPAREN {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, LPAREN);
        }
    },
    RPAREN {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, RPAREN);
        }
    },
    MUL {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, MUL);
        }
    },
    PLUS {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, PLUS);
        }
    },
    MINUS {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, MINUS);
        }
    },
    DIV {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, DIV);
        }
    }
}
