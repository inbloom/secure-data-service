package org.slc.sli.util;

import org.slc.sli.entity.CustomConfigString;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * 
 * @author svankina
 *
 */
public class CustomConfigValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomConfigString.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object customConfigString, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "configString", "Empty string is not allowed, use {} to represent an empty string");

        try {
            JsonElement jsonElement = new JsonParser().parse(((CustomConfigString) customConfigString).getConfigString());
        } catch (JsonSyntaxException jse) {
            errors.reject("configString", "Json Syntax Error");
        } catch (NullPointerException npe) {
            errors.reject("configString", "JSON String can not be null");
        }
    }

}
