package io.github.sboyanovich.ebnffirst.data;

import io.github.sboyanovich.scannergenerator.scanner.Fragment;
import io.github.sboyanovich.scannergenerator.scanner.Text;
import io.github.sboyanovich.scannergenerator.scanner.token.DomainWithAttribute;
import io.github.sboyanovich.scannergenerator.scanner.token.TokenWithAttribute;
import io.github.sboyanovich.scannergenerator.utility.Utility;

import static io.github.sboyanovich.scannergenerator.utility.Utility.asCodePoint;

public enum DomainsWithStringAttribute implements DomainWithAttribute<String> {
    TERMINAL {
        private final int BACKSLASH = asCodePoint("\\");

        @Override
        public TokenWithAttribute<String> createToken(Text text, Fragment fragment) {
            return new TokenWithAttribute<>(fragment, TERMINAL, attribute(text, fragment));
        }

        @Override
        public String attribute(Text text, Fragment fragment) {
            String simple = super.attribute(text, fragment);
            if (simple.codePointAt(0) == BACKSLASH) {
                return simple.substring(1);
            }
            return simple;
        }
    },
    NON_TERMINAL {
        @Override
        public TokenWithAttribute<String> createToken(Text text, Fragment fragment) {
            return new TokenWithAttribute<>(fragment, NON_TERMINAL, attribute(text, fragment));
        }

        @Override
        public String attribute(Text text, Fragment fragment) {
            String all = super.attribute(text, fragment);
            return all.substring(1, all.length() - 1);
        }
    };

    @Override
    public String attribute(Text text, Fragment fragment) {
        return Utility.getTextFragmentAsString(text, fragment);
    }
}
