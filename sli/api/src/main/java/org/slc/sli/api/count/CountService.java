package org.slc.sli.api.count;

import java.util.List;

public interface CountService {
	
	public List<EducationOrganizationCount> find();
	public EducationOrganizationCount findOne(String edOrgId);

}
