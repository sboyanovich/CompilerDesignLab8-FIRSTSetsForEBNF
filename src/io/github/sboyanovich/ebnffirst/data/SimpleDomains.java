package io.github.sboyanovich.ebnffirst.data;

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
    DOT {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, DOT);
        }
    },
    VERTICAL_BAR {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, VERTICAL_BAR);
        }
    },
    EQUALS {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, EQUALS);
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
    STAR {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, STAR);
        }
    },
    POS_ITER {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, POS_ITER);
        }
    },
    LBRACKET {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, LBRACKET);
        }
    },
    RBRACKET {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, RBRACKET);
        }
    },
    OPTION {
        @Override
        public Token createToken(Text text, Fragment fragment) {
            return new BasicToken(fragment, OPTION);
        }
    }
}
