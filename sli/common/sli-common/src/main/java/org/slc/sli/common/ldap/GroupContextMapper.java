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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * LDAPTemplate mapper for converting Group object from the LDAP group context.
 * 
 * @author dliu
 * 
 */
public class GroupContextMapper implements ContextMapper {

    @Override
    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        Group group = new Group();
        group.setGroupName(context.getStringAttribute("cn"));
        String[] memberUids = context.getStringAttributes("memberUid");
        if (memberUids != null && memberUids.length > 0) {
            List<String> uids = new ArrayList<String>();
            uids.addAll(Arrays.asList(memberUids));
            group.setMemberUids(uids);
        }
        return group;
    }
    
}
