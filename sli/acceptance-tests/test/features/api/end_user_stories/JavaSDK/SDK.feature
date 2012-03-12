Feature: Test Authentication, CRUD, optional flag and simple display with SDK

Background: None

	Scenario Outline:  As an application developer, I want to log in as an educator and access a filtered list of my students
    Given  I am a valid SEA/LEA end user "linda.kim" with password "linda.kim1234"
    And I am authenticated with the "Shared Learning Infrastructure" Realm

    When I run the "API SLI SDK Sample Application" interactively
        And I type "1"
        And I enter my password
        Then the output should contain:
        """
    entityType=teacher        
	  """
    
#   Given format "application/json"
#	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
#	Then I should receive a return code of 200
#		And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
#		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
#		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
#	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
#	Then I should receive a return code of 200
#		And I should receive a collection of 2 section links 
#		And I should find section with uniqueSectionCode is "Section I" 
#		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
				
#	When I navigate to GET "getStudents" with URI "/student-sections-association/<'ImportantSection' ID>/targets" 
#		And filter by  "sex" = "Female" 
#		And "sort-by" = "name.lastSurname"
#		 And "sort-order" = "descending" 
#		 And "start-index" = "0" 
#		 And "max-results" = "5"
#	     Then  I should receive a collection of 5 students
#			And I should find Student with <'Suzy Queue' ID>
#			And I should find Student with <'Jane Steve' ID>
#			And I should find Student with <'Mary Line' ID>
#			And I should find Student with <'Jill Doe' ID>
#			And I should find Student with <'Jenny Deer' ID>
					     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "leader"        | "leader1234"        | "Leader"           |


@wip
Scenario Outline:  As a IT admin I should be able to CRUD a student and association
     Given  I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SEA/LEA IDP"

    Given format "application/json"
	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "Section I" 
		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
	
	When I navigate to GET "getStudents" with URI "/student-sections-association/<'ImportantSection' ID>/targets" 
	Then I should receive a collection of 5 student links
		And I should find Student with <'John Doe' ID>
		And I should find Student with <'Sean Deer' ID>
		And I should find Student with <'Suzy Queue' ID>
		And I should find Student with <'Mary Line' ID>
	 	And I should find Student with <'Dong Steve' ID>
	 	
	# create a student 			
	Given the "name" is "Jason" "Barker"
        And the "birthDate" is "1994-04-04"
        And the "sex" is "Male"
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive an ID for the newly created student
    When I navigate to GET "/students/<'Jason Barker' ID>"
    Then the "name" should be "Jason" "Barker"
       And the "birthDate" should be "1994-04-04"
       And the "sex" should be "Male"
       
       # associate a student with a section, test associations
	Given the "studentId" is "<'Jason Barker' ID>"
        And the "sectionId" is "<'ImportantSection' ID>"
    When I navigate to POST "/student-section-associations/" 
    Then I should receive a return code of 201
       And I should receive an ID for the newly created association
    
	When I navigate to GET "getStudents" with URI "/student-sections-association/<'ImportantSection' ID>/targets" 
	Then I should receive a collection of 6 student links
		And I should find Student with <'John Doe' ID>
		And I should find Student with <'Sean Deer' ID>
		And I should find Student with <'Suzy Queue' ID>
		And I should find Student with <'Mary Line' ID>
	 	And I should find Student with <'Dong Steve' ID>
	 	And I should find Student with <'Jason Barker' ID>
	
	# update a student
	 When I navigate to GET "/students/ <'Mary Line' ID>"
	    Then the "birthDate" should be "1998-01-22"
     When I set the "birthDate" to "1997-01-22"
	    And I navigate to PUT "/students/<' <'Mary Line' ID>'"
	    Then I should receive a return code of 204
      When I navigate to GET "/students/<'Mary Line' ID>"
        Then the "birthDate" should be "1997-01-22"
    
    #delete student    
      When I navigate to DELETE "/students/ <'Mary Line' ID>"  
      	 Then I should receive a return code of 204
	  When  I navigate to GET "/students/<'Mary Line' ID>"
	     Then I should receive a return code of 404
	  
	  # check the association is gone as well  
	  When I navigate to GET "getStudents" with URI "/student-sections-association/<'ImportantSection' ID>/targets" 
		Then I should receive a collection of 6 student links
		And I should find Student with <'John Doe' ID>
		And I should find Student with <'Sean Deer' ID>
		And I should find Student with <'Suzy Queue' ID>
	 	And I should find Student with <'Dong Steve' ID>
	 	And I should find Student with <'Jason Barker' ID> 
     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "administrator" | "administrator1234" | "IT Administrator" |


@wip
Scenario Outline:  As a AggregateViewer I should not see personally identifiable information data
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals "Aggregate Viewer"
    And I am authenticated on "SLI Realm"

    When I navigate to GET "/teachers/<'Ms. Smith' ID>"
    Then I should receive a return code of 403
        
    When I navigate to GET "/teacher-section-associations/<'Teacher Ms. Jones and Section Algebra II' ID>/targets"
    Then I should receive a return code of 403

    When I navigate to GET "/student-section-associations/<'Algebra II' ID>/targets"
    Then I should receive a return code of 403
         
    When I navigate to GET "/students/<'Jane Doe' ID>"      
    Then I should receive a return code of 403
    
    When I navigate to GET "/sections/<'Algebra II' ID>"      
    Then I should receive a return code of 403
        
    When I navigate to GET "/assessments/<'Grade 2 BOY DIBELS' ID>"
    Then I should receive a return code of 403

Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregator"     | "aggregator1234"     | "AggregateViewer"  |
     
