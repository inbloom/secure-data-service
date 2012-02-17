package org.slc.sli.api.security.saml;

import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.domain.Entity;

/**
 * Transforms saml attributes and values to SLI canonicalized format
 * 
 * @author dkornishev
 * 
 */
public interface SamlAttributeTransformer {
    public LinkedMultiValueMap<String, String> apply(Entity realm, LinkedMultiValueMap<String, String> samlAttributes);
}
