package org.slc.sli.util.transform;

import java.util.ArrayList;
import java.util.HashMap;

public class School implements MongoDataEmitter {
    // {"_id":{"$binary":"I9+C9TWMO+vyGZokR2kG5A==","$type":"03"},"type":"school","body":{"stateOrganizationId":152901001,
    // "nameOfInstitution":"Apple Alternative Elementary School","organizationCategories":["School"],"address":[{"addressType":"Physical",
    // "streetNumberName":"123 Main Street","city":"Lebanon","stateAbbreviation":"KS","postalCode":"66952","nameOfCounty":"Smith County"}],
    // "telephone":[{"institutionTelephoneNumberType":"MAIN","telephoneNumber":"(785) 667-6006"}],
    // "gradesOffered":["Third_grade","Fifth_grade","Fourth_grade","Sixth_grade"],"schoolCategories":["Elementary_School"]}}
    
    private String name = null;
    private String id = null;
    private String street = null;
    private String city = null;
    private String state = null;
    private String postalCode = null;
    private String county = "Wake";	// default
    private String telephone = null;
    private int gradesOfferedStart = 0;
    private int gradesOfferedEnd = 0;
    private String schoolCategories = null;
    
    private String generatedUuid = null;
    private String generatedEdOrgAssociationId = null;	// this ID is already Base64 encoded!!!
    private HashMap<String, String> idHackMap = null;
    private EducationalOrganization educationalOrganization = null;
    
    private ArrayList<SessionAssociation> sessionAssociations = new ArrayList<SessionAssociation>();
    
    public School(String name, String id, String street, String city, String state, String zip, String county,
            String phone, String gradesOffered, String categories) {
        this.name = name;
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = zip;
        this.county = county;
        this.telephone = phone;
        this.schoolCategories = categories;
        
        String[] grades = gradesOffered.split("-");
        if (grades[0].equalsIgnoreCase("K")) {
            gradesOfferedStart = 0;
        } else {
            gradesOfferedStart = Integer.parseInt(grades[0]);
        }
        gradesOfferedEnd = Integer.parseInt(grades[1]);
        
        generatedUuid = Base64.nextUuid("aaab");
        generatedEdOrgAssociationId = getEdOrgAssociationId(id);
    }
    
    public String getStateUniqueId() {
        return id;
    }
    
    // this is a hack to deal with fixture data that was created manually
    private String getEdOrgAssociationId(String id) {
        if (idHackMap == null) {
            idHackMap = new HashMap<String, String>();
            idHackMap.put("1", "9kKx3B/aoQC6cbBdCCVmmQ==");
            idHackMap.put("2", "tEbexpraGZgml1xYdqnmpw==");
            idHackMap.put("3", "MUzyhCyMYODgApNlGj5oug==");
            idHackMap.put("4", "fU4t1E4F8tWBqhtwSHFkgg==");
            idHackMap.put("5", "L03NYx6385mML1eOrKsLmA==");
            idHackMap.put("6", "uki50HsWZWbdGZcZ2LFHqA==");
            idHackMap.put("7", "XEXrYxznAXfsX551lw35tw==");
            idHackMap.put("8", "jkISUuVwOuazRqTK5Cffng==");
            idHackMap.put("9", "SEeCxN8pthx3dQDSXUJKhw==");
        }
        String answer = idHackMap.get(id);
        if (answer == null) {
            answer = Base64.toBase64(Base64.nextUuid("aabf"));
        }
        return answer;
    }
    
    public void setEdOrg(EducationalOrganization edOrg) {
        this.educationalOrganization = edOrg;
    }
    
