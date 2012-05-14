package org.slc.sli.validation.strategy;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Validation strategy to check for invalid characters in String input
 * @author vmcglaughlin
 */
@Component
public class CharacterBlacklistStrategy extends AbstractBlacklistStrategy {

    private Set<Character> characterSet;

    /**
     * Default constructor, sets identifier to "CharacterBlacklistStrategy"
     */
    public CharacterBlacklistStrategy() {
        super("CharacterBlacklistStrategy");
    }

    @Override
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

            try {
                String charStr = entry;
                if (entry.startsWith("\\u")) {
                    charStr = entry.substring(2);
                }

                char c = (char) Integer.parseInt(charStr, 16);
                characterSet.add(new Character(c));

            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

    @Override
    public boolean isValid(String context, String input) {
        if (input == null) {
            return false;
        }

        for (char c : input.toCharArray()) {
            if (characterSet.contains(c)) {
                return false;
            }
        }
        return true;
    }
}
