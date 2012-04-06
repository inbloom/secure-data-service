package org.slc.sli.test.edfi.entities.relations;

import java.util.Set;
import java.util.HashSet;

public class CohortMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;
    public ProgramMeta programMeta;

    public final String id;

    public CohortMeta(String id, ProgramMeta programMeta) {
        this.id = programMeta.id + "-" + id;
        this.programMeta = programMeta;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();
    }

    @Override
    public String toString() {
        return "CohortMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
