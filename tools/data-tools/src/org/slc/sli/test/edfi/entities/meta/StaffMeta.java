package org.slc.sli.test.edfi.entities.meta;

public class StaffMeta {
    public final String id;
    public final String edOrgId;

    public final String simpleId;

    private StaffMeta(String id, String edOrgId) {
        this.id = id;
        this.edOrgId = edOrgId;
        this.simpleId = id;
    }

    public static StaffMeta createWithChainedId(String id, SeaMeta seaMeta) {
        return new StaffMeta(seaMeta.id + "-" + id, seaMeta.id);
    }

    @Override
    public String toString() {
        return "StaffMeta [id=" + id + ", edOrgId=" + edOrgId + "]";
    }

}
