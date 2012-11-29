@smoke @RALLY_US209
Feature: I want to be able to retrieve only sections I am currently teaching/students currently enrolled in the school
  That means only the data within the grace period is accessible

Background:
  Given format "application/json"

Scenario: Educator accessing a student that he/she teaches
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I teach "<'MARVIN MILLER'>"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@DE1767
Scenario: Leader accessing a student that is in own school
  Given I am logged in using "sbantu" "sbantu1234" to realm "IL"
  And my school is "<'DAYBREAK CENTRAL HIGH'>"
  And "<'CHARLA CHRISTOFF'>" studies in "<'DAYBREAK CENTRAL HIGH'>"
  When I make an API call to get "<'CHARLA CHRISTOFF'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

Scenario: Educator accessing a student that he/she does not teach, but it's within the grace period
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I do not teach "<'CARMEN ORTIZ'>"
  And I taught "<'CARMEN ORTIZ JR'>" in "<'FALL 2010'>"
  And "<'FALL 2010 END DATE'>" is within the grace period
  When I make an API call to get "<'CARMEN ORTIZ JR'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@DE1767
Scenario: Leader accessing a student that is not in his/her school, but was before and that is within the grace period
  Given I am logged in using "mgonzales" "mgonzales1234" to realm "IL"
  And my school is "<'SOUTH DAYBREAK ELEMENTARY'>"
  And "<'MATT SOLLARS'>" is not enrolled in "<'SOUTH DAYBREAK ELEMENTARY'>"
  And "<'MATT SOLLARS'>" exited "<'SOUTH DAYBREAK ELEMENTARY'>" on "<'MATT SOLLARS SOUTH DAYBREAK EXIT DATE'>"
  And "<'MATT SOLLARS SOUTH DAYBREAK EXIT DATE'>" is within the grace period
  When I make an API call to get "<'MATT SOLLARS'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

Scenario: Educator accessing a student that he/she does not teach, but is associated to through a program
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I do not teach "<'LUCRETIA NAGAI'>"
  And I taught "<'LUCRETIA NAGAI'>" in "<'FALL 2001'>"
  And "<'FALL 2001 END DATE'>" is outside of the grace period
  And I am associated to the student through a program
  When I make an API call to get "<'LUCRETIA NAGAI'>"
  Then I should receive a return code of 200
  When I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And I make an API call to get "<'LUCRETIA NAGAI'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@DE1767
Scenario: Leader accessing a student that is not enrolled in his/her school, and was before but that was out of the grace period
  Given I am logged in using "mgonzales" "mgonzales1234" to realm "IL"
  And my school is "<'SOUTH DAYBREAK ELEMENTARY'>"
  And "<'STEVE DONG'>" is not enrolled in "<'SOUTH DAYBREAK ELEMENTARY'>"
  And "<'STEVE DONG'>" exited "<'SOUTH DAYBREAK ELEMENTARY'>" on "<'STEVE DONG SOUTH DAYBREAK EXIT DATE'>"
  And "<'STEVE DONG SOUTH DAYBREAK EXIT DATE'>" is outside of the grace period
  When I make an API call to get "<'STEVE DONG'>"
  Then I should receive a return code of 403
  When I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And I make an API call to get "<'STEVE DONG'>"
  Then I should receive a return code of 403