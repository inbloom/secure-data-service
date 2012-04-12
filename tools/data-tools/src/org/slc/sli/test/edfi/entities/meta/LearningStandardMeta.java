package org.slc.sli.test.edfi.entities.meta;

public final class LearningStandardMeta {

    public final String id;

    private LearningStandardMeta(String id) {
        this.id = id;
    }

    public static LearningStandardMeta create(String id) {
        return new LearningStandardMeta(id);
    }

    @Override
    public String toString() {
        return "LearningStandardMeta [id=" + id + "]";
    }

}
