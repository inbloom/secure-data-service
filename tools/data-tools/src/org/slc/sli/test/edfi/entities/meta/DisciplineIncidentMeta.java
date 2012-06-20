package org.slc.sli.test.edfi.entities.meta;

import java.util.Set;
import java.util.HashSet;

public class DisciplineIncidentMeta {

    public Set<String> studentIds;
    public String schoolId;
    public String staffId;

    public final String id;

    public DisciplineIncidentMeta(String id, SchoolMeta schoolMeta, String teacherId) {
        this.id = schoolMeta.id + "-" + id;
        this.schoolId = schoolMeta.id;

        studentIds = new HashSet<String>();
        schoolId = schoolMeta.id;
        staffId = teacherId;
    }

    @Override
    public String toString() {
        return "DisciplineIncidentMeta [id=" + id + ",studentIds=" + studentIds + ",schoolId=" + schoolId + ",staffId=" + staffId + "]";
    }

}
