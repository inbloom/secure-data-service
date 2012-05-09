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
public class BlacklistByCharsetStrategy extends BaseValidationRule {

    @Resource(name="charsetBlacklist")
    private List<String> charsetBlacklist;

    private List<Pattern> patternList;

    public BlacklistByCharsetStrategy() {
        this("default");
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        patternList = new ArrayList<Pattern>();
        for (String entry : charsetBlacklist) {
            patternList.add(Pattern.compile(entry, Pattern.CASE_INSENSITIVE));
        }
    }

    public BlacklistByCharsetStrategy(String typeName) {
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
