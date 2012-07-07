package org.slc.sli.api.ldap;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * LDAPTemplate mapper for getting attributes from the person context. Retrieves cn and
 * description,
 * parsing the value of description by line and then by key=value.
 * 
 * @author scole
 * 
 */
public class PersonContextMapper implements ContextMapper {
    @Override
    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        User user = new User();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("userName", context.getStringAttribute("cn"));
        String description = context.getStringAttribute("description");
        if (description != null && description.length() > 0) {
            String[] pairs;
            if (description.indexOf("\n") > 0) {
                pairs = description.split("\n");
            } else if (description.indexOf(",") > 0) {
                pairs = description.split(",");
            } else {
                pairs = description.split(" ");
            }
            for (String pair : pairs) {
                pair = pair.trim();
                String[] pairArray = pair.split("=", 2);
                if (pairArray.length == 2) {
                    String value = pairArray[1].trim();
                    if (value.length() > 0) {
                        attributes.put(pairArray[0].trim(), value);
                    }
                }
            }
        }
        user.setAttributes(attributes);
        return user;
    }
    
}