    public String emitXml() {
        StringBuffer answer = new StringBuffer();
        answer.append("<School>\n");
        answer.append("   <StateOrganizationId>").append(id).append("</StateOrganizationId>\n");
        answer.append("   <NameOfInstitution>").append(name).append("</NameOfInstitution>\n");
        answer.append("   <OrganizationCategory>School</OrganizationCategory>\n");
        answer.append("   <Address AddressType=\"Physical\">\n");
        answer.append("      <StreetNumberName>").append(street).append("</StreetNumberName>\n");
        answer.append("      <City>").append(city).append("</City>\n");
        answer.append("      <StateAbbreviation>").append(state).append("</StateAbbreviation>\n");
        answer.append("      <PostalCode>").append(postalCode).append("</PostalCode>\n");
        answer.append("      <NameOfCounty>\"\"</NameOfCounty>\n");
        answer.append("   </Address>\n");
        answer.append("   <Telephone InstitutionTelephoneNumberType=\"Main\">\n");
        answer.append("      <TelephoneNumber>").append(telephone).append("</TelephoneNumber>\n");
        answer.append("   </Telephone>\n");
        answer.append("   <GradesOffered>\n");
        
        for (int i = gradesOfferedStart; i <= gradesOfferedEnd; i++) {
            answer.append("      <GradeLevel>");
            switch (i) {
            case 0:
                answer.append("Kindergarten");
                break;
            case 1:
                answer.append("First grade");
                break;
            case 2:
                answer.append("Second grade");
                break;
            case 3:
                answer.append("Third grade");
                break;
            case 4:
                answer.append("Fourth grade");
                break;
            case 5:
                answer.append("Fifth grade");
                break;
            case 6:
                answer.append("Sixth grade");
                break;
            case 7:
                answer.append("Seventh grade");
                break;
            case 8:
                answer.append("Eighth grade");
                break;
            case 9:
                answer.append("Ninth grade");
                break;
            case 10:
                answer.append("Tenth grade");
                break;
            case 11:
                answer.append("Eleventh grade");
                break;
            case 12:
                answer.append("Twelfth grade");
                break;
            }
            answer.append("</GradeLevel>\n");
        }
        answer.append("   </GradesOffered>\n");
        answer.append("   <SchoolCategories>\n");
        if (gradesOfferedEnd < 7) {
            answer.append("   <SchoolCategory>Elementary School</SchoolCategory>\n");
        } else if (gradesOfferedStart >= 5 && gradesOfferedEnd < 10) {
            answer.append("   <SchoolCategory>Middle School</SchoolCategory>\n");
        } else if (gradesOfferedStart >= 8) {
            answer.append("   <SchoolCategory>High School</SchoolCategory>\n");
        }
        answer.append("   </SchoolCategories>\n");
        if (educationalOrganization != null) {
            answer.append("   <LocalEducationAgencyReference>\n");
            answer.append("      <EducationalOrgIdentity>\n");
            answer.append("         <StateOrganizationId>").append(educationalOrganization.getStateId())
                    .append("</StateOrganizationId>\n");
            answer.append("      </EducationalOrgIdentity>\n");
            answer.append("   </LocalEducationAgencyReference>\n");
        }
        answer.append("</School>\n");
        /*
         * <School>
         * <StateOrganizationId>152901001</StateOrganizationId>
         * <NameOfInstitution>Apple Alternative Elementary School</NameOfInstitution>
         * <OrganizationCategory>School</OrganizationCategory>
         * <Address AddressType="Physical">
         * <StreetNumberName>123 Main Street</StreetNumberName>
         * <City>Lebanon</City>
         * <StateAbbreviation>KS</StateAbbreviation>
         * <PostalCode>66952</PostalCode>
         * <NameOfCounty>Smith County</NameOfCounty>
         * </Address>
         * <Telephone InstitutionTelephoneNumberType="Main">
         * <TelephoneNumber>(785) 667-6006</TelephoneNumber>
         * </Telephone>
         * <GradesOffered>
         * <GradeLevel>Third grade</GradeLevel>
         * <GradeLevel>Fifth grade</GradeLevel>
         * <GradeLevel>Fourth grade</GradeLevel>
         * <GradeLevel>Sixth grade</GradeLevel>
         * </GradesOffered>
         * <SchoolCategories>
         * <SchoolCategory>Elementary School</SchoolCategory>
         * </SchoolCategories>
         * <LocalEducationAgencyReference>
         * <EducationalOrgIdentity>
         * <StateOrganizationId>152901</StateOrganizationId>
         * </EducationalOrgIdentity>
         * </LocalEducationAgencyReference>
         * </School>
         */
        return answer.toString();
    }
    
    @Override
    public String emit() {
        if (Configuration.outputType == Configuration.OutputType.EdFiXml) {
            return emitXml();
        }
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"");
        answer.append(Base64.toBase64(generatedUuid));
        answer.append("\",\"$type\":\"03\"},\"type\":\"school\",\"body\":{\"stateOrganizationId\":\"");
        answer.append(id);
        answer.append("\",\"nameOfInstitution\":\"");
        answer.append(name);
        answer.append("\",\"organizationCategories\":[\"School\"],\"address\":[{\"addressType\":\"Physical\"");
        answer.append(",\"streetNumberName\":\"");
        answer.append(street);
        answer.append("\",\"city\":\"");
        answer.append(city);
        answer.append("\",\"stateAbbreviation\":\"");
        answer.append(state);
        answer.append("\",\"postalCode\":\"");
        answer.append(postalCode);
        answer.append("\",\"nameOfCounty\":\"Wake\"}],\"telephone\":[{\"institutionTelephoneNumberType\":\"MAIN\",\"telephoneNumber\":\"");
        answer.append(telephone);
        answer.append("\"}],\"gradesOffered\":[");
        
