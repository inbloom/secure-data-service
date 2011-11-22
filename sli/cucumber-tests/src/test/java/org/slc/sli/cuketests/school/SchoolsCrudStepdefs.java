package org.slc.sli.cuketests.school;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import cucumber.annotation.en.And;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.domain.School;

public class SchoolsCrudStepdefs {
    
     private static final String RESOURCE_URL =
     "http://ec2-50-19-203-5.compute-1.amazonaws.com:8080/api/rest";
//    private static final String RESOURCE_URL = "http://localhost:8080/api/rest";
    
    Client client;
    {
        DefaultClientConfig cc = new DefaultClientConfig();
        client = Client.create(cc);
    }
    
    WebResource webResource = client.resource(RESOURCE_URL);
    String format;
    School school;
    int expectedStatus;
    List<ClientResponse> response;
    
    @Given("^I am logged in using \"([^\"]*)\" \"([^\"]*)\"$")
    public void I_am_logged_in_using_(String username, String password) {
        HTTPBasicAuthFilter filter = new HTTPBasicAuthFilter(username, password);
        client.addFilter(filter);
    }
    
    @And("^I go to \"([^\"]*)\"$")
    public void I_go_to_(String path) {
        webResource = webResource.path(path);
    }
    
    @And("^I accept \"([^\"]*)\"$")
    public void i_accept(String format) {
        this.format = format;
    }
    
    @Then("^the status code is (\\d+)$")
    public void setStatusCode(int responseCode) {
        this.expectedStatus = responseCode;
    }
    
