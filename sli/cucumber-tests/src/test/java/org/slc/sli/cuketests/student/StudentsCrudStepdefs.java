package org.slc.sli.cuketests.student;




import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.And;
import cucumber.annotation.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Step definitions for the get_all_students.feature Cucumber test. It uses the Jersey client to test the API.
 * 
 * 
 */

public class StudentsCrudStepdefs {
	
	private static final String RESOURCE_URL = "http://ec2-50-19-203-5.compute-1.amazonaws.com:8080/api/rest";
//	private static final String RESOURCE_URL = "http://localhost:8080/api/rest";

	 
	private DefaultClientConfig cc = new DefaultClientConfig();
	private Client client = Client.create(cc);
	private WebResource webResource = client.resource(RESOURCE_URL);
	
	@Given("^I am logged in using \"([^\"]*)\" \"([^\"]*)\"$")
	public void I_am_logged_in_using_(String username, String password) {
	    HTTPBasicAuthFilter filter = new HTTPBasicAuthFilter(username, password);
	    client.addFilter(filter);
	}

	@And("^I go to \"([^\"]*)\"$")
	public void I_go_to_(String path) {
		webResource.path(path);
	}
	
	@Then("^the status code for \"([^\"]*)\" format \"([^\"]*)\" is (\\d+)$")
	public void the_status_code_for_is_(String path, String format, int responseCode) {
		assertEquals(responseCode, webResource.path(path).accept(format).get(ClientResponse.class).getStatus());

	}
	
	@Then("^\"([^\"]*)\" format \"([^\"]*)\" contains \"([^\"]*)\"$")
	public void response_contains(String path, String format, String studentName) {
		assertTrue(webResource.path(path).accept(format).get(String.class).contains(studentName));
	    
	}
	
	@Then("^\"([^\"]*)\" format \"([^\"]*)\" contains the changed name$")
	public void _format_contains_the_changed_name(String arg1, String arg2, String arg3) {
	    // Express the Regexp above with the code you wish you had
	}

	@Then("^\"([^\"]*)\" format \"([^\"]*)\" contains the new student$")
	public void _format_contains_the_new_student(String arg1, String arg2, String arg3) {
	    // Express the Regexp above with the code you wish you had
	}

	@Then("^\"([^\"]*)\" format \"([^\"]*)\" does not contain the deleted student$")
	public void _format_does_not_contain_the_deleted_student(String arg1, String arg2, String arg3) {
	    // Express the Regexp above with the code you wish you had
	}

	@Then("^the student info is returned$")
	public void the_student_info_is_returned() {
	    // Express the Regexp above with the code you wish you had
	}

	@When("^I go to \"([^\"]*)\" and request a student by id$")
	public void I_go_to_and_request_a_student_by_id(String arg1) {
	    // Express the Regexp above with the code you wish you had
	}

	@When("^I go to \"([^\"]*)\" and request to add a student$")
	public void I_go_to_and_request_to_add_a_student(String arg1) {
	    // Express the Regexp above with the code you wish you had
	}

	@When("^I go to \"([^\"]*)\" and request to change a student name$")
	public void I_go_to_and_request_to_change_a_student_name(String arg1) {
	    // Express the Regexp above with the code you wish you had
	}

	@When("^I go to \"([^\"]*)\" and request to delete a student$")
	public void I_go_to_and_request_to_delete_a_student(String arg1) {
	    // Express the Regexp above with the code you wish you had
	}


	
	


}
