package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class AssessmentItemMeta {

    public final String id;
    public final List<String> learningStandardIds;

    private AssessmentItemMeta(String id) {
        this.id = id;
        this.learningStandardIds = new ArrayList<String>();
    }

    public static AssessmentItemMeta create(String id) {
        return new AssessmentItemMeta(id);
    }

    @Override
    public String toString() {
        return "AssesssmentItemMeta [id=" + id + ", learningStandardIds=" + learningStandardIds + "]";
    }

}
