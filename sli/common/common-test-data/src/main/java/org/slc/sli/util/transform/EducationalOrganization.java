package org.slc.sli.util.transform;

public class EducationalOrganization implements MongoDataEmitter {
    private String generatedUuid = null;
    private String name = null;
    private String street = null;
    private String suite = null;
    private String city = null;
    private String state = null;
    private String postalCode = null;
    private String stateOrganizationId = null;
    private String type = null;
    
    private EducationalOrganization parentEdOrg = null;
    private String parentAssociationUuid = null;
    
    public EducationalOrganization(String edOrgName, String street, String suite, String city, String state,
            String postalCode, String stateOrgId) {
        this.name = edOrgName;
        this.street = street;
        this.suite = suite;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.stateOrganizationId = stateOrgId;
        
        if (edOrgName.equals("Greater Smallville K-12 School District")) { // SkDUiGE8MB3FSzLMxdcTug==
            generatedUuid = "1d303c61-88d4-404a-ba13-d7c5cc324bc5";	// hack to deal with an id that
                                                                    // was created manually, that we
                                                                    // don't want to change
        } else {
            generatedUuid = Base64.nextUuid("aabe");
        }
    }
    
    public String getStateId() {
        return stateOrganizationId;
    }
    
    public void updateFrom(EducationalOrganization newEdOrg) {
        this.name = (newEdOrg.name != null) ? newEdOrg.name : this.name;
        this.street = (newEdOrg.street != null) ? newEdOrg.street : this.street;
        this.suite = (newEdOrg.suite != null) ? newEdOrg.suite : this.suite;
        this.city = (newEdOrg.city != null) ? newEdOrg.city : this.city;
        this.state = (newEdOrg.state != null) ? newEdOrg.state : this.state;
        this.postalCode = (newEdOrg.postalCode != null) ? newEdOrg.postalCode : this.postalCode;
        this.stateOrganizationId = (newEdOrg.stateOrganizationId != null) ? newEdOrg.stateOrganizationId
                : this.stateOrganizationId;
        this.type = (newEdOrg.type != null) ? newEdOrg.type : this.type;
        
        if (this.parentEdOrg == null) {
            this.setParentEdOrg(newEdOrg.getParentEdOrg());
        }
    }
    
    public String emitXml() {
        StringBuffer answer = new StringBuffer();
        answer.append("<").append(type).append(">\n");
        answer.append("   <StateOrganizationId>").append(stateOrganizationId).append("</StateOrganizationId>\n");
        answer.append("   <NameOfInstitution>").append(name).append("</NameOfInstitution>\n");
        answer.append("   <OrganizationCategory>").append(type).append("</OrganizationCategory>\n");
        answer.append("   <Address AddressType=\"Physical\">\n");
        answer.append("      <StreetNumberName>").append(street).append("</StreetNumberName>\n");
        answer.append("      <City>").append(city).append("</City>\n");
        answer.append("      <StateAbbreviation>").append(state).append("</StateAbbreviation>\n");
        answer.append("      <PostalCode>").append(postalCode).append("</PostalCode>\n");
        answer.append("      <NameOfCounty>\"\"</NameOfCounty>\n");
        answer.append("   </Address>\n");
        answer.append("   <Telephone InstitutionTelephoneNumberType=\"Main\">\n");
        answer.append("      <TelephoneNumber>\"\"</TelephoneNumber>\n");
        answer.append("   </Telephone>\n");
        answer.append("   <ProgramReference>\n");
        answer.append("      <ProgramIdentity>\n");
        answer.append("         <ProgramId>\"\"</ProgramId>\n");
        answer.append("      </ProgramIdentity>\n");
        answer.append("   </ProgramReference>\n");
        answer.append("   <LEACategory>Independent</LEACategory>\n");
        answer.append("</").append(type).append(">\n");
        /*
         * <LocalEducationAgency>
         * <StateOrganizationId>152901</StateOrganizationId>
         * <NameOfInstitution>Smith County ISD</NameOfInstitution>
         * <OrganizationCategory>Local Education Agency</OrganizationCategory>
         * <Address AddressType="Physical">
         * <StreetNumberName>1628 99th Street</StreetNumberName>
         * <City>Lebanon</City>
         * <StateAbbreviation>KS</StateAbbreviation>
         * <PostalCode>66952</PostalCode>
         * <NameOfCounty>Smith County</NameOfCounty>
         * </Address>
         * <Telephone InstitutionTelephoneNumberType="Main">
         * <TelephoneNumber>(785) 667-1000</TelephoneNumber>
         * </Telephone>
         * <ProgramReference>
         * <ProgramIdentity>
         * <ProgramId>Bilingual</ProgramId>
         * </ProgramIdentity>
         * </ProgramReference>
         * <LEACategory>Independent</LEACategory>
         * </LocalEducationAgency>
         */
        return answer.toString();
    }
    
