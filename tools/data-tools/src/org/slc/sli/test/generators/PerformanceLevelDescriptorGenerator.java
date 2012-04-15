package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptor;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.meta.PerformanceLevelDescriptorMeta;

public class PerformanceLevelDescriptorGenerator {

    // TODO: maybe make public and move to one common generator class for use by all
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static PerformanceLevelDescriptor generateLowFi(PerformanceLevelDescriptorMeta perfLevelDescMeta) {
        PerformanceLevelDescriptor pld = new PerformanceLevelDescriptor();
        pld.setCodeValue(perfLevelDescMeta.id);
        pld.setDescription("Description for " + perfLevelDescMeta.id);
        pld.setPerformanceBaseConversion(PerformanceBaseType.BASIC);
        return pld;
    }

    /**
     * Provides references for performance level descriptors during interchange.
     *
     * @return
     */
    public static PerformanceLevelDescriptorType getPerformanceLevelDescriptorType(String perfLevelDescCodeValue) {
        PerformanceLevelDescriptorType perfLevelDescType = new PerformanceLevelDescriptorType();

        perfLevelDescType.getCodeValueOrDescription().add(
                OBJECT_FACTORY.createPerformanceLevelDescriptorTypeCodeValue(perfLevelDescCodeValue));
        perfLevelDescType.getCodeValueOrDescription().add(
                OBJECT_FACTORY.createPerformanceLevelDescriptorTypeDescription(perfLevelDescCodeValue));
        return perfLevelDescType;
    }
}
