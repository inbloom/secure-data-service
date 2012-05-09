package org.slc.sli.validation.strategy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.BaseValidationRule;
import org.springframework.stereotype.Component;

@Component
public class StringBlacklistStrategy extends BaseValidationRule {

    @Resource(name="stringBlacklist")
    private List<String> stringBlacklist;

    private Pattern pattern;

    public StringBlacklistStrategy() {
        this("default");
    }

    @PostConstruct
    protected void init() {
        String regex = "\\b(";
        for (String entry : stringBlacklist) {
            regex += entry + "|";
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
            throw new ValidationException("Invalid input", "Invalid input");
        }
        return input;
    }

    @Override
    protected Object sanitize(String context, String input) {
        return input;
    }

}
