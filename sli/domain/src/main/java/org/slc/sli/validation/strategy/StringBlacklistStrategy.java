package org.slc.sli.validation.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class StringBlacklistStrategy extends AbstractBlacklistStrategy {

    private Pattern pattern;

    public StringBlacklistStrategy() {
        this("default");
    }

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

    public StringBlacklistStrategy(String typeName) {
        super(typeName);
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
