package org.slc.sli.test.edfi.entities.meta;

public class SchoolMeta {

    public final String id;
    public final String leaId;

    public final String simpleId;
    
    public String programId;

    public SchoolMeta(String id, LeaMeta leaMeta) {
        this.id = leaMeta.id + "-" + id;
        this.leaId = leaMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "SchoolMeta [id=" + id + ", leaId=" + leaId + "]";
    }
}
