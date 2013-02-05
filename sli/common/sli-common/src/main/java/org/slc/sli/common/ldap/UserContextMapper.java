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

package org.slc.sli.common.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * LDAPTemplate mapper for converting User object from the LDAP user context.
 *
 * @author dliu
 *
 */
public class UserContextMapper implements ContextMapper {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(UserContextMapper.class);

    private Date ldapStringToDate(String dateString) {
        if (dateString != null) {
            SimpleDateFormat test = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                return test.parse(dateString);
            } catch (ParseException e) {
                LOG.error("failed parsing ldap string to date {}", e);
            }
        }
        return null;
    }
    @Override
    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        User user = new User();
        user.setSn(context.getStringAttribute("sn"));
        user.setGivenName(context.getStringAttribute("givenName"));
        user.setUid(context.getStringAttribute("uid"));
        user.setEmail(context.getStringAttribute("mail"));
        user.setHomeDir(context.getStringAttribute("homeDirectory"));
        user.setCreateTime(ldapStringToDate(context.getStringAttribute(LdapService.CREATE_TIMESTAMP)));
        user.setModifyTime(ldapStringToDate(context.getStringAttribute(LdapService.MODIFY_TIMESTAMP)));
        user.setPassword("**********");
        user.setCn(context.getStringAttribute("cn"));
        user.setStatus(User.Status.getFromString(context.getStringAttribute("destinationindicator")));

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
                String[] pairArray = pair.trim().split("=", 2);
                if (pairArray.length == 2) {
                    String key = pairArray[0].trim();
                    String value = pairArray[1].trim();
                    if (key.toLowerCase().indexOf("tenant") >= 0) {
                        user.setTenant(value);
                    } else if (key.toLowerCase().indexOf("edorg") >= 0) {
                        user.setEdorg(value);
                    }
                }
            }
        }
        return user;
    }

}
