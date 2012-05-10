package org.slc.sli.validation.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Validation strategy to check for invalid text in String input based on regex
 * @author vmcglaughlin
 */
@Component
public class RegexBlacklistStrategy extends AbstractBlacklistStrategy {

    private List<Pattern> patternList;

    /**
     * Default constructor, sets typeName to "RegexBlacklistStrategy"
     */
    public RegexBlacklistStrategy() {
        super("RegexBlacklistStrategy");
    }

    @Override
    @PostConstruct
    protected void init() {
        patternList = new ArrayList<Pattern>();

        if (inputCollection == null) {
            return;
        }

        for (String entry : inputCollection) {
            try {
                patternList.add(Pattern.compile(entry, Pattern.CASE_INSENSITIVE));
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Override
    public Object getValid(String context, String input) throws ValidationException {
        for (Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
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
