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
