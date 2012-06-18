package org.slc.sli.test.edfi.entities.meta;

public class LeaMeta {

    public final String id;
    public final String seaId;

    public final String simpleId;

    public LeaMeta(String id, SeaMeta seaMeta) {
        this.id = seaMeta.id + "-" + id;
        this.seaId = seaMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "LeaMeta [id=" + id + ", seaId=" + seaId + "]";
    }
}
