package org.slc.sli.test.edfi.entities.meta;

public final class PerformanceLevelDescriptorMeta {

    public final String id;

    private PerformanceLevelDescriptorMeta(String id) {
        this.id = id;
    }

    public static PerformanceLevelDescriptorMeta create(String id) {
        return new PerformanceLevelDescriptorMeta(id);
    }

    @Override
    public String toString() {
        return "PerformanceLevelDescriptorMeta [id=" + id + "]";
    }

}
