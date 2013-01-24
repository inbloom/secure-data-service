/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define the class that represents the user entry in ldap directory
 *
 * @author dliu
 *
 */
public class User {

    private String uid;
    private List<String> groups;
    private String password;
    private String email;
    private String tenant;
    private String edorg;
    private String homeDir;
    private String sn;
    private String givenName;
    private String cn;
    private String vendor;
	private Status status = Status.SUBMITTED;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setFullName(final String name) {
        String fullName = name;
        if (fullName == null) {
            this.sn = null;
            this.givenName = null;
        } else {
            fullName = fullName.replaceAll("\\s+", " ");
            fullName = fullName.trim();
            this.givenName = fullName.split(" ")[0];

            String[] split = fullName.split(" ", 2);
            if (split.length == 2) {
                this.sn = split[1];
            } else {
                this.sn = null;
            }
        }
    }

    private Date createTime;
    private Date modifyTime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void addGroup(String group) {
        if (groups == null) {
            groups = new ArrayList<String>();
        }
        groups.add(group);
    }

    public void removeGroup(String group) {
        if (groups != null && groups.contains(group)) {
            groups.remove(group);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getEdorg() {
        return edorg;
    }

    public void setEdorg(String edorg) {
        this.edorg = edorg;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public String getFullName() {
        if (getGivenName() == null && getSn() == null) {
            return null;
        }
        String fullName = this.getGivenName() + (this.getSn() == null ? "" : " " + this.getSn());
        return fullName.trim();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        if (this.sn == null) {
            this.sn = sn;
        }
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        if (this.givenName == null) {
            this.givenName = givenName;
        }
    }

    public void setCreateTime(Date createTime) {
        this.createTime = new Date(createTime.getTime());
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = new Date(modifyTime.getTime());
    }

    public String getCreateTime() {
        if (createTime == null) {
            return null;
        }
        return toDateString(createTime);
    }

    public String getModifyTime() {
        if (modifyTime == null) {
            return null;
        }
        return toDateString(modifyTime);
    }

    private String toDateString(Date date) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDataFormat.format(date);
    }

    public static String printGroup(List<String> groups) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                sb.append(groups.get(i));
                if (i != groups.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder("User [");
    	builder.append("uid=").append(uid).append(", ");
    	builder.append("groups=").append(groups).append(", ");
    	builder.append("password=").append(password).append(", ");
    	builder.append("email=").append(email).append(", ");
    	builder.append("tenant=").append(tenant).append(", ");
    	builder.append("edorg=").append(edorg).append(", ");
    	builder.append("homeDir=").append(homeDir).append(", ");
    	builder.append("fullName=").append(getFullName()).append(", ");
    	builder.append("cn=").append(cn).append(", ");
    	builder.append("status=").append(status).append(", ");
    	builder.append("createTime=").append(createTime).append(", ");
    	builder.append("modifyTime=").append(modifyTime).append(", ");
    	builder.append("vendor=").append(vendor).append("]");
    
    	return builder.toString();
    }

    /**
     * A user status
     *
     * @author nbrown
     *
     */
    public enum Status {
        SUBMITTED("submitted"), APPROVED("approved");
        private final String statusString;

        private Status(String statusString) {
            this.statusString = statusString;
        }

        protected String getStatusString() {
            return statusString;
        }

        protected static Status getFromString(String statusString) {
            for (Status value : Status.values()) {
                if (value.getStatusString().equals(statusString)) {
                    return value;
                }
            }
            return null;
        }

    }

}
