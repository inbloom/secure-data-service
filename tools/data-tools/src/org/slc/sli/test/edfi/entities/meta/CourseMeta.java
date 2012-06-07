package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.test.edfi.entities.CourseCode;

public class CourseMeta {
    public final String id;
    public final String schoolId;

    public final String simpleId;

    public final List<CourseCode> courseCodes = new ArrayList<CourseCode>();

    public CourseMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;
        this.schoolId = schoolMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "CourseMeta [id=" + id + ", schoolId=" + schoolId + "]";
    }
}
