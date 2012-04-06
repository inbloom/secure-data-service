package org.slc.sli.test.edfi.entities.relations;

import java.util.Set;
import java.util.HashSet;

public class CohortMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;

    public final String id;

    /**
     * Constructor for meta data for cohort affiliated with a program
     * 
     * @param id
     * @param programMeta
     */
    public CohortMeta(String id, ProgramMeta programMeta) {
        
        String simplifiedProgramId = programMeta.id.replaceAll("[a-z]", "");
        this.id = simplifiedProgramId + "-prog-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();
    }

    /**
     * Constructor for meta data for cohort not affiliated with a program
     * @param id
     * @param programMeta
     */
    public CohortMeta(String id, SchoolMeta schoolMeta) {
        
        String simplifiedProgramId = schoolMeta.id.replaceAll("[a-z]", "");
        this.id = simplifiedProgramId + "-sch-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();
    }

    @Override
    public String toString() {
        return "CohortMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
