package org.slc.sli.test.edfi.entities.meta;

public class GraduationPlanMeta {
	public final String id;

	public String schoolIds;
	
	public GraduationPlanMeta (String id, SchoolMeta schoolMeta){
		this.id = schoolMeta.id + "-" + id;
		this.schoolIds = schoolMeta.id;
	}
}