    @And("^the response contains:$")
    public void response_contains(List<School> expectedSchools) throws Exception {
        List<School> responseSchools = getSchoolList(webResource, format);
        for (School e : expectedSchools) {
            boolean found = false;
            for (School r : responseSchools) {
                if (e.getShortName().equals(r.getShortName()) && e.getFullName().equals(r.getFullName())
                        && e.getAdministrativeFundingControl().equals(r.getAdministrativeFundingControl())
                        && e.getCharterStatus().equals(r.getCharterStatus())
                        && e.getMagnetSpecialProgramEmphasisSchool().equals(r.getMagnetSpecialProgramEmphasisSchool())
                        && e.getOperationalStatus().equals(r.getOperationalStatus())
                        && e.getStateOrganizationId().equals(r.getStateOrganizationId())
                        && e.getTitleIPartASchoolDesignation().equals(r.getTitleIPartASchoolDesignation())
                        && e.getWebSite().equals(r.getWebSite())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
    
    //
    // @And("^response equals \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\".$")
    // public void response_equals_(int schoolId, String shortName, String fullName, String stateId,
    // String schoolType, String webSite, String charter, String titleI, String magnet, String
    // funding, String status) {
    // if (format.equalsIgnoreCase("application/json")) {
    // School r = webResource.accept(format).get(School.class);
    // assertEquals(r.getSchoolId(), r.getSchoolId());
    // assertEquals(shortName, r.getShortName());
    // assertEquals(fullName, r.getFullName());
    // assertEquals(stateId, r.getStateOrganizationId());
    // assertEquals(schoolType, r.getSchoolType());
    // assertEquals(webSite, r.getWebSite());
    // assertEquals(charter, r.getCharterStatus());
    // assertEquals(titleI, r.getTitleIPartASchoolDesignation());
    // assertEquals(magnet, r.getMagnetSpecialProgramEmphasisSchool());
    // assertEquals(funding, r.getAdministrativeFundingControl());
    // assertEquals(status, r.getOperationalStatus());
    // } else {
    // fail();
    // }
    // }
    
    // @And("^I pass in \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    // public void I_pass_in_(String shortName, String fullName, String stateId, String schoolType,
    // String webSite, String charter, String titleI, String magnet, String funding, String status)
    // {
    // school = new School();
    // school.setShortName(shortName);
    // school.setFullName(fullName);
    // school.setStateOrganizationId(stateId);
    // school.setSchoolType(schoolType);
    // school.setWebSite(webSite);
    // school.setCharterStatus(charter);
    // school.setTitleIPartASchoolDesignation(titleI);
    // school.setMagnetSpecialProgramEmphasisSchool(magnet);
    // school.setAdministrativeFundingControl(funding);
    // school.setOperationalStatus(status);
    // }
    
    @And("^the response is empty$")
    public void the_response_is_empty() throws IOException {
        assertNotNull(response);
        assertTrue(0 != response.size());
        for (ClientResponse r : response) {
            assertEquals(expectedStatus, r.getStatus());
            assertEquals(0, responseSize(r));
        }
        
    }
    
    @And("^I cannot find the following:$")
    public void at_I_cannot_find_the_following(List<School> deletedSchools) throws Exception {
        List<School> allSchools = getSchoolList(webResource, format);
        for (School d : deletedSchools) {
            assertEquals(0, findMatches(allSchools, d).size());
        }
    }
    
    @And("^I create:$")
    public void I_create(List<School> schools) throws Exception {
        response = new LinkedList<ClientResponse>();
        for (School e : schools) {
            response.add(webResource.accept(format).post(ClientResponse.class, e));
        }
    }
    
    @And("^I find and delete:$")
    public void I_find_and_delete(List<School> schools) throws Exception {
        List<School> allSchools = getSchoolList(webResource, format);
        
        List<School> toDelete = new LinkedList<School>();
        for (School expected : schools) {
            List<School> matches = findMatches(allSchools, expected);
            toDelete.addAll(matches);
        }
        
        response = new LinkedList<ClientResponse>();
        for (School e : toDelete) {
            WebResource deleteResource = webResource.path("/" + e.getSchoolId());
            response.add(deleteResource.accept(format).delete(ClientResponse.class));
        }
    }
    
    @And("^I find and update:$")
    public void I_find_and_update(List<School> schools) throws Exception {
        List<School> allSchools = getSchoolList(webResource, format);
        List<School> toUpdate = new LinkedList<School>();
        for (School s : schools) {
            List<School> matches = findMatches(allSchools, s);
            for (School m : matches) {
                m.setAdministrativeFundingControl(s.getAdministrativeFundingControl());
                m.setCharterStatus(s.getCharterStatus());
                m.setMagnetSpecialProgramEmphasisSchool(s.getMagnetSpecialProgramEmphasisSchool());
                m.setOperationalStatus(s.getOperationalStatus());
                m.setSchoolType(s.getSchoolType());
                m.setStateOrganizationId(s.getStateOrganizationId());
                m.setTitleIPartASchoolDesignation(s.getTitleIPartASchoolDesignation());
                m.setWebSite(s.getWebSite());
                toUpdate.add(m);
            }
        }
        
        response = new LinkedList<ClientResponse>();
        for (School update : toUpdate) {
            WebResource updateResource = webResource.path("/" + update.getSchoolId());
            response.add(updateResource.accept(format).put(ClientResponse.class, update));
        }
    }
    
    @And("^I see the following updates:$")
    public void I_see_the_following_updates(List<School> expected) throws Exception {
        List<School> allSchools = getSchoolList(webResource, format);
        for (School e : expected) {
            List<School> matches = findMatches(allSchools, e);
            assertTrue(matches.size() > 0);
            for (School m : matches) {
                assertEquals(e.getShortName(), m.getShortName());
                assertEquals(e.getFullName(), m.getFullName());
                assertEquals(e.getAdministrativeFundingControl(), m.getAdministrativeFundingControl());
                assertEquals(e.getCharterStatus(), m.getCharterStatus());
                assertEquals(e.getMagnetSpecialProgramEmphasisSchool(), m.getMagnetSpecialProgramEmphasisSchool());
                assertEquals(e.getOperationalStatus(), m.getOperationalStatus());
                assertEquals(e.getSchoolType(), m.getSchoolType());
                assertEquals(e.getStateOrganizationId(), m.getStateOrganizationId());
                assertEquals(e.getTitleIPartASchoolDesignation(), m.getTitleIPartASchoolDesignation());
                assertEquals(e.getWebSite(), m.getWebSite());
            }
        }
    }
    
    private static int responseSize(ClientResponse response) {
        InputStream stream = response.getEntityInputStream();
        int byteCount = 0;
        try {
            while (stream.read() >= 0) {
                byteCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteCount;
    }
    
    private static List<School> getSchoolList(WebResource webResource, String format) throws JsonParseException,
            JsonMappingException, IOException {
        if (format.equalsIgnoreCase("application/json")) {
            String jsonString = webResource.accept(format).get(String.class);
            /* have to use jackson object mapper because the response from the API is of the form:
             *     [ {"schoolId" : 1, ...}, {"schoolId": 2, ...} ]
             * instead of
             *     [ {"school" : {"schoolId" : 1, ..}}, {"school" : {"schoolId" : 2}} ]
             */
            School[] schools = new ObjectMapper().readValue(jsonString, School[].class);
            return Arrays.asList(schools);
        }
        return null;
    }
    
    private static List<School> findMatches(List<School> allSchools, School expected) {
        List<School> matching = new LinkedList<School>();
        for (School s : allSchools) {
            if (expected.getShortName().equals(s.getShortName()) && expected.getFullName().equals(s.getFullName())) {
                matching.add(s);
            }
        }
        return matching;
    }
    
}