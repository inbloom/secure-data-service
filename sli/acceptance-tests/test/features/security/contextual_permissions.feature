Feature: Context-based Permissions for Educator 

I would like to implement context-based permissions, so that when a SEA/LEA end user (that represents an Educator) access the SLI, he/she is provided with the student data that the Educator has relationship with.

#School

Scenario Outline: Authenticated Educator makes API call to get own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the school <School>
Then I receive a JSON response that includes the school <School> and its attributes
Examples:
| Realm  | Username  | Password      | School                      |
| "SLI"  | "jdoe"    | "jdoe1234"    | "Fry High School"           |
#| "idp2" | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "SLI"  | "ejane"   | "ejane1234"   | "Watson Elementary School"  |

Scenario Outline: Authenticated Educator makes API call to get not own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | OtherSchool                 |
| "SLI"  | "tbear"    | "tbear1234"    | "Fry High School"           | "Parker-Dust Middle School" |
| "SLI"  | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Fry High School"           |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
#| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "SLI"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

#Teacher

Scenario Outline: Authenticated Educator makes API call to get self (Teacher)
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I receive a JSON response that includes the teacher <Teacher> and its attributes
Examples:
| Realm  | Username | Password    | School                      | Teacher          |
| "SLI"  | "tbear"  | "tbear1234" | "Fry High School"           | "Ted Bear"       |
#| "idp2" | "ejane"  | "ejane1234" | "Parker-Dust Middle School" | "Emily Jane"     |
| "SLI"  | "ejane"  | "ejane1234" | "Watson Elementary School"  | "Elizabeth Jane" |
| "SLI"  | "jdoe"   | "jdoe1234"  | "Fry High School"           | "John Doe 1"     |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers within own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <School>
Then I receive a JSON response that includes a list of teachers from school <School>
Examples:
| Realm  | Username  | Password      | School                      |
| "SLI"  | "jdoe"    | "jdoe1234"    | "Fry High School"           |
#| "idp2" | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "SLI"  | "ejane"   | "ejane1234"   | "Watson Elementary School"  |
#| "idp2" | "ejane"   | "ejane1234"   | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers not in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | OtherSchool                 |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Fry High School"           | "Parker-Dust Middle School" |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
#| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "SLI"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get Teacher in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I receive a JSON response that includes the teacher <Teacher> and its attributes
Examples:
| Realm  | Username   | Password       | School                      | Teacher      |
| "SLI"  | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 1" |
#| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "John Doe 3" |
| "SLI"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "John Doe 2" |

Scenario Outline: Authenticated Educator makes API call to get Teacher not in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | Teacher          |
| "SLI"  | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 3"     |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Elizabeth Jane" |
#| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Elizabeth Jane" |
| "SLI"  | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Ted Bear"       |

#Section

Scenario Outline: Authenticated Educator makes API call to get own list of Sections
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get the list of sections taught by the teacher <Teacher>
Then I receive a JSON response that includes the list of sections that <Teacher> teaches
Examples:
| Realm  | Username   | Password       | Teacher      |
| "SLI"  | "tbear"    | "tbear1234"    | "Ted Bear"   |
#| "idp2" | "johndoe"  | "johndoe1234"  | "John Doe 3" |
#| "idp2" | "ejane"    | "ejane1234"    | "Emily Jane" |
| "SLI"  | "john_doe" | "john_doe1234" | "John Doe 2" |

Scenario Outline: Authenticated Educator makes API call to get other teacher's' list of Sections
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get the list of sections taught by the teacher <Teacher>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password      | Teacher          |
| "SLI"  | "tbear"    | "tbear1234"   | "John Doe 3"     |
#| "idp2" | "johndoe"  | "johndoe1234" | "Ted Bear"       |
| "SLI"  | "ejane"    | "ejane1234"   | "Emily Jane"     |
#| "idp2" | "ejane"    | "ejane1234"   | "Elizabeth Jane" |
#| "idp1" | "jdoe"     | "jdoe1234"    | "Ted Bear"       | #disabled, would get shared sections

