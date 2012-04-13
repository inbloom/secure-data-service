package org.slc.sli.test.edfi.entities.meta;

public class AssessmentPeriodDescriptorMeta {

    public final String id;

    private AssessmentPeriodDescriptorMeta(String id) {
        this.id = id;
    }

    public static AssessmentPeriodDescriptorMeta create(String id) {
        return new AssessmentPeriodDescriptorMeta(id);
    }

    @Override
    public String toString() {
        return "AssessmentPeriodDescriptorMeta [id=" + id + "]";
    }

}
