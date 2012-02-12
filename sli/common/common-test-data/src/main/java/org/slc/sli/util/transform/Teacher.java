package org.slc.sli.util.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author dwilliams
 * 
 */
public class Teacher implements MongoDataEmitter {
    private String generatedUuid = null;
    
    private TeacherName name = null;
    private ArrayList<TeacherOtherName> otherNames = null;
    private String sex = null;
    private String birthDate = "";
    private ArrayList<TeacherAddress> addresses = new ArrayList<TeacherAddress>();
    private ArrayList<TeacherPhone> phones = new ArrayList<TeacherPhone>();
    private ArrayList<TeacherEmail> emails = new ArrayList<TeacherEmail>();
    private String edLevel = null;
    private int years = 1;
    private int priorYears = 0;
    private ArrayList<TeacherCredential> credentials = new ArrayList<TeacherCredential>();
    private String uniqueStateId = null;
    private HashMap<String, String> sectionIds = new HashMap<String, String>();
    
    public Teacher(String staffUniqueStateId, String firstName, String lastSurname, String sex,
            String highestLevelOfEducationCompleted) {
        generatedUuid = Base64.nextUuid("aabc");
        this.uniqueStateId = staffUniqueStateId;
        this.name = new TeacherName("", firstName, "", lastSurname, "", "");
        this.sex = sex;
        this.credentials = new ArrayList<TeacherCredential>();
        this.credentials.add(new TeacherCredential("LICENSURE", "Mathematics", highestLevelOfEducationCompleted,
                "Professional", "1998-01-01"));
        this.edLevel = highestLevelOfEducationCompleted;
    }
    
    public void addAddress(String type, String street, String suite, String site, String city, String state,
            String postalCode, String county) {
        TeacherAddress address = new TeacherAddress(type, street, suite, site, city, state, postalCode, county);
        addresses.add(address);
    }
    
    public void addPhone(String type, String number, boolean isPrimary) {
        TeacherPhone phone = new TeacherPhone(type, number, isPrimary);
        phones.add(phone);
    }
    
    public void addEmail(String type, String email) {
        TeacherEmail emailObj = new TeacherEmail(type, email);
        emails.add(emailObj);
    }
    
    public void addCredential(String credentialType, String field, String level, String teachingCredentialType,
            String credentialIssuanceDate) {
        TeacherCredential cred = new TeacherCredential(credentialType, field, level, teachingCredentialType,
                credentialIssuanceDate);
        credentials.add(cred);
    }
    
    public String getUuid() {
        return generatedUuid;
    }
    
    public void addSectionId(String id) {
        sectionIds.put(id, Base64.nextUuid("aabd"));
    }
    
