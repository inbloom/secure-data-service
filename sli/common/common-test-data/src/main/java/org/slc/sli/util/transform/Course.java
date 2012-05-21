package org.slc.sli.util.transform;

import java.util.ArrayList;

/**
 * 
 * @author dwilliams
 * 
 */
public class Course implements MongoDataEmitter {
    private String courseDef = null;
    private String adoptionDate = null;
    private String courseDesc = null;
    private String courseChar = null;
    private boolean required = false;
    private String courseCode = null;
    private String gpaApplicability = null;
    private ArrayList<String> grades = new ArrayList<String>();
    private String idSystem = null;
    private int minCredit = 0;
    private int maxCredit = 0;
    private String subject = null;
    private String level = null;
    private String courseTitle = null;
    private int parts = 0;
    private String orgCode = null;
    private String careerPath = null;
    
    private String generatedUuid = null;
    
    private ArrayList<SessionAssociation> sessionAssociations = new ArrayList<SessionAssociation>();
    private ArrayList<SectionAssociation> sectionAssociations = new ArrayList<SectionAssociation>();
    
    public Course(String courseDefinedBy, String adoptionDate, String description, String characteristics,
            boolean required, String code, String gpaAppl, String idSystem, int minCredit, int maxCredit) {
        this.courseDef = courseDefinedBy;
        this.adoptionDate = adoptionDate;
        this.courseDesc = description;
        this.courseChar = characteristics;
        this.required = required;
        this.courseCode = code;
        this.gpaApplicability = gpaAppl;
        this.idSystem = idSystem;
        this.minCredit = minCredit;
        this.maxCredit = maxCredit;
        
        generatedUuid = Base64.nextUuid("aacc");
    }
    
    public void setAdditionalParametersThatExceedFascistCheckstyleLimitOnConstructorArgs(String subject, String level,
            String title, int parts, String orgCode, String careerPath) {
        this.subject = subject;
        this.level = level;
        this.courseTitle = title;
        this.parts = parts;
        this.orgCode = orgCode;
        this.careerPath = careerPath;
    }
    
    public void addGrade(String grade) {
        grades.add(grade);
    }
    
    public String emitXml() {
        StringBuffer answer = new StringBuffer();
        answer.append("<Course>\n");
        answer.append("   <CourseTitle>").append(courseTitle).append("</CourseTitle>\n");
        answer.append("   <NumberOfParts>").append(parts).append("</NumberOfParts>\n");
        answer.append("   <LocalCourseCode>").append(courseCode).append("</LocalCourseCode>\n");
        
        answer.append("   <GradesOffered>\n");
        for (int i = 0; i < grades.size(); i++) {
            String grade = grades.get(i);
            answer.append("      <GradeLevel>").append(grade).append("</GradeLevel>\n");
        }
        answer.append("   </GradesOffered>\n");
        
        answer.append("   <EducationOrganizationReference>\n");
        answer.append("      <EducationalOrgIdentity>\n");
        answer.append("         <StateOrganizationId>").append(orgCode).append("</StateOrganizationId>\n");
        answer.append("      </EducationalOrgIdentity>\n");
        answer.append("   </EducationOrganizationReference>\n");
        answer.append("</Course>\n");
        /*
         * <Course>
         * <CourseTitle>English Language Arts 4</CourseTitle>
         * <NumberOfParts>1</NumberOfParts>
         * <LocalCourseCode>ELA4</LocalCourseCode>
         * <GradesOffered>
         * <GradeLevel>Fourth grade</GradeLevel>
         * </GradesOffered>
         * <EducationOrganizationReference>
         * <EducationalOrgIdentity>
         * <StateOrganizationId>152901</StateOrganizationId>
         * </EducationalOrgIdentity>
         * </EducationOrganizationReference>
         * </Course>
         */
        return answer.toString();
    }
    
    @Override
    public String emit() {
        if (Configuration.getOutputType().equals(Configuration.OutputType.EdFiXml)) {
            return emitXml();
        }
        // {"_id":{"$binary":"EUEZNYFxd1OZmEJQk1IQkg==","$type":"03"},"type":"course","body":{"courseDefinedBy":"NAAFP","dateCourseAdopted":"2000-01-01","courseDescription":
        // "Intro to French","courseLevelCharacteristics":"AP","highSchoolCourseRequirement":false,"courseCode.id":"FR1","courseGpaApplicability":"Normal","gradesOffered":
        // ["Ninth_grade","Tenth_grade","Eleventh_grade"],"courseCode.identificationSystem":"Fran's ID System","maximumAvailableCredit":1,"subjectArea":"Foreign Language and Literature",
        // "courseLevel":"Basic","minimumAvailableCredit":1,"courseTitle":"French 1","numberOfParts":1,"courseCode.assigningOrganizationCode":"Fran's Code Generator",
        // "careerPathway":"Hospitality and Tourism"},"tenantId":"Zork"}
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"course\",\"body\":{\"courseDefinedBy\":\"").append(courseDef)
                .append("\",\"dateCourseAdopted\":\"").append(adoptionDate).append("\",\"courseDescription\":\"")
                .append(courseDesc).append("\",\"courseLevelCharacteristics\":\"").append(courseChar)
                .append("\",\"highSchoolCourseRequirement\":").append(required).append(",\"courseCode.id\":\"")
                .append(courseCode).append("\",\"courseGpaApplicability\":\"").append(gpaApplicability)
                .append("\",\"gradesOffered\":[");
        
