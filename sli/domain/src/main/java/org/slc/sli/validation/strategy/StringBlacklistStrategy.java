package org.slc.sli.validation.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validation strategy to check for invalid strings of characters in String input
 * @author vmcglaughlin
 */
@Component
public class StringBlacklistStrategy extends AbstractBlacklistStrategy {

    private Pattern pattern;

    /**
     * Default constructor, sets typeName to "StringBlacklistStrategy"
     */
    public StringBlacklistStrategy() {
        super("StringBlacklistStrategy");
    }

    @Override
    @PostConstruct
    protected void init() {
        String regex = "\\b(";

        if (inputCollection != null) {
            for (String entry : inputCollection) {
                regex += entry + "|";
            }
        }

        if (regex.endsWith("|")) {
            regex = regex.substring(0, regex.length() - 1);
        }

        regex += ")\\b";

        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Object getValid(String context, String input) throws ValidationException {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            throw new BlacklistValidationException("Invalid input: " + input);
        }
        return input;
    }

    @Override
    protected Object sanitize(String context, String input) {
        return input;
    }
}
