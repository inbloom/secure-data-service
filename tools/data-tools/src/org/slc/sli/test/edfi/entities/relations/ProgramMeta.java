package org.slc.sli.test.edfi.entities.relations;

import java.util.Set;
import java.util.HashSet;

public class ProgramMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;
    
    public final String id;

    public ProgramMeta(String id) {
        this.id = id;
        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();
    }

    @Override
    public String toString() {
        return "ProgramMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
