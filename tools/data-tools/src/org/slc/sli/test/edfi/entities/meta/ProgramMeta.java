package org.slc.sli.test.edfi.entities.meta;

import java.util.Set;
import java.util.HashSet;

public class ProgramMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;

    public Set<String> cohortIds;

    public String orgId; // this is used in generating studentProgram associations
    
    public final String id;

    public ProgramMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        cohortIds = new HashSet<String>();

        orgId = schoolMeta.id;
    }
    
    
    public ProgramMeta(String id, SeaMeta seaMeta) {
        this.id = seaMeta.id + "-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        cohortIds = new HashSet<String>();

        orgId = seaMeta.id;
    }

    @Override
    public String toString() {
        return "ProgramMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + 
                             ",cohortIds=" + cohortIds + "]";
    }

}
