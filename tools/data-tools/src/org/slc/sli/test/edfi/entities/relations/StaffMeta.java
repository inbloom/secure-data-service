package org.slc.sli.test.edfi.entities.relations;

import java.util.ArrayList;
import java.util.List;

public final class StaffMeta {
    public final String id;
    public final List<String> edOrgIds;

    public final String simpleId;

    private StaffMeta(String id, String edOrgId) {
        this.id = id;
        this.edOrgIds = new ArrayList<String>();
        this.edOrgIds.add(edOrgId);
        this.simpleId = id;
    }

    public static StaffMeta createWithChainedId(String id, SchoolMeta schoolMeta) {
        return new StaffMeta(schoolMeta.id + "-" + id, schoolMeta.id);
    }

    public static StaffMeta create(String id, SchoolMeta schoolMeta) {
        return new StaffMeta(id, schoolMeta.id);
    }

    @Override
    public String toString() {
        return "StaffMeta [id=" + id + ", schoolIds=" + edOrgIds + "]";
    }

}
