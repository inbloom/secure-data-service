package org.slc.sli.web.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for NoBadChars.
 * @author agrebneva
 *
 */
public class NoBadCharsValidator implements ConstraintValidator<NoBadChars, Object> {
    private Pattern pattern;
    private int depth;
    @Override
    public void initialize(NoBadChars noBadChars) {
        pattern = Pattern.compile(noBadChars.regexp());
        this.depth = noBadChars.depth();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        return traverseObject(object, 0);
    }

    /**
     * Traverse object up to N depth levels and validate all found maps, strings, and collections
     * @param object - to validate
     * @param context - context
     * @param count - depth
     * @return
     */
    private boolean traverseObject(Object object, int count) {
        if (count >= depth) {
            return true;
        }
        if (object instanceof String) {
            return validateString((String) object);
        }
        if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) object;
            for (Map.Entry<String, ?> item :  map.entrySet()) {
                if (!(validateString(item.getKey()) && traverseObject(item.getValue(), count))) {
                    return false;
                }
            }
        }
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            for (Object item : collection) {
                if (!traverseObject(item, count + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateString(String s) {
        return !pattern.matcher(s).find();
    }
}