package org.slc.sli.test.edfi.entities.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class StudentMeta {
    public final String id;
    public List<String> schoolIds;
    public List<String> sectionIds;
    public Set<String> programIds;

    public final String simpleId;

    public StudentMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        this.schoolIds = new ArrayList<String>();
        this.schoolIds.add(schoolMeta.id);

        this.sectionIds = new ArrayList<String>();
        
        this.programIds = new HashSet<String>();

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "StudentMeta [id=" + id + ", schoolIds=" + schoolIds + ", sectionIds=" + sectionIds + ", programIds=" + programIds + "]";
    }

}