        for (int i = gradesOfferedStart; i <= gradesOfferedEnd; i++) {
            if (i > gradesOfferedStart) {
                answer.append(",");
            }
            switch (i) {
            case 0:
                answer.append("\"Kindergarten\"");
                break;
            case 1:
                answer.append("\"First grade\"");
                break;
            case 2:
                answer.append("\"Second grade\"");
                break;
            case 3:
                answer.append("\"Third grade\"");
                break;
            case 4:
                answer.append("\"Fourth grade\"");
                break;
            case 5:
                answer.append("\"Fifth grade\"");
                break;
            case 6:
                answer.append("\"Sixth grade\"");
                break;
            case 7:
                answer.append("\"Seventh grade\"");
                break;
            case 8:
                answer.append("\"Eighth grade\"");
                break;
            case 9:
                answer.append("\"Ninth grade\"");
                break;
            case 10:
                answer.append("\"Tenth grade\"");
                break;
            case 11:
                answer.append("\"Eleventh grade\"");
                break;
            case 12:
                answer.append("\"Twelfth grade\"");
                break;
            }
        }
        answer.append("],\"schoolCategories\":[\"");
        answer.append(schoolCategories);
        answer.append("\"]}}\n");
        return answer.toString();
    }
    
    public String emitEdOrgAssociation() {
        // {"_id":{"$binary":"XEXrYxznAXfsX551lw35tw==","$type":"03"},"type":"educationOrganizationSchoolAssociation","body":
        // {"schoolId":"67ce204b-9999-4a11-aaab-000000000006","educationOrganizationId":"1d303c61-88d4-404a-ba13-d7c5cc324bc5"},"metadata":{}}
        
        if (educationalOrganization == null) {
            return ""; // incomplete dashboard test data?
        }
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(generatedEdOrgAssociationId)
                .append("\",\"$type\":\"03\"},\"type\":\"educationOrganizationSchoolAssociation\",\"body\":")
                .append("{\"schoolId\":\"").append(generatedUuid).append("\",\"educationOrganizationId\":\"")
                .append(educationalOrganization.getId()).append("\"},\"metadata\":{}}\n");
        
        return answer.toString();
    }
    
    public String getUuid() {
        return generatedUuid;
    }
    
    public void createSessionAssociation(Session session, String gpName, String beginDate, String endDate, int days) {
        SessionAssociation sa = new SessionAssociation(session, gpName, beginDate, endDate, days);
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
        private String gradingPeriodName = null;
        private String beginDate = null;
        private String endDate = null;
        private int instructionDays = 0;
        
        private String generatedUuid = null;
        
        public SessionAssociation(Session session, String gpName, String beginDate, String endDate, int days) {
            this.session = session;
            this.gradingPeriodName = gpName;
            this.beginDate = beginDate;
            this.endDate = endDate;
            this.instructionDays = days;
            
            generatedUuid = Base64.nextUuid("aacd");
        }
        
        public String emit() {
            // {"_id":{"$binary":"rUl/bu3aveqM87vwbxcorg==","$type":"03"},"type":"schoolSessionAssociation","body":{"sessionId":"cb1fb2d3-a906-446a-bdfd-06ad23823265",
            // "gradingPeriod":{"name":"Spring 2011 at Chemistry Elementary","beginDate":"2011-01-01","endDate":"2011-06-31","totalInstructionalDays":90},
            // "schoolId":"b6ad1eb2-3cf7-41c4-96e7-2f393f0dd847"},"tenantId":"Zork"}
            StringBuffer answer = new StringBuffer();
            answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                    .append("\",\"$type\":\"03\"},\"type\":\"schoolSessionAssociation\",\"body\":{\"sessionId\":\"")
                    .append(session.getUuid()).append("\",\"gradingPeriod\":{\"name\":\"").append(gradingPeriodName)
                    .append("\",\"beginDate\":\"").append(beginDate).append("\",\"endDate\":\"").append(endDate)
                    .append("\",\"totalInstructionalDays\":").append(instructionDays).append("},\"schoolId\":\"")
                    .append(School.this.getUuid()).append("\"},\"tenantId\":\"Zork\"}\n");
            
            return answer.toString();
        }
    }
    
    public EducationalOrganization getEdOrg() {
        return educationalOrganization;
    }
    
    public void mergeSessionAssociationsFrom(School other) {
        if (other.sessionAssociations == null) {
            return;
        }
        for (SessionAssociation otherSA : other.sessionAssociations) {
            sessionAssociations.add(otherSA);
        }
    }
}
