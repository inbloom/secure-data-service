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

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptor;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;

public class CompetancyLevelDescriptorGenerator {
    
    private static final Logger log = Logger.getLogger(CompetancyLevelDescriptorGenerator.class);
    
    public CompetencyLevelDescriptor getCompetancyLevelDescriptorGenerator(String id, PerformanceBaseType pbt)
    {
        CompetencyLevelDescriptor descriptor = new CompetencyLevelDescriptor();
        descriptor.setId(id);
        descriptor.setCodeValue("CLD-1"); 
        descriptor.setDescription("CLD-2"); 
        descriptor.setPerformanceBaseConversion(pbt) ;
        return descriptor;
    }
}
