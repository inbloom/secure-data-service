Feature: I want to be able to retrieve only sections I am currently teaching/students currently enrolled in the school
  That means only data from those entities within the grace period is returned

Background:
  Given format "application/json"

Scenario: Educator accessing a student that he/she teaches
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I teach "<'MARVIN MILLER'>"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@wip
Scenario: Leader accessing a student that is in own school
  Given I am logged in using "sbantu" "sbantu1234" to realm "IL"
  And my school is "<'DAYBREAK CENTRAL HIGH'>"
  And "<'CHARLA CHRISTOFF'>" studies in "<'DAYBREAK CENTRAL HIGH'>"
  When I make an API call to get "<'CHARLA CHRISTOFF'>"
  Then I should receive a return code of 200
  And "entityType" should be "student" in the JSON response

Scenario: Educator accessing a student that he/she does not teach, but it's within the grace period
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I do not teach "<'CARMEN ORTIZ'>"
  And I taught "<'CARMEN ORTIZ'>" in "<'FALL 2010'>"
  And "<'FALL 2010 END DATE'>" is within the grace period
  When I make an API call to get "<'CARMEN ORTIZ'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@wip
Scenario: Leader accessing a student that is not in his/her school, but was before and that is within the grace period
  Given I am logged in using "sbantu" "sbantu1234" to realm "IL"
  And my school is "<'DAYBREAK CENTRAL HIGH'>"
  And "<'ORALIA SIMMER'>" is not enrolled in "<'DAYBREAK CENTRAL HIGH'>"
  And "<'ORALIA SIMMER'>" exited "<'DAYBREAK CENTRAL HIGH'>" on "2010-05-01"
  And "2010-05-01" is within the grace period
  When I make an API call to get "<'ORALIA SIMMER'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

Scenario: Educator accessing a student that he/she does not teach, and it is out of the grace period
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And I do not teach "<'LUCRETIA NAGAI'>"
  And I taught "<'LUCRETIA NAGAI'>" in "<'FALL 2001'>"
  And "<'FALL 2001 END DATE'>" is outside of the grace period
  When I make an API call to get "<'LUCRETIA NAGAI'>"
  Then I should receive a return code of 403
  When I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And I make an API call to get "<'LUCRETIA NAGAI'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response

@wip
Scenario: Leader accessing a student that is not enrolled in his/her school, and was before but that was out of the grace period
Given I am a valid authenticated SEA/LEA end user
And my role is Leader
And my school is <school>
And <student> is not enrolled in <school>
And <student> was enrolled in <school> <days> ago
And <days> is greater than <grace period>
When I make an API call to get <student>
Then I receive a message that is not authorized