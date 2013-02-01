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


package org.slc.sli.api.validation.config;

import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.api.validation.impl.QueryStringValidator;
import org.slc.sli.api.validation.impl.SimpleURLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves up the url validation list
 * @author srupasinghe
 *
 */
@Configuration
public class URLValidationConfig {

    @Autowired
    SimpleURLValidator simpleURLValidator;

    @Autowired
    QueryStringValidator queryStringValidator;

    @Bean(name = "urlValidators")
    public List<URLValidator> getURLValidators() {
        List<URLValidator> list = new ArrayList<URLValidator>();
        list.add(simpleURLValidator);
        list.add(queryStringValidator);

        return list;
    }
}
