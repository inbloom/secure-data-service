package org.slc.sli.validation.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Validation strategy to check for invalid strings of characters in String input
 * @author vmcglaughlin
 */
@Component
public class StringBlacklistStrategy extends AbstractBlacklistStrategy {

    private Pattern pattern;

    /**
     * Default constructor, sets identifier to "StringBlacklistStrategy"
     */
    public StringBlacklistStrategy() {
        super("StringBlacklistStrategy");
    }

    @Override
    @PostConstruct
    protected void init() {
        String regex = "(";

        if (inputCollection != null) {
            for (String entry : inputCollection) {
                regex += entry + "|";
            }
        }

        if (regex.endsWith("|")) {
            regex = regex.substring(0, regex.length() - 1);
        }

        regex += ")";

        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public boolean isValid(String context, String input) {
        if (input == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return false;
        }
        return true;
    }
}
