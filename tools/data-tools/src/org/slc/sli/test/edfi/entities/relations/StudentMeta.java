package org.slc.sli.test.edfi.entities.relations;

import java.util.ArrayList;
import java.util.List;

public class StudentMeta {
    public final String id;
    public List<String> schoolIds;
    public List<String> sectionIds;

    public final String simpleId;

    public StudentMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        this.schoolIds = new ArrayList<String>();
        this.schoolIds.add(schoolMeta.id);

        this.sectionIds = new ArrayList<String>();
<<<<<<< HEAD
        
=======

>>>>>>> master
        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "StudentMeta [id=" + id + ", schoolIds=" + schoolIds + ", sectionIds=" + sectionIds + "]";
    }

}
