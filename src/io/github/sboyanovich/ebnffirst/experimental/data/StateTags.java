package io.github.sboyanovich.ebnffirst.experimental.data;

import io.github.sboyanovich.scannergenerator.scanner.StateTag;
import io.github.sboyanovich.scannergenerator.scanner.token.Domain;

public enum StateTags implements StateTag {
    WHITESPACE {
        @Override
        public Domain getDomain() {
            return SimpleDomains.WHITESPACE;
        }
    },
    LPAREN {
        @Override
        public Domain getDomain() {
            return SimpleDomains.LPAREN;
        }
    },
    RPAREN {
        @Override
        public Domain getDomain() {
            return SimpleDomains.RPAREN;
        }
    },
    PLUS {
        @Override
        public Domain getDomain() {
            return SimpleDomains.PLUS;
        }
    },
    MINUS {
        @Override
        public Domain getDomain() {
            return SimpleDomains.MINUS;
        }
    },
    MUL {
        @Override
        public Domain getDomain() {
            return SimpleDomains.MUL;
        }
    },
    DIV {
        @Override
        public Domain getDomain() {
            return SimpleDomains.DIV;
        }
    },
    INTEGER_LITERAL {
        @Override
        public Domain getDomain() {
            return DomainsWithIntegerAttribute.INTEGER_LITERAL;
        }
    }
}
