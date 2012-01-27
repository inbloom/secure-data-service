package org.slc.sli.api.security.resolve.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.SliAdminValidator;

/**
 * Default implementation of {@link SliAdminValidator}.
 * The specified realm name is matched against a regexp pattern.
 * 
 * @author pwolf
 * 
 */
@Component
public class DefaultSliAdminValidator implements SliAdminValidator {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSliAdminValidator.class);
    
    @Value("${sli.security.admin.realm}")
    private String              patternString;
    
    private Pattern             realmPattern;
    
    public DefaultSliAdminValidator() {
        this.patternString = "Intercision-185a7f52-17ff-4016-ad5f-27a5b78e0f26";
    }
    
    public DefaultSliAdminValidator(String patternString) {
        this.patternString = patternString;
    }
    
    /**
     * Initialize a validator.
     * 
     * @throws IllegalArgumentException
     *             if the pattern is null or not valid regex
     */
    @PostConstruct
    public void init() {
        if (patternString == null) {
            throw new IllegalArgumentException("Pattern must not be null");
        }
        
        try {
            realmPattern = Pattern.compile(patternString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid realm regex pattern specified: " + patternString, e);
        }
    }
    
    /**
     * Determine if the specified realm matches the pattern that was given earlier.
     */
    @Override
    public boolean isSliAdminRealm(String realm) {
        boolean matches = false;
        if (realm != null) {
            matches = realmPattern.matcher(realm).matches();
        }
        LOG.trace("isSliAdminRealm {} = {}", realm, matches);
        return matches;
    }
    
}
