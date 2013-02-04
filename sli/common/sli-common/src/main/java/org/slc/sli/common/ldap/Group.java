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
import java.util.List;

/**
 * define the class that represent the group entry in LDAP directory
 * 
 * @author dliu
 * 
 */
public class Group {

    private String groupName;
    private List<String> memberUids;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMemberUids() {
        return memberUids;
    }

    public void setMemberUids(List<String> memberUids) {
        this.memberUids = memberUids;
    }

    public void addMemberUid(String memberUid) {
        if (memberUids == null) {
            memberUids = new ArrayList<String>();
        }

        if (!memberUids.contains(memberUid)) {
            memberUids.add(memberUid);
        }
    }

    public void removeMemberUid(String memberUid) {
        if (memberUids != null && memberUids.contains(memberUid)) {
            memberUids.remove(memberUid);
        }
    }
}
