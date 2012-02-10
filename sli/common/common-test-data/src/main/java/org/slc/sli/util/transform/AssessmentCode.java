package org.slc.sli.util.transform;

public class AssessmentCode implements MongoDataEmitter 
{
	private String idSystem = null;
	private String id = null;
	
	public AssessmentCode(String id)
	{
		this("School", id);
	}
	
	public AssessmentCode(String system, String id)
	{
		this.idSystem = system;
		this.id = id;
	}
	
	@Override
	public String emit()
	{
		// "assessmentIdentificationCode":[{"identificationSystem":"School","id":"01234B"}]
		StringBuffer answer = new StringBuffer();
		answer.append("{\"identificationSystem\":\"").append(idSystem).append("\",\"id\":\"").append(id).append("\"}");
		return answer.toString();
	}
}
