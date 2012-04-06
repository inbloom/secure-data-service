package org.slc.sli.test.edfi.entities.relations;

public class StaffMeta {
    public final String id;
    public final String seaId;

    public final String simpleId;

    public StaffMeta(String id, SeaMeta seaMeta) {
        this.id = seaMeta.id + "-" + id;
        this.seaId = seaMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "StaffMeta [id=" + id + ", seaId=" + seaId + "]";
    }

}
