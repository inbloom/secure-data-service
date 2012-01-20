package org.slc.sli.api.security.resolve.impl;

import java.util.regex.Pattern;

import org.slc.sli.api.security.resolve.SliAdminValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link SliAdminValidator}.
 * The specified realm name is matched against a regexp pattern.
 * 
 * @author pwolf
 * 
 */
public class DefaultSliAdminValidator implements SliAdminValidator {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSliAdminValidator.class);
    
    private Pattern realmPattern = null;
    
    /**
     * Construct a validator.
     * 
     * @param patternString
     *            a regex string to use for validation
     * @throws IllegalArgumentException
     *             if the pattern is null or not valid regex
     */
    public DefaultSliAdminValidator(String patternString) {
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
