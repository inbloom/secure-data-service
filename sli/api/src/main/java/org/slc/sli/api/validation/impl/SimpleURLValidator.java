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


package org.slc.sli.api.validation.impl;

import java.net.URI;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import org.slc.sli.api.validation.URLValidator;

/**
 * A simple url validator using apache-common url validator
 * @author srupasinghe
 *
 */
@Component
public class SimpleURLValidator implements URLValidator {

    @Override
    public boolean validate(URI url) {
        String[] schemes = {"http", "https"};
        UrlValidator validator = new UrlValidator(schemes);

        return validator.isValid(url.toString());
    }
}
