package org.slc.sli.test.edfi.entities.meta;

public class CourseOfferingMeta {
    public final String id;
    public final SchoolMeta schoolId;
    public final SessionMeta sessionMeta;
    public final CourseMeta courseMeta;

    public final String simpleId;

    public CourseOfferingMeta(String id, SchoolMeta schoolMeta, SessionMeta sessionMeta, CourseMeta courseMeta) {
        this.id = schoolMeta.id + "-" + id;
        this.schoolId = schoolMeta;
        this.sessionMeta = sessionMeta;
        this.courseMeta = courseMeta;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "CourseOfferingMeta [id=" + id + ", schoolId=" + schoolId + ", sessionId=" + sessionMeta.id
                + ", courseId=" + courseMeta.id + "]";
    }

}
