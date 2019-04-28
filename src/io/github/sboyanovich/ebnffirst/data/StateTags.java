package io.github.sboyanovich.ebnffirst.data;

import io.github.sboyanovich.scannergenerator.scanner.StateTag;
import io.github.sboyanovich.scannergenerator.scanner.token.Domain;

public enum StateTags implements StateTag {
    WHITESPACE {
        @Override
        public Domain getDomain() {
            return SimpleDomains.WHITESPACE;
        }
    },
    DOT {
        @Override
        public Domain getDomain() {
            return SimpleDomains.DOT;
        }
    },
    VERTICAL_BAR {
        @Override
        public Domain getDomain() {
            return SimpleDomains.VERTICAL_BAR;
        }
    },
    EQUALS {
        @Override
        public Domain getDomain() {
            return SimpleDomains.EQUALS;
        }
    },
    TERMINAL {
        @Override
        public Domain getDomain() {
            return DomainsWithStringAttribute.TERMINAL;
        }
    },
    NON_TERMINAL {
        @Override
        public Domain getDomain() {
            return DomainsWithStringAttribute.NON_TERMINAL;
        }
    },
    STAR {
        @Override
        public Domain getDomain() {
            return SimpleDomains.STAR;
        }
    },
    POS_ITER {
        @Override
        public Domain getDomain() {
            return SimpleDomains.POS_ITER;
        }
    },
    LBRACKET {
        @Override
        public Domain getDomain() {
            return SimpleDomains.LBRACKET;
        }
    },
    RBRACKET {
        @Override
        public Domain getDomain() {
            return SimpleDomains.RBRACKET;
        }
    },
    OPTION {
        @Override
        public Domain getDomain() {
            return SimpleDomains.OPTION;
        }
    }
}
