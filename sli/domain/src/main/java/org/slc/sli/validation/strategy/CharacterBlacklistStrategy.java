package org.slc.sli.validation.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.BaseValidationRule;
import org.springframework.stereotype.Component;

@Component
public class CharacterBlacklistStrategy extends BaseValidationRule {

    @Resource(name="characterBlacklist")
    private List<String> characterBlacklist;

    private Set<Character> characterSet;

    public CharacterBlacklistStrategy() {
        this("default");
    }

    @PostConstruct
    protected void init() {
        characterSet = new HashSet<Character>();
        for (String entry : characterBlacklist) {
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
                throw new ValidationException("Invalid character", "InvalidCharacter");
            }
        }
        return input;
    }

    @Override
    protected Object sanitize(String context, String input) {
        return input;
    }

}