Scenario Outline: Authenticated Educator makes API call to get own Section
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach in <Section>
When I make an API call to get the section <Section>
Then I receive a JSON response that includes the section <Section> and its attributes
Examples:
| Realm  | Username  | Password      | Section          |
| "SLI"  | "jdoe"    | "jdoe1234"    | "FHS-Science101" |
#| "idp2" | "johndoe" | "johndoe1234" | "PDMS-Geometry"  |
| "SLI"  | "ejane"   | "ejane1234"   | "WES-Math"       |
| "SLI"  | "jdoe"    | "jdoe1234"    | "FHS-Math101"    |
| "SLI"  | "tbear"   | "tbear1234"   | "FHS-Science101" |

#Temporary WIP, need to figure out why they were commented out and why the regression was not addressed
@wip
Scenario Outline: Authenticated Educator makes API call to get not own Section
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get the section <Section>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | Section          |
| "SLI"  | "jdoe"     | "jdoe1234"     | "FHS-English101" |
#| "idp2" | "johndoe"  | "johndoe1234"  | "FHS-Math101"    |
#| "idp2" | "ejane"    | "ejane1234"    | "WES-Math"       |
| "SLI"  | "ejane"    | "ejane1234"    | "PDMS-Trig"      |
| "SLI"  | "tbear"    | "tbear1234"    | "FHS-Math101"    |
| "SLI"  | "john_doe" | "john_doe1234" | "FHS-English101" |

#Student

Scenario Outline: Authenticated Educator makes API call to get list of Students in section they teach
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get a list of students in the section <Section>
Then I receive a JSON response that includes the list of students in section <Section>
Examples:
| Realm  | Username  | Password      | Section          |
| "SLI"  | "jdoe"    | "jdoe1234"    | "FHS-Science101" |
#| "idp2" | "johndoe" | "johndoe1234" | "PDMS-Geometry"  |
| "SLI"  | "ejane"   | "ejane1234"   | "WES-Math"       |
| "SLI"  | "jdoe"    | "jdoe1234"    | "FHS-Math101"    |
| "SLI"  | "tbear"   | "tbear1234"   | "FHS-Science101" |

#Temporary WIP, need to figure out why they were commented out and why the regression was not addressed
@wip
Scenario Outline: Authenticated Educator makes API call to get list of Students in section they do not teach
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get a list of students in the section <Section>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | Section          |
| "SLI"  | "jdoe"     | "jdoe1234"     | "FHS-English101" |
#| "idp2" | "johndoe"  | "johndoe1234"  | "FHS-Math101"    |
#| "idp2" | "ejane"    | "ejane1234"    | "WES-Math"       |
| "SLI"  | "ejane"    | "ejane1234"    | "PDMS-Trig"      |
| "SLI"  | "tbear"    | "tbear1234"    | "FHS-Math101"    |
| "SLI"  | "john_doe" | "john_doe1234" | "FHS-English101" |

Scenario Outline: Authenticated Educator makes API call to get Student that he/she is teaching
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach the student <Student>
When I make an API call to get the student <Student>
Then I receive a JSON response that includes the student <Student> and its attributes
Examples:
| Realm  | Username   | Password       | Student        |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Doris Hanes"  |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Gail Newman"  |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Mark Moody"   |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Hal Kessler"  |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Brock Ott"    |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Elnora Fin"   |
| "SLI"  | "ejane"    | "ejane1234"    | "Lavern Chaney" |
| "SLI"  | "john_doe" | "john_doe1234" | "Lavern Chaney" |
| "SLI"  | "tbear"    | "tbear1234"    | "Mark Moody"   |

Scenario Outline: Authenticated Educator makes API call to get Student that he/she is not teaching
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get the student <Student>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | Student          |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Austin Durran"  |
| "SLI"  | "jdoe"     | "jdoe1234"     | "Millie Lovel"   |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Hal Kessler"    |
#| "idp2" | "ejane"    | "ejane1234"    | "Lavern Chaney"   |
#| "idp2" | "ejane"    | "ejane1234"    | "Freeman Marcum" |
#| "idp2" | "ejane"    | "ejane1234"    | "Danny Fields"   |
#| "idp2" | "johndoe"  | "johndoe1234"  | "Kristy Carillo" |
| "SLI"  | "ejane"    | "ejane1234"    | "Forrest Hopper" |
| "SLI"  | "john_doe" | "john_doe1234" | "Emil Oneil"     |
| "SLI"  | "tbear"    | "tbear1234"    | "Doris Hanes"    |
