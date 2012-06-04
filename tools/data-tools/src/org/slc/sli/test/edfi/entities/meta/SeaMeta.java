package org.slc.sli.test.edfi.entities.meta;

import java.util.HashMap;
import java.util.Map;

public class SeaMeta {

    public final String id;
    
    public String programId;
    
    public Map<String, ProgramMeta> programs = new HashMap();

    public SeaMeta(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SeaMeta [id=" + id + "]";
    }

}
