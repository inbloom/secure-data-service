package org.slc.sli.api.validation.config;

import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.api.validation.impl.SimpleURLValidator;
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

    @Bean(name = "urlValidators")
    public List<URLValidator> getURLValidators() {
        List<URLValidator> list = new ArrayList<URLValidator>();
        list.add(new SimpleURLValidator());

        return list;
    }
}
