package org.slc.sli.api.validation.impl;

import org.apache.commons.validator.routines.UrlValidator;
import org.slc.sli.api.validation.URLValidator;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * A simple url validator using apache-common url validator
 * @author srupasinghe
 *
 */
@Component
public class SimpleURLValidator implements URLValidator {

    @Override
    public boolean validate(URI url) {
        String[] schemes = {"http","https"};
        UrlValidator validator = new UrlValidator(schemes);

        return validator.isValid(url.toString());
    }
}
