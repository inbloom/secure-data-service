package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class LearningObjectiveMeta {

    public final String id;
    public final List<String> learningStandardIds;
    public final List<String> learningObjectiveMetaIds;

    private LearningObjectiveMeta(String id) {
        this.id = id;
        this.learningStandardIds = new ArrayList<String>();
        this.learningObjectiveMetaIds = new ArrayList<String>();
    }

    public static LearningObjectiveMeta create(String id) {
        return new LearningObjectiveMeta(id);
    }

    @Override
    public String toString() {
        return "LearningObjectiveMeta [id=" + id + ", learningStandardIds=" + learningStandardIds
                + ", learningObjectiveMetaIds=" + learningObjectiveMetaIds + "]";
    }

}
