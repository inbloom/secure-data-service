package org.slc.sli.validation.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.BaseValidationRule;
import org.springframework.stereotype.Component;

@Component
public class RegexBlacklistStrategy extends BaseValidationRule {

    @Resource(name="regexBlacklist")
    private List<String> regexBlacklist;

    private List<Pattern> patternList;

    public RegexBlacklistStrategy() {
        this("default");
    }

    @PostConstruct
    protected void init() {
        patternList = new ArrayList<Pattern>();
        for (String entry : regexBlacklist) {
            patternList.add(Pattern.compile(entry, Pattern.CASE_INSENSITIVE));
        }
    }

    public RegexBlacklistStrategy(String typeName) {
        super(typeName);
    }

    @Override
    public Object getValid(String context, String input) throws ValidationException {
        for (Pattern pattern : patternList) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                throw new ValidationException("Invalid input", "Invalid input");
            }
        }
        return input;
    }

    @Override
    protected Object sanitize(String context, String input) {
        return input;
    }

}
