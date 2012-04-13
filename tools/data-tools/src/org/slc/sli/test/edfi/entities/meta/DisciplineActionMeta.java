package org.slc.sli.test.edfi.entities.meta;

import java.util.Set;
import java.util.HashSet;

public class DisciplineActionMeta {

    public Set<String> incidentIds;
    public Set<String> studentIds;
    public Set<String> staffIds;

    public String schoolId; 
    
    public final String id;

    public DisciplineActionMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        incidentIds = new HashSet<String>();
        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        schoolId = schoolMeta.id;
    }

    @Override
    public String toString() {
        return "DisciplineActionMeta [id=" + id + ",incidentIds=" + incidentIds + ",staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
