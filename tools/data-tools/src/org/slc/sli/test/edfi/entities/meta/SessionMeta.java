package org.slc.sli.test.edfi.entities.meta;

public class SessionMeta {
    public final String id;
    public final String schoolId;

    public final String simpleId;

    public SessionMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;
        this.schoolId = schoolMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "SessionMeta [id=" + id + ", schoolId=" + schoolId + "]";
    }
}
