package org.slc.sli.validation.strategy;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CharacterBlacklistStrategy extends AbstractBlacklistStrategy {

    private Set<Character> characterSet;

    public CharacterBlacklistStrategy() {
        this("default");
    }

    @PostConstruct
    protected void init() {
        characterSet = new HashSet<Character>();

        if (inputCollection == null) {
            return;
        }

        for (String entry : inputCollection) {
            if (entry.isEmpty()) {
                continue;
            }

            if (!entry.startsWith("\\u")) {
                continue;
            }

            try {
                char c = (char) Integer.parseInt(entry.substring(2), 16);
                characterSet.add(new Character(c));
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

    public CharacterBlacklistStrategy(String typeName) {
        super(typeName);
    }

    @Override
    public Object getValid(String context, String input) throws ValidationException {
        for (char c : input.toCharArray()) {
            if (characterSet.contains(c)) {
                throw new BlacklistValidationException("Invalid input: " + input);
            }
        }
        return input;
    }

    @Override
    protected Object sanitize(String context, String input) {
        return input;
    }
}
