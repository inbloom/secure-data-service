package org.slc.sli.test.edfi.entities.relations;

import java.util.ArrayList;
import java.util.List;

public class TeacherMeta {
    public final String id;
    public List<String> schoolIds;
    public List<String> sectionIds;

    public final String simpleId;

    public TeacherMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        this.schoolIds = new ArrayList<String>();
        this.schoolIds.add(schoolMeta.id);

        this.sectionIds = new ArrayList<String>();

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "TeacherMeta [id=" + id + ", schoolIds=" + schoolIds + ", sectionIds=" + sectionIds + "]";
    }

}