        for (int i = 0; i < grades.size(); i++) {
            String grade = grades.get(i);
            if (i > 0) {
                answer.append(",");
            }
            answer.append("\"").append(grade).append("\"");
        }
        
        answer.append("],\"courseCode.identificationSystem\":\"").append(idSystem)
                .append("\",\"maximumAvailableCredit\":").append(maxCredit).append(",\"subjectArea\":\"")
                .append(subject).append("\",\"courseLevel\":\"").append(level).append("\",\"minimumAvailableCredit\":")
                .append(minCredit).append(",\"courseTitle\":\"").append(courseTitle).append("\",\"numberOfParts\":")
                .append(parts).append(",\"courseCode.assigningOrganizationCode\":\"").append(orgCode)
                .append("\",\"careerPathway\":\"").append(careerPath).append("\"},\"tenantId\":\"Zork\"}\n");
        
        return answer.toString();
    }
    
    public void createSessionAssociation(Session session, String lcc, String lct) {
        SessionAssociation sa = new SessionAssociation(session, lcc, lct);
        sessionAssociations.add(sa);
    }
    
    public String emitSessionAssociations() {
        StringBuffer answer = new StringBuffer();
        for (SessionAssociation sa : sessionAssociations) {
            answer.append(sa.emit());
        }
        return answer.toString();
    }
    
    private class SessionAssociation {
        private Session session = null;
        private String localCourseCode = null;
        private String localCourseTitle = null;
        
        private String generatedUuid = null;
        
        public SessionAssociation(Session session, String lcc, String lct) {
            this.session = session;
            this.localCourseCode = lcc;
            this.localCourseTitle = lct;
            
            generatedUuid = Base64.nextUuid("aace");
        }
        
        public String emit() {
            // {"_id":{"$binary":"iEWL77Fb9p9ov8E5j9Wvgw==","$type":"03"},"type":"courseOffering","body":{"localCourseCode":"LCCGR1",
            // "sessionId":"389b0caa-dcd2-4e84-93b7-daa4a6e9b18e","localCourseTitle":"German 1 - Intro to German","courseId":"93d33f0b-0f2e-43a2-b944-7d182253a79a"},"tenantId":"Zork"}
            StringBuffer answer = new StringBuffer();
            answer.append("{\"_id\":{\"$binary\":\"")
                    .append(Base64.toBase64(generatedUuid))
                    .append("\",\"$type\":\"03\"},\"type\":\"courseOffering\",\"body\":{\"localCourseCode\":\"")
                    .append(localCourseCode).append("\",\"sessionId\":\"").append(session.getUuid())
                    .append("\",\"localCourseTitle\":\"").append(localCourseTitle).append("\",\"courseId\":\"")
                    .append(Course.this.generatedUuid).append("\"},\"tenantId\":\"Zork\"}\n");
            
            return answer.toString();
        }
    }
    
    public void createSectionAssociation(Section section) {
        SectionAssociation sa = new SectionAssociation(section);
        sectionAssociations.add(sa);
        section.setCourse(this);
    }
    
    public String emitSectionAssociations() {
        StringBuffer answer = new StringBuffer();
        for (SectionAssociation sa : sectionAssociations) {
            answer.append(sa.emit());
        }
        return answer.toString();
    }
    
    private class SectionAssociation {
        private Section section = null;
        
        private String generatedUuid = null;
        
        // course id,uniqueSectionCode
        // 5,math-8-6-1
        
        public SectionAssociation(Section section) {
            this.section = section;
            
            generatedUuid = Base64.nextUuid("aacf");
        }
        
        public String emit() {
            // {"_id":{"$binary":"EUqZmUsgzmcAAAAAAADKqg==","$type":"03"},"type":"sectionCourseAssociation","body":{"courseId":
            // "67ce204b-9999-4a11-aabe-000000000000","sectionId":"1d303c61-88d4-404a-ba13-d7c5cc324bc5"},"tenantId":"Zork"}
            
            StringBuffer answer = new StringBuffer();
            answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                    .append("\",\"$type\":\"03\"},\"type\":\"sectionCourseAssociation\",\"body\":{\"courseId\":\"")
                    .append(Course.this.generatedUuid).append("\",\"sectionId\":\"").append(section.getUuid())
                    .append("\"},\"tenantId\":\"Zork\"}\n");
            
            return answer.toString();
        }
    }
    
    public String getUuid() {
        return generatedUuid;
    }
}
