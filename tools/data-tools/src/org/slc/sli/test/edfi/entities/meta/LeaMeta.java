package org.slc.sli.test.edfi.entities.meta;

import java.util.HashMap;
import java.util.Map;

public class LeaMeta {

    public final String id;
    public final String seaId;

    public final String simpleId;
    
    public Map<String, ProgramMeta> programs = new HashMap();
    
    public String programId;

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
