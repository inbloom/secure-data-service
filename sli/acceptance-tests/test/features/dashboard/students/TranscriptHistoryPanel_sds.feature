@RALLY_US149
Feature: Student Transcript History Panel
  As a teacher in a school district, I want to click on a student and look at his/her transcript history.

  Background:
    Given I have an open web browser
    Given the server is in "live" mode
    When I navigate to the Dashboard home page
    When I select "Illinois Daybreak School District 4529" and click go

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
    #And the teacher is "Mrs Linda Kim"
    #And the class is "8th Grade English - Sec 6"
    And the lozenges count is "1"
    And the lozenges include "ELL"

    And there are "4" Tabs
    And Tab has a title named "Middle School Overview"
    And Tab has a title named "Attendance and Discipline"
    And Tab has a title named "Assessments"
    And Tab has a title named "Grades and Credits"
    And in "Grades and Credits" tab, there are "1" Panels

    When I look at the "Transcript History" panel under "Grades and Credits"
    Then I should find 8 rows of transcript history
    And I should see the table headers "Year;Term;School;Grade Level;Cumulative GPA;"
    And I should see the row "2008-2009;Fall Semester; ;5;3.3;"
    And I should see the row "2010-2011;Fall Semester;East Daybreak Junior High;7;3.4;"
    And I should see the row "2010-2011;Spring Semester;East Daybreak Junior High;7;3.2;"
    And I should see the row "2009-2010;Fall Semester;East Daybreak Junior High;6;3.1;"
    And I should see the row "2007-2008;Fall Semester; ;4;3.0;"
    And I should see the row "2007-2008;Spring Semester; ;4;3.3;"
    And I should see the row "2009-2010;Spring Semester;East Daybreak Junior High;6;2.9;"
    And I should see the row "2008-2009;Spring Semester; ;5;3.2;"
    And I should find 0 expanded rows

    When I click the expand button of the row "2008-2009;Fall Semester; ;5;3.3;"
    And I should see the sub table headers "Subject;Course;Grade;"
    Then I should find 6 sub rows
    And I should see the sub row "Reading;Reading 5A;A;"
    And I should see the sub row "Writing;Writing 5A;B;"
    And I should see the sub row "Mathematics;Math 5A;B-;"
    And I should see the sub row "Social Studies;Government-5;B+;"
    And I should see the sub row "Science;Science 5A;B+;"
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 5A;A-;"

    When I click the expand button of the row "2010-2011;Fall Semester;East Daybreak Junior High;7;3.4;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "English Language and Literature;7th Grade English;A-;"
    And I should see the sub row "Mathematics;Math 7A;B-;"
    And I should see the sub row "Social Studies;State History I-G7;B;"
    And I should see the sub row "Science;Science 7A;C+;"
    And I should see the sub row "Foreign Language and Literature;Spanish I;B;"
    And I should see the sub row "Physical, Health, and Safety Education;Conditioning;A-;"

    When I click the expand button of the row "2010-2011;Spring Semester;East Daybreak Junior High;7;3.2;"
    And I should see the sub table headers "Subject;Course;Grade;"
    Then I should find 6 sub rows
    And I should see the sub row "Social Studies;State History II-G7;B-;"
    And I should see the sub row "Science;Science 7B;C;"
    And I should see the sub row "Foreign Language and Literature;Spanish II;B+;"
    And I should see the sub row "English Language and Literature;7th Grade Composition;B+;"
    And I should see the sub row "Fine and Performing Arts;Jazz Band;A;"
    And I should see the sub row "Mathematics;Math 7B;B;"

    When I click the expand button of the row "2009-2010;Fall Semester;East Daybreak Junior High;6;3.1;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "English Language and Literature;ELA 6A;A-;"
    And I should see the sub row "Mathematics;Math 6A;B;"
    And I should see the sub row "Social Studies;World History I-G6;C+;"
    And I should see the sub row "Science;Science 6A;B;"
    And I should see the sub row "Fine and Performing Arts;Drama I;B;"
    And I should see the sub row "Physical, Health, and Safety Education;Team Sports;A;"

    When I click the expand button of the row "2007-2008;Fall Semester; ;4;3.0;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 4A;A-;"
    And I should see the sub row "Reading;Reading 4A;A;"
    And I should see the sub row "Writing;Writing 4A;B;"
    And I should see the sub row "Mathematics;Math 4A;B-;"
    And I should see the sub row "Social Studies;Government-4;B+;"
    And I should see the sub row "Science;Science 4A;B+;"

    When I click the expand button of the row "2007-2008;Spring Semester; ;4;3.3;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Mathematics;Math 4B;B+;"
    And I should see the sub row "Social Studies;Geography-4;B;"
    And I should see the sub row "Science;Science 4B;A;"
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 4B;A;"
    And I should see the sub row "Reading;Reading 4B;A-;"
    And I should see the sub row "Writing;Writing 4B;B-;"

    When I click the expand button of the row "2009-2010;Spring Semester;East Daybreak Junior High;6;2.9;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "English Language and Literature;ELA 6B;A;"
    And I should see the sub row "Mathematics;Math 6B;B;"
    And I should see the sub row "Social Studies;World History II-G6;B+;"
    And I should see the sub row "Science;Science 6B;C+;"
    And I should see the sub row "Fine and Performing Arts;Drama II;B-;"
    And I should see the sub row "Physical, Health, and Safety Education;Nutrition;C+;"

    When I click the expand button of the row "2008-2009;Spring Semester; ;5;3.2;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Reading;Reading 5B;A-;"
    And I should see the sub row "Writing;Writing 5B;B-;"
    And I should see the sub row "Mathematics;Math 5B;B+;"
    And I should see the sub row "Social Studies;Geography-5;B;"
    And I should see the sub row "Science;Science 5B;B;"
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 5B;A;"

    # Sort by subject - descending, ascending
    When I click the "Subject" header to sort
    Then I should find 6 sub rows
    And I should see "Writing;Writing 5B;B-;" for sub row 1
    And I should see "Social Studies;Geography-5;B;" for sub row 2
    And I should see "Science;Science 5B;B;" for sub row 3
    And I should see "Reading;Reading 5B;A-;" for sub row 4
    And I should see "Physical, Health, and Safety Education;Phys-Ed 5B;A;" for sub row 5
    And I should see "Mathematics;Math 5B;B+;" for sub row 6
    When I click the "Subject" header to sort
    Then I should find 6 sub rows
    And I should see "Writing;Writing 5B;B-;" for sub row 6
    And I should see "Social Studies;Geography-5;B;" for sub row 5
    And I should see "Science;Science 5B;B;" for sub row 4
    And I should see "Reading;Reading 5B;A-;" for sub row 3
    And I should see "Physical, Health, and Safety Education;Phys-Ed 5B;A;" for sub row 2
    And I should see "Mathematics;Math 5B;B+;" for sub row 1

    # Collapse the sub sub grids and check for counts
    When I click the expand button of the row "2008-2009;Fall Semester; ;5;3.3;"
    Then I should find 7 expanded rows
    When I click the expand button of the row "2010-2011;Fall Semester;East Daybreak Junior High;7;3.4;"
    Then I should find 6 expanded rows
    When I click the expand button of the row "2010-2011;Spring Semester;East Daybreak Junior High;7;3.2;"
    Then I should find 5 expanded rows
    When I click the expand button of the row "2009-2010;Fall Semester;East Daybreak Junior High;6;3.1;"
    Then I should find 4 expanded rows
    When I click the expand button of the row "2007-2008;Fall Semester; ;4;3.0;"
    Then I should find 3 expanded rows
    When I click the expand button of the row "2007-2008;Spring Semester; ;4;3.3;"
    Then I should find 2 expanded rows
    When I click the expand button of the row "2009-2010;Spring Semester;East Daybreak Junior High;6;2.9;"
    Then I should find 1 expanded rows
    When I click the expand button of the row "2008-2009;Spring Semester; ;5;3.2;"
    Then I should find 0 expanded rows
    # Expand all
    When I click the Expand All link
    Then I should find 8 expanded rows
    When I click the expand button of the row "2010-2011;Fall Semester;East Daybreak Junior High;7;3.4;"
    When I click the expand button of the row "2010-2011;Spring Semester;East Daybreak Junior High;7;3.2;"
    Then I should find 6 expanded rows
    When I click the Expand All link
    Then I should find 8 expanded rows

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

    And there are "4" Tabs
    And Tab has a title named "High School Overview"
    And Tab has a title named "Attendance and Discipline"
    And Tab has a title named "Assessments"
    And Tab has a title named "Grades and Credits"
    And in "Grades and Credits" tab, there are "1" Panels

    When I look at the "Transcript History" panel under "Grades and Credits"
    Then I should find 6 rows of transcript history
    And I should see the table headers "Year;Term;School;Grade Level;Cumulative GPA;"
    And I should see the row "2008-2009;Spring Semester; ;8;3.2;"
    And I should see the row "2009-2010;Fall Semester;Daybreak Central High;9;3.6;"
    And I should see the row "2010-2011;Fall Semester;Daybreak Central High;10;3.2;"
    And I should see the row "2008-2009;Fall Semester; ;8;2.8;"
    And I should see the row "2010-2011;Spring Semester;Daybreak Central High;10;3.4;"
    And I should see the row "2009-2010;Spring Semester;Daybreak Central High;9;2.5;"
    And I should find 0 expanded rows

    When I click the expand button of the row "2008-2009;Spring Semester; ;8;3.2;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Fine and Performing Arts;Choir B;A-;"
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 8B;B+;"
    And I should see the sub row "English Language and Literature;ELA 8B;B;"
    And I should see the sub row "Mathematics;Pre-Algebra II;B-;"
    And I should see the sub row "Social Studies;U.S. History II;C+;"
    And I should see the sub row "Science;Science 8B;C+;"

    When I click the expand button of the row "2009-2010;Fall Semester;Daybreak Central High;9;3.6;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    Then I should find 6 sub rows
    And I should see the sub row "Science;Environmental Studies I;B;"
    And I should see the sub row "Fine and Performing Arts;Vocal Ensemble I;A;"
    And I should see the sub row "Physical, Health, and Safety Education;Yoga and Fitness;A;"
    And I should see the sub row "English Language and Literature;English 9A ELL;C+;"
    And I should see the sub row "Mathematics;Basic Math I;C-;"
    And I should see the sub row "Social Studies;World History I;C;"

    When I click the expand button of the row "2010-2011;Fall Semester;Daybreak Central High;10;3.2;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "English Language and Literature;10th Grade English;86.0;"
    And I should see the sub row "Science;Earth Sciences I;B;"
    And I should see the sub row "Fine and Performing Arts;Vocal Performance I;A;"
    And I should see the sub row "Physical, Health, and Safety Education;Weight Training;B;"
    And I should see the sub row "Mathematics;Algebra I;C+;"
    And I should see the sub row "Social Studies;State History I;C+;"

    When I click the expand button of the row "2008-2009;Fall Semester; ;8;2.8;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Fine and Performing Arts;Choir A;B;"
    And I should see the sub row "English Language and Literature;ELA 8A;C+;"
    And I should see the sub row "Physical, Health, and Safety Education;Phys-Ed 8A;B+;"
    And I should see the sub row "Mathematics;Pre-Algebra I;C-;"
    And I should see the sub row "Social Studies;U.S. History I;C+;"
    And I should see the sub row "Science;Science 8A;C+;"

    When I click the expand button of the row "2010-2011;Spring Semester;Daybreak Central High;10;3.4;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Social Studies;State History II;B-;"
    And I should see the sub row "Science;Earth Sciences II;C+;"
    And I should see the sub row "Fine and Performing Arts;Vocal Performance II;A;"
    And I should see the sub row "Physical, Health, and Safety Education;Nutrition;B;"
    And I should see the sub row "English Language and Literature;10th Grade Writing;88.0;"
    And I should see the sub row "Mathematics;Algebra II;B;"

    When I click the expand button of the row "2009-2010;Spring Semester;Daybreak Central High;9;2.5;"
    Then I should see the sub table headers "Subject;Course;Grade;"
    And I should find 6 sub rows
    And I should see the sub row "Social Studies;World History II;C+;"
    And I should see the sub row "Science;Environmental Studies II;C+;"
    And I should see the sub row "Fine and Performing Arts;Vocal Ensemble II;A;"
    And I should see the sub row "Physical, Health, and Safety Education;Track and Field;B;"
    And I should see the sub row "English Language and Literature;English 9B ELL;B;"
    And I should see the sub row "Mathematics;Basic Math II;C-;"
