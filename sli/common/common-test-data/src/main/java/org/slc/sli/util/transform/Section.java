package org.slc.sli.util.transform;

public class Section implements MongoDataEmitter 
{
	private String generatedUuid = null;
	private String uniqueSectionCode = null;
	private String sectionName = null;
	private String sequenceOfCourse = null;
	private Assessment assessment = null;
	private School school = null;
	private Course course = null;
	private Session session = null;

	public Section(String uniqueId, String name, String sequence)
	{
		this.uniqueSectionCode = uniqueId;
		this.sectionName = name;
		this.sequenceOfCourse = sequence;
		generatedUuid = Base64.nextUuid("aaac");
	}
	
	public String getUuid()
	{
		return generatedUuid;
	}
	
	@Override
	public String emit() 
	{
		// {"_id":{"$binary":"Context+Test/Class102Q==","$type":"03"},"type":"section",
		// "body":{"educationalEnvironment":"Classroom","populationServed":"Regular_students","sequenceOfCourse":1,"uniqueSectionCode":"FHS-Science101",
		// "sessionId":"foo","courseId":"bar","schoolId":"qux",
		// "mediumOfInstruction":"Independent_study"},"metadata":{},"tenantId":"Zork"}

		StringBuffer answer = new StringBuffer();
		answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid)).append("\",\"$type\":\"03\"},\"type\":\"section\",").
			append("\"body\":{\"educationalEnvironment\":\"Classroom\",\"populationServed\":\"Regular Students\",\"sequenceOfCourse\":").append(sequenceOfCourse).
			append(",\"uniqueSectionCode\":\"").append(uniqueSectionCode).append("\",\"sessionId\":\"").append(session.getUuid()).
			append("\",\"courseId\":\"").append(course.getUuid()).append("\",\"schoolId\":\"").append(school.getUuid()).
			append("\",\"mediumOfInstruction\":\"Independent study\"},\"metadata\":{},\"tenantId\":\"Zork\"}\n");
		return answer.toString();
	}
	
	public String emitAssessmentAssociation()
	{
		// {"_id":{"$binary":"4UWgM2OPvIGXl+XZrT2WlQ==","$type":"03"},"type":"sectionAssessmentAssociation",
		// "body":{"sectionId":"1e1cdb04-2094-46b7-8140-e3e481013480","assessmentId":"df897f7a-7ac4-42e4-bcbc-8cc6fd88b91a"},"metadata":{}}

		StringBuffer answer = new StringBuffer();
		answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(Base64.nextUuid("aaad"))).append("\",\"$type\":\"03\"},\"type\":\"sectionAssessmentAssociation\",").
			append("\"body\":{\"sectionId\":\"").append(generatedUuid).append("\",\"assessmentId\":\"").append(assessment.getUuid()).append("\"},\"metadata\":{}}\n");
		return answer.toString();
	}

	public void setAssessment(Assessment a)
	{
		this.assessment  = a;
	}
	
	public String emitSchoolAssociation()
	{
		// {"_id":{"$binary":"4UWgM2OPvIGXl+XZrT2WlQ==","$type":"03"},"type":"sectionSchoolAssociation",
		// "body":{"sectionId":"1e1cdb04-2094-46b7-8140-e3e481013480","assessmentId":"df897f7a-7ac4-42e4-bcbc-8cc6fd88b91a"},"metadata":{}}

		StringBuffer answer = new StringBuffer();
		answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(Base64.nextUuid("aada"))).append("\",\"$type\":\"03\"},\"type\":\"sectionSchoolAssociation\",").
			append("\"body\":{\"sectionId\":\"").append(generatedUuid).append("\",\"schoolId\":\"").append(school.getUuid()).append("\"},\"metadata\":{}}\n");
		return answer.toString();
	}

	public void setSchool(School s)
	{
		this.school = s;
	}

	public void setCourse(Course course) 
	{
		this.course  = course;
	}

	public void setSession(Session session) 
	{
		this.session  = session;
	}
}