    @Override
    public String emit() {
        if (Configuration.getOutputType().equals(Configuration.OutputType.EdFiXml)) {
            return emitXml();
        }
        // {"_id":{"$binary":"SkDUiGE8MB3FSzLMxdcTug==","$type":"03"},"type":"educationOrganization","body":{"organizationCategories":["State_Education_Agency"],
        // "address":[{"addressType":"Physical","streetNumberName":"123 District Office Way","apartmentRoomSuiteNumber":"200","buildingSiteNumber":"","city":"NY",
        // "stateAbbreviation":"NY","postalCode":"","nameOfCounty":""}],"stateOrganizationId":"KS-SMALLVILLE",
        // "nameOfInstitution":"Greater Smallville K-12 School District"},"metadata":{},"tenantId":"Zork"}
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"")
                .append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"educationOrganization\",\"body\":{\"organizationCategories\":[\"")
                .append(type).append("\"],\"address\":[{\"addressType\":\"Physical\",\"streetNumberName\":\"")
                .append(street).append("\",\"apartmentRoomSuiteNumber\":\"").append(suite)
                .append("\",\"buildingSiteNumber\":\"\",\"city\":\"").append(city)
                .append("\",\"stateAbbreviation\":\"").append(state).append("\",\"postalCode\":\"").append(postalCode)
                .append("\",\"nameOfCounty\":\"\"}],\"stateOrganizationId\":\"").append(stateOrganizationId)
                .append("\",\"nameOfInstitution\":\"").append(name)
                .append("\"},\"metadata\":{},\"tenantId\":\"Zork\"}\n");
        
        return answer.toString();
    }
    
    public String emitParentEdOrgAssociation() {
        // {"_id":{"$binary":"X0xBu3Wxn2JBtXF6YUBwjg==","$type":"03"},"type":"educationOrganizationAssociation",
        // "body":{"educationOrganizationParentId":"0a922b8a-7a3b-4320-8b34-0f7749b8b062","educationOrganizationChildId":"be3a26a2-a0e8-4098-bca3-c6cb2cdee970"},"tenantId":"Zork"}
        
        if (parentEdOrg == null) {
            return "";
        }
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(parentAssociationUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"educationOrganizationAssociation\",")
                .append("\"body\":{\"educationOrganizationParentId\":\"").append(parentEdOrg.getId())
                .append("\",\"educationOrganizationChildId\":\"").append(this.getId())
                .append("\"},\"tenantId\":\"Zork\"}\n");
        
        return answer.toString();
    }
    
    public String getId() {
        return generatedUuid;
    }
    
    public void setType(String edOrgType) {
        this.type = edOrgType;
    }
    
    public void setParentEdOrg(EducationalOrganization parent) {
        if (parent == null) {
            return;
        }
        this.parentEdOrg = parent;
        parentAssociationUuid = Base64.nextUuid("aaca");
    }
    
    public EducationalOrganization getParentEdOrg() {
        return parentEdOrg;
    }
}
