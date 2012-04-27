Feature:  Student Transcript History Panel
  As a teacher in a school district, I want to click on a student and look at his/her transcript history.

  Background:
    Given I have an open web browser
    Given the server is in "live" mode
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go

@integration
  Scenario: View student's transcript history - Matt Sollars
    When I login as "linda.kim" "linda.kim1234"
    And I select ed org "Daybreak School District 4529"
    And I select school "East Daybreak Junior High"
    And I select course "8th Grade English"
    And I select section "8th Grade English - Sec 6"
    And I click on student "Matt Sollars"
    And I view its student profile

    Then their name shown in profile is "Matt Joseph Sollars Jr"
    And their id shown in proflie is "800000025"
    And their grade is "8"
    And the teacher is "Mrs Linda Kim"
    And the class is "8th Grade English - Sec 6"
    And the lozenges count is "1"
    And the lozenges include "ELL"

    And there are "7" Tabs
    And Tab has a title named "Middle School Overview"
    And Tab has a title named "Attendance and Discipline"
    And Tab has a title named "Assessments"
    And Tab has a title named "Advanced Academics"
    And Tab has a title named "Daybreak District"
    And Tab has a title named "ELL"
    And Tab has a title named "Grades and Credits"
    And in "Grades and Credits" tab, there are "1" Panels

    When I look at the "Transcript History" panel under "Grades and Credits"
    Then I should find 5 rows of transcript history
    And I should see the table headers "Year;Term;School;Grade Level;Cumulative GPA;"
    And I should see "2010-2011;Fall Semester;East Daybreak Junior High;Seventh grade; ;" for row 1
    And I should see "2010-2011;Spring Semester;East Daybreak Junior High;Seventh grade; ;" for row 2
    And I should see "2007-2008;Fall Semester; ;Fourth grade; ;" for row 3
    And I should see "2007-2008;Spring Semester; ;Fourth grade; ;" for row 4
    And I should see "2009-2010;Spring Semester;East Daybreak Junior High;Seventh grade; ;" for row 5
    And I should find 0 expanded rows

    When I click the expand button of row 1
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 1 sub rows
    And I should see "English Language and Literature;7th Grade English;A-;" for sub row 1

    When I click the expand button of row 2
    And I should see the sub table headers "Subject;Course;Grade;"
    Then I should find 6 sub rows
    And I should see "Social Studies;State History II-G7;B-;" for sub row 1
    And I should see "Science;Science 7B;C;" for sub row 2
    And I should see "Foreign Language and Literature;Spanish II;B+;" for sub row 3
    And I should see "English Language and Literature;7th Grade Composition;B+;" for sub row 4
    And I should see "Fine and Performing Arts;Jazz Band;A;" for sub row 5
    And I should see "Mathematics;Math 7B;B;" for sub row 6

    When I click the expand button of row 3
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "Physical, Health, and Safety Education;Phys-Ed 4A;A-;" for sub row 1
    And I should see "Reading;Reading 4A;A;" for sub row 2
    And I should see "Writing;Writing 4A;B;" for sub row 3
    And I should see "Mathematics;Math 4A;B-;" for sub row 4
    And I should see "Social Studies;Government-4;B+;" for sub row 5
    And I should see "Science;Science 4A;B+;" for sub row 6

    When I click the expand button of row 4
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 4 sub rows
    And I should see "Mathematics;Math 4B;B+;" for sub row 1
    And I should see "Social Studies;Geography-4;B;" for sub row 2
    And I should see "Science;Science 4B;A;" for sub row 3
    And I should see "Physical, Health, and Safety Education;Phys-Ed 4B;A;" for sub row 4

    # Sort by subject - descending, ascending
    When I click the "Subject" header to sort
    Then I should find 4 sub rows
    And I should see "Social Studies;Geography-4;B;" for sub row 1
    And I should see "Science;Science 4B;A;" for sub row 2
    And I should see "Physical, Health, and Safety Education;Phys-Ed 4B;A;" for sub row 3
    And I should see "Mathematics;Math 4B;B+;" for sub row 4
    When I click the "Subject" header to sort
    Then I should find 4 sub rows
    And I should see "Mathematics;Math 4B;B+;" for sub row 1
    And I should see "Physical, Health, and Safety Education;Phys-Ed 4B;A;" for sub row 2
    And I should see "Science;Science 4B;A;" for sub row 3
    And I should see "Social Studies;Geography-4;B;" for sub row 4

    When I click the expand button of row 5
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 1 sub rows
    And I should see "English Language and Literature;7th Grade English;A-;" for sub row 1

    # Collapse the sub sub grids and check for counts
    When I click the expand button of row 1
    Then I should find 4 expanded rows
    When I click the expand button of row 2
    Then I should find 3 expanded rows
    When I click the expand button of row 3
    Then I should find 2 expanded rows
    When I click the expand button of row 4
    Then I should find 1 expanded rows
    When I click the expand button of row 5
    Then I should find 0 expanded rows
    # Expand all
    When I click the Expand All link
    Then I should find 5 expanded rows
    When I click the expand button of row 1
    When I click the expand button of row 2
    Then I should find 3 expanded rows
    When I click the Expand All link
    Then I should find 5 expanded rows

  @integration
  Scenario: View student's transcript history - Carmen Ortiz
    When I login as "cgray" "cgray1234"
    And I select ed org "Daybreak School District 4529"
    And I select school "Daybreak Central High"
    And I select course "American Literature"
    And I select section "Sec 145"
    And I click on student "Carmen Ortiz"
    And I view its student profile

    Then their name shown in profile is "Carmen Daniella Ortiz"
    And their id shown in proflie is "900000016"
    And their grade is "11"
    And the teacher is "!"
    And the class is "!"
    And the lozenges count is "0"

    And there are "6" Tabs
    And Tab has a title named "High School Overview"
    And Tab has a title named "Attendance and Discipline"
    And Tab has a title named "Assessments"
    And Tab has a title named "Advanced Academics"
    And Tab has a title named "Daybreak District"
    And Tab has a title named "Grades and Credits"
    And in "Grades and Credits" tab, there are "1" Panels

    When I look at the "Transcript History" panel under "Grades and Credits"
    Then I should find 6 rows of transcript history
    And I should see the table headers "Year;Term;School;Grade Level;Cumulative GPA;"
    And I should see "2008-2009;Spring Semester; ;Eighth grade; ;" for row 1
    And I should see "2009-2010;Fall Semester;Daybreak Central High;Ninth grade; ;" for row 2
    And I should see "2010-2011;Fall Semester;Daybreak Central High;Tenth grade; ;" for row 3
    And I should see "2008-2009;Fall Semester; ;Eighth grade; ;" for row 4
    And I should see "2010-2011;Spring Semester;Daybreak Central High;Tenth grade; ;" for row 5
    And I should see "2009-2010;Spring Semester;Daybreak Central High;Ninth grade; ;" for row 6
    And I should find 0 expanded rows

    When I click the expand button of row 1
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "Fine and Performing Arts;Choir B;A-;" for sub row 1
    And I should see "Physical, Health, and Safety Education;Phys-Ed 8B;B+;" for sub row 2
    And I should see "English Language and Literature;ELA 8B;B;" for sub row 3
    And I should see "Mathematics;Pre-Algebra II;B-;" for sub row 4
    And I should see "Social Studies;U.S. History II;C+;" for sub row 5
    And I should see "Science;Science 8B;C+;" for sub row 6

    When I click the expand button of row 2
    And I should see the sub table headers "Subject;Course;Grade;"
    Then I should find 6 sub rows
    And I should see "Science;Environmental Studies I;B;" for sub row 1
    And I should see "Fine and Performing Arts;Vocal Ensemble I;A;" for sub row 2
    And I should see "Physical, Health, and Safety Education;Yoga and Fitness;A;" for sub row 3
    And I should see "English Language and Literature;English 9A ELL;C+;" for sub row 4
    And I should see "Mathematics;Basic Math I;C-;" for sub row 5
    And I should see "Social Studies;World History I;C;" for sub row 6

    When I click the expand button of row 3
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "English Language and Literature;10th Grade English;86.0;" for sub row 1
    And I should see "Science;Earth Sciences I;B;" for sub row 2
    And I should see "Fine and Performing Arts;Vocal Performance I;A;" for sub row 3
    And I should see "Physical, Health, and Safety Education;Weight Training;B;" for sub row 4
    And I should see "Mathematics;Algebra I;C+;" for sub row 5
    And I should see "Social Studies;State History I;C+;" for sub row 6

    When I click the expand button of row 4
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "Fine and Performing Arts;Choir A;B;" for sub row 1
    And I should see "English Language and Literature;ELA 8A;C+;" for sub row 2
    And I should see "Physical, Health, and Safety Education;Phys-Ed 8A;B+;" for sub row 3
    And I should see "Mathematics;Pre-Algebra I;C-;" for sub row 4
    And I should see "Social Studies;U.S. History I;C+;" for sub row 5
    And I should see "Science;Science 8A;C+;" for sub row 6

    When I click the expand button of row 5
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "Social Studies;State History II;B-;" for sub row 1
    And I should see "Science;Earth Sciences II;C+;" for sub row 2
    And I should see "Fine and Performing Arts;Vocal Performance II;A;" for sub row 3
    And I should see "Physical, Health, and Safety Education;Nutrition;B;" for sub row 4
    And I should see "English Language and Literature;10th Grade Writing;88.0;" for sub row 5
    And I should see "Mathematics;Algebra II;B;" for sub row 6

    When I click the expand button of row 6
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see "Social Studies;World History II;C+;" for sub row 1
    And I should see "Science;Environmental Studies II;C+;" for sub row 2
    And I should see "Fine and Performing Arts;Vocal Ensemble II;A;" for sub row 3
    And I should see "Physical, Health, and Safety Education;Track and Field;B;" for sub row 4
    And I should see "English Language and Literature;English 9B ELL;B;" for sub row 5
    And I should see "Mathematics;Basic Math II;C-;" for sub row 6