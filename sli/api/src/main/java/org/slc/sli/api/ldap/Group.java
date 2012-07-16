package org.slc.sli.api.ldap;

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
        if (!memberUids.contains(memberUid))
        memberUids.add(memberUid);
    }
    
    public void removeMemberUid(String memberUid) {
        if (memberUids != null && memberUids.contains(memberUid)) {
            memberUids.remove(memberUid);
        }
    }
}