    public String emitSectionAssociation() {
        // {"_id":{"$binary":"ContextTeach/Class202Q==","$type":"03"},"type":"teacherSectionAssociation",
        // "body":{"sectionId":"eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d","classroomPosition":"TEACHER_OF_RECORD","teacherId":"eb4d7e1b-7bed-890a-d974-1d729a37fd2d"},
        // "metadata":{},"tenantId":"Zork"}
        
        StringBuffer answer = new StringBuffer();
        Iterator<String> iter = sectionIds.keySet().iterator();
        while (iter.hasNext()) {
            String sectionId = iter.next();
            String tsaUuid = sectionIds.get(sectionId);
            answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(tsaUuid))
                    .append("\",\"$type\":\"03\"},\"type\":\"teacherSectionAssociation\",")
                    .append("\"body\":{\"sectionId\":\"").append(sectionId)
                    .append("\",\"classroomPosition\":\"Teacher of Record\",\"teacherId\":\"").append(generatedUuid)
                    .append("\"},\"metadata\":{},\"tenantId\":\"Zork\"}\n");
        }
        return answer.toString();
    }
    
    public String emitXml() {
        StringBuffer answer = new StringBuffer();
        answer.append("<Teacher>\n");
        answer.append("   <StaffUniqueStateId>").append(uniqueStateId).append("</StaffUniqueStateId>\n");
        answer.append("   <StaffIdentificationCode IdentificationSystem=\"State\">\n");
        if (addresses != null && addresses.size() > 0) {
            answer.append("      <ID>").append(addresses.get(0).state).append("</ID>\n");
        }
        answer.append("   </StaffIdentificationCode>\n");
        answer.append("   <Name>\n");
        if (name != null) {
            answer.append("      <FirstName>").append(name.firstName).append("</FirstName>\n");
            answer.append("      <LastSurname>").append(name.lastName).append("</LastSurname>\n");
        }
        answer.append("   </Name>\n");
        answer.append("   <Sex>").append(sex).append("</Sex>\n");
        answer.append("   <HispanicLatinoEthnicity>").append(false).append("</HispanicLatinoEthnicity>\n");
        answer.append("   <HighestLevelOfEducationCompleted>").append(edLevel)
                .append("</HighestLevelOfEducationCompleted>\n");
        answer.append("   <YearsOfPriorProfessionalExperience>").append(priorYears)
                .append("</YearsOfPriorProfessionalExperience>\n");
        answer.append("</Teacher>\n");
        /*
         * <Teacher>
         * <StaffUniqueStateId>111</StaffUniqueStateId>
         * <StaffIdentificationCode IdentificationSystem="District">
         * <ID>111111111</ID>
         * </StaffIdentificationCode >
         * <Name>
         * <FirstName>Jan</FirstName>
         * <LastSurname>Richards</LastSurname>
         * </Name>
         * <Sex>Female</Sex>
         * <HispanicLatinoEthnicity>false</HispanicLatinoEthnicity>
         * <Race/>
         * <HighestLevelOfEducationCompleted>Bachelor's</HighestLevelOfEducationCompleted>
         * <YearsOfPriorProfessionalExperience>25</YearsOfPriorProfessionalExperience>
         * </Teacher>
         */
        return answer.toString();
    }
    
    @Override
    public String emit() {
        if (Configuration.getOutputType().equals(Configuration.OutputType.EdFiXml)) {
            return emitXml();
        }
        // {"_id":{"$binary":"PmRlKik+QI+yDQBTy1ysbw==","$type":"03"},"type":"teacher","stateId":"dc=slidev,dc=net",
        // "body":{"name":{"personalTitlePrefix":null,"firstName":"Bert","middleName":"","lastSurname":"Munoz","generationCodeSuffix":null,"maidenName":""},
        // "otherName":[{"otherNameType":"ALIAS","personalTitlePrefix":"MR","firstName":"","middleName":"","lastSurname":"","generationCodeSuffix":null}],"sex":"Male",
        // "birthDate":"1950-01-01",
        // "address":[{"addressType":"Physical","streetNumberName":"","apartmentRoomSuiteNumber":"","buildingSiteNumber":"","city":"","stateAbbreviation":"NY","postalCode":"","nameOfCounty":""}],
        // "telephone":[{"telephoneNumberType":"HOME","telephoneNumber":"","primaryTelephoneNumberIndicator":true}],
        // "electronicMail":[{"emailAddressType":"HOME_PERSONAL","emailAddress":""}],
        // "hispanicLatinoEthnicity":true,"highestLevelOfEducationCompleted":"Doctorate","yearsOfPriorProfessionalExperience":10,"yearsOfPriorTeachingExperience":5,
        // "credentials":[{"credentialType":"LICENSURE","field":"Mathematics","level":"ALL_LEVEL_GRADE_LEVEL_PK_12","teachingCredentialType":"Professional","credentialIssuanceDate":"1998-01-01"}],
        // "staffUniqueStateId":725286880," highlyQualifiedTeacher":false},"tenantId":"Zork"}
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"teacher\",\"stateId\":\"dc=slidev,dc=net\",")
                .append("\"body\":{\"name\":").append(name.emit()).append(",");
        
        if (otherNames != null) {
            answer.append("\"otherName\":[");
            boolean firstTime = true;
            for (TeacherOtherName other : otherNames) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    answer.append(",");
                }
                answer.append(other.emit());
            }
            answer.append("],");
        }
        
        answer.append("\"sex\":\"").append(sex).append("\",\"birthDate\":\"").append(birthDate).append("\",");
        
        if (addresses != null) {
            answer.append("\"address\":[");
            boolean firstTime = true;
            for (TeacherAddress address : addresses) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    answer.append(",");
                }
                answer.append(address.emit());
            }
            answer.append("],");
        }
        
        if (phones != null) {
            answer.append("\"telephone\":[");
            boolean firstTime = true;
            for (TeacherPhone phone : phones) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    answer.append(",");
                }
                answer.append(phone.emit());
            }
            answer.append("],");
        }
        
        if (emails != null) {
            answer.append("\"electronicMail\":[");
            boolean firstTime = true;
            for (TeacherEmail email : emails) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    answer.append(",");
                }
                answer.append(email.emit());
            }
            answer.append("],");
        }
        
        answer.append("\"hispanicLatinoEthnicity\":false,\"highestLevelOfEducationCompleted\":\"").append(edLevel)
                .append("\",\"yearsOfPriorProfessionalExperience\":").append(years)
                .append(",\"yearsOfPriorTeachingExperience\":").append(priorYears).append(",");
        
        if (credentials != null) {
            answer.append("\"credentials\":[");
            boolean firstTime = true;
            for (TeacherCredential cred : credentials) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    answer.append(",");
                }
                answer.append(cred.emit());
            }
            answer.append("],");
        }
        
        answer.append("\"staffUniqueStateId\":\"").append(uniqueStateId)
                .append("\",\"highlyQualifiedTeacher\":false},\"tenantId\":\"Zork\"}\n");
        
        return answer.toString();
    }
    
    private class TeacherAddress implements MongoDataEmitter {
        private String type = "Physical";
        private String street = "";
        private String suite = "";
        private String site = "";
        private String city = "";
        private String state = "";
        private String postalCode = "";
        private String county = "";
        
        public TeacherAddress(String type, String street, String suite, String site, String city, String state,
                String postalCode, String county) {
            this.type = type;
            this.street = street;
            this.suite = suite;
            this.site = site;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            this.county = county;
        }
        
        // {"addressType":"Physical","streetNumberName":"","apartmentRoomSuiteNumber":"","buildingSiteNumber":"","city":"","stateAbbreviation":"NY","postalCode":"","nameOfCounty":""}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"addressType\":\"").append(type).append("\",\"streetNumberName\":\"").append(street)
                    .append("\",\"apartmentRoomSuiteNumber\":\"").append(suite).append("\",\"buildingSiteNumber\":\"")
                    .append(site).append("\",\"city\":\"").append(city).append("\",\"stateAbbreviation\":\"")
                    .append(state).append("\",\"postalCode\":\"").append(postalCode).append("\",\"nameOfCounty\":\"")
                    .append(county).append("\"}");
            
            return answer.toString();
        }
    }
    
    private class TeacherEmail implements MongoDataEmitter {
        private String type = null;
        private String email = null;
        
        public TeacherEmail(String type, String email) {
            this.type = type;
            this.email = email;
        }
        
        // {"emailAddressType":"HOME_PERSONAL","emailAddress":""}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"emailAddressType\":\"").append(type).append("\",\"emailAddress\":\"").append(email)
                    .append("\"}");
            
            return answer.toString();
        }
    }
    
    private class TeacherPhone implements MongoDataEmitter {
        private String type = null;
        private String number = null;
        private boolean isPrimary = false;
        
        public TeacherPhone(String type, String number, boolean isPrimary) {
            this.type = type;
            this.number = number;
            this.isPrimary = isPrimary;
        }
        
        // {"telephoneNumberType":"HOME","telephoneNumber":"","primaryTelephoneNumberIndicator":true}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"telephoneNumberType\":\"").append(type).append("\",\"telephoneNumber\":\"")
                    .append(number).append("\",\"primaryTelephoneNumberIndicator\":").append(isPrimary).append("}");
            
            return answer.toString();
        }
    }
    
    private class TeacherCredential implements MongoDataEmitter {
        private String credentialType = null;
        private String field = null;
        private String level = null;
        private String teachingCredentialType = null;
        private String credentialIssuanceDate = null;
        
        public TeacherCredential(String credentialType, String field, String level, String teachingCredentialType,
                String credentialIssuanceDate) {
            this.credentialType = credentialType;
            this.field = field;
            this.level = level;
            this.teachingCredentialType = teachingCredentialType;
            this.credentialIssuanceDate = credentialIssuanceDate;
        }
        
        // {"credentialType":"LICENSURE","field":"Mathematics","level":"ALL_LEVEL_GRADE_LEVEL_PK_12","teachingCredentialType":"Professional","credentialIssuanceDate":"1998-01-01"}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"credentialType\":\"").append(credentialType).append("\",\"field\":\"").append(field)
                    .append("\",\"level\":\"").append(level).append("\",\"teachingCredentialType\":\"")
                    .append(teachingCredentialType).append("\",\"credentialIssuanceDate\":\"")
                    .append(credentialIssuanceDate).append("\"}");
            
            return answer.toString();
        }
    }
    
    private class TeacherName implements MongoDataEmitter {
        protected String prefix = null;
        protected String firstName = null;
        protected String middleName = null;
        protected String lastName = null;
        protected String generationCode = null;
        protected String maidenName = null;
        
        public TeacherName(String prefix, String first, String middle, String last, String generationCode,
                String maidenName) {
            this.prefix = prefix;
            this.firstName = first;
            this.middleName = middle;
            this.lastName = last;
            this.generationCode = generationCode;
            this.maidenName = maidenName;
        }
        
        // {"personalTitlePrefix":null,"firstName":"Bert","middleName":"","lastSurname":"Munoz","generationCodeSuffix":null,"maidenName":""}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"personalTitlePrefix\":").append(prefix == null ? "null" : "\"" + prefix + "\"")
                    .append(",\"firstName\":\"").append(firstName).append("\",\"middleName\":\"")
                    .append(middleName == null ? "" : middleName).append("\",\"lastSurname\":\"").append(lastName)
                    .append("\",\"generationCodeSuffix\":")
                    .append(generationCode == null ? "null" : "\"" + generationCode + "\"")
                    .append(",\"maidenName\":\"").append(maidenName == null ? "" : maidenName).append("\"}");
            
            return answer.toString();
        }
    }
    
    private class TeacherOtherName extends TeacherName {
        private String otherType = null;
        
        public TeacherOtherName(String prefix, String first, String middle, String last, String generationCode,
                String maidenName, String otherType) {
            super(prefix, first, middle, last, generationCode, maidenName);
            this.otherType = otherType;
        }
        
        // {"otherNameType":"ALIAS","personalTitlePrefix":"MR","firstName":"","middleName":"","lastSurname":"","generationCodeSuffix":null}
        @Override
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"otherNameType\":\"").append(otherType).append("\",\"personalTitlePrefix\":")
                    .append(prefix == null ? "null" : "\"" + prefix + "\"").append(",\"firstName\":\"")
                    .append(firstName).append("\",\"middleName\":\"").append(middleName == null ? "" : middleName)
                    .append("\",\"lastSurname\":\"").append(lastName).append("\",\"generationCodeSuffix\":")
                    .append(generationCode == null ? "null" : "\"" + generationCode + "\"").append("\"}");
            
            return answer.toString();
        }
    }
}
