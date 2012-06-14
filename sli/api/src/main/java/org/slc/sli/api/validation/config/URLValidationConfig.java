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
