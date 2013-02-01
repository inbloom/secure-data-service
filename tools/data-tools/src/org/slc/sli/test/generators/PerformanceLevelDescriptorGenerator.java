/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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

        perfLevelDescType.setCodeValue(perfLevelDescCodeValue);
//        perfLevelDescType.setDescription(perfLevelDescCodeValue);
        perfLevelDescType.setPerformanceLevelMet(true);
        return perfLevelDescType;
    }
}
