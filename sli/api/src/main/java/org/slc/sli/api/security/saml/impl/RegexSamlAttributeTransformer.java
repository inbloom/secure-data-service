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


package org.slc.sli.api.security.saml.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.domain.Entity;

/**
 * Uses regexps stored within realm to transform SAML attributes to SLI canonicalized form
 * 
 * @author dkornishev
 * 
 */
@Component
public class RegexSamlAttributeTransformer implements SamlAttributeTransformer {
    
    @Override
    @SuppressWarnings("unchecked")
    public LinkedMultiValueMap<String, String> apply(Entity realm, LinkedMultiValueMap<String, String> samlAttributes) {
        Map<String, List<Map<String, String>>> saml = (Map<String, List<Map<String, String>>>) realm.getBody().get("saml");
        
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
        List<Map<String, String>> fields = saml.get("field");
        for (Map<String, String> node : fields) {
            String clientAttributeName = node.get("clientName");
            
            List<String> samlValues = samlAttributes.get(clientAttributeName);
            
            if (samlValues != null) {
                String sliAttributeName = node.get("sliName");
                
                for (String value : samlValues) {
                    result.add(sliAttributeName, applyValueTransform(value, node.get("transform")));
                }
            }
        }
        
        return result;
    }
    
    private String applyValueTransform(String value, String regex) {
        Matcher m = Pattern.compile(regex).matcher(value);
        
        String resultValue = value;
        if (m.find()) {
            resultValue = m.group(1);
        }
        
        return resultValue;
    }
    
}
