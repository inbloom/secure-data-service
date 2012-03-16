@wip
Feature: Test Authentication, CRUD, optional flag and simple display with SDK

Background: None

Scenario:  As an application developer, I want to log in as an educator and access a filtered list of my students
    Given  I am a valid SEA/LEA end user "linda.kim" with password "linda.kim1234"
    And I am authenticated with the "Illinois Realm" Realm
    And the role attribute equals "Educator"
   
   Given format "application/json"
	When I navigate to GET "/teachers/<'Linda Kim' ID>"
	Then I should receive a return code of 200
		And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Linda Kim' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Linda Kim' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Linda Kim' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Linda Kim' ID>/targets"
	Then I should receive a return code of 200
		And I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "L. Kim - 7th Grade English - Sec. 1" 
		And I should find section with uniqueSectionCode is "L. Kim - 7th Grade English - Sec. 2"  with <'ImportantSection' ID>
				
	When I navigate to GET "getStudents" with URI "/student-sections-association/<'ImportantSection' ID>/targets" 
		And filter by  "sex" = "Female" 
		And "sort-by" = "name.lastSurname"
		 And "sort-order" = "descending" 
		 And "start-index" = "0" 
		 And "max-results" = "5"
	     Then  I should receive a collection of 5 students
			And I should find Student with <'Suzy Queue' ID>
			And I should find Student with <'Jane Steve' ID>
			And I should find Student with <'Mary Line' ID>
			And I should find Student with <'Jill Doe' ID>
			And I should find Student with <'Jenny Deer' ID>
					     

@wip
Scenario:  As a IT admin I should be able to CRUD a student and association
     Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals "IT Administrator"
    And I am authenticated on "Shared Learning Infrastructure"

    Given format "application/json"
	When I navigate to GET "/teachers/<'Linda Kim' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Linda Kim' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Linda Kim' ID>"/targets"
		And I should receive a link named "self" with URI "/teachers/<'Linda Kim' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Linda Kim' ID>"/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "L. Kim - 7th Grade English - Sec. 1" 
		And I should find section with uniqueSectionCode is "L. Kim - 7th Grade English - Sec. 2"  with <'ImportantSection' ID>
	
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
     
@wip
Scenario:  As a AggregateViewer I should not see personally identifiable information data
    Given I am a valid SEA/LEA end user "mdorgan" with password "mdorgan1234"
    And I am authenticated on "Illinois Realm" Realm
    And the role attribute equals "Aggregate Viewer"

    When I navigate to GET "/teachers/<'Linda Kim' ID>"
    Then I should receive a return code of 403
        
    When I navigate to GET "/teacher-section-associations/<'Linda Kim and 7th Grade English - Sec. 1' ID>/targets"
    Then I should receive a return code of 403

    When I navigate to GET "/student-section-associations/<'7th Grade English - Sec. 1' ID>/targets"
    Then I should receive a return code of 403
         
    When I navigate to GET "/students/<'Jane Doe' ID>"      
    Then I should receive a return code of 403
    
    When I navigate to GET "/sections/<'7th Grade English - Sec. 1' ID>"      
    Then I should receive a return code of 403
        
    When I navigate to GET "/assessments/<'Grade 2 BOY DIBELS' ID>"
    Then I should receive a return code of 403
