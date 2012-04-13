package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class TeacherMeta {
    public final String id;
    public final List<String> schoolIds;
    public final List<String> sectionIds;

    public final String simpleId;

    private TeacherMeta(String id, String schoolId) {
        this.id = id;
        this.schoolIds = new ArrayList<String>();
        this.schoolIds.add(schoolId);
        this.sectionIds = new ArrayList<String>();

        this.simpleId = id;
    }

    public static TeacherMeta createWithChainedId(String id, SchoolMeta schoolMeta) {
        return new TeacherMeta(schoolMeta.id + "-" + id, schoolMeta.id);
    }

    public static TeacherMeta create(String id, SchoolMeta schoolMeta) {
        return new TeacherMeta(id, schoolMeta.id);
    }

    @Override
    public String toString() {
        return "TeacherMeta [id=" + id + ", schoolIds=" + schoolIds + ", sectionIds=" + sectionIds + "]";
    }

}
