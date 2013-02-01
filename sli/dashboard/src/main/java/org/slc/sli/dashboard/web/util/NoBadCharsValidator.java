/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.web.util;

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
