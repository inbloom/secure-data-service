@wip

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
| "idp1" | "jdoe"    | "jdoe1234"    | "Fry High School"           |
| "idp2" | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "idp1" | "ejane"   | "ejane1234"   | "Watson Elementary School"  |

Scenario Outline: Authenticated Educator makes API call to get not own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | OtherSchool                 |
| "idp1" | "tbear"    | "tbear1234"    | "Fry High School"           | "Parker-Dust Middle School" |
| "idp1" | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Fry High School"           |
| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp1" | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

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
| "idp1" | "tbear"  | "tbear1234" | "Fry High School"           | "Ted Bear"       |
| "idp2" | "ejane"  | "ejane1234" | "Parker-Dust Middle School" | "Emily Jane"     |
| "idp1" | "ejane"  | "ejane1234" | "Watson Elementary School"  | "Elizabeth Jane" |
| "idp1" | "jdoe"   | "jdoe1234"  | "Fry High School"           | "John Doe 1"     |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers within own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <School>
Then I receive a JSON response that includes a list of teachers from school <School>
Examples:
| Realm  | Username  | Password      | School                      |
| "idp1" | "jdoe"    | "jdoe1234"    | "Fry High School"           |
| "idp2" | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "idp1" | "ejane"   | "ejane1234"   | "Watson Elementary School"  |
| "idp2" | "ejane"   | "ejane1234"   | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers not in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | OtherSchool                 |
| "idp1" | "jdoe"     | "jdoe1234"     | "Fry High School"           | "Parker-Dust Middle School" |
| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp1" | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get Teacher in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I receive a JSON response that includes the teacher <Teacher> and its attributes
Examples:
| Realm  | Username   | Password       | School                      | Teacher      |
| "idp1" | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 1" |
| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "John Doe 3" |
| "idp1" | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "John Doe 2" |

Scenario Outline: Authenticated Educator makes API call to get Teacher not in own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | Teacher          |
| "idp1" | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 3"     |
| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Elizabeth Jane" |
| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Elizabeth Jane" |
| "idp1" | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Ted Bear"       |

#Section

Scenario: Authenticated Educator makes API call to get list of Sections
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get my list of sections
Then I receive a JSON response that includes the list of sections that <Teacher> teaches only

Scenario Outline: Authenticated Educator makes API call to get own Section
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach in <Section>
When I make an API call to get <Section>
Then I receive a JSON response that includes the section <Section> and its attributes
Examples:
| Realm  | Username  | Password      | Section          |
| "idp1" | "jdoe"    | "jdoe1234"    | "FHS-Science101" |
| "idp2" | "johndoe" | "johndoe1234" | "PDMS-Geometry"  |
| "idp1" | "ejane"   | "ejane1234"   | "WES-Math"       |
| "idp1" | "jdoe"    | "jdoe1234"    | "FHS-Math101"    |
| "idp1" | "tbear"   | "tbear1234"   | "FHS-Science101" |

Scenario Outline: Authenticated Educator makes API call to get not own Section
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get <Section>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | Section          |
| "idp1" | "jdoe"     | "jdoe1234"     | "FHS-English101" |
| "idp2" | "johndoe"  | "johndoe1234"  | "FHS-Math101"    |
| "idp2" | "ejane"    | "ejane1234"    | "WES-Math"       |
| "idp1" | "ejane"    | "ejane1234"    | "PDMS-Trig"      |
| "idp1" | "tbear"    | "tbear1234"    | "FHS-Math101"    |
| "idp1" | "john_doe" | "john_doe1234" | "FHS-English101" |

#Student

Scenario: Authenticated Educator makes API call to get list of Students
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get my list of students
Then I receive a JSON response that includes the Students that I am teaching only

Scenario Outline: Authenticated Educator makes API call to get Student that he/she is teaching
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach the student <Student>
When I make an API call to get the student <Student>
Then I receive a JSON response that includes the student <Student> and its attributes
Examples:
| Realm  | Username   | Password       | Student        |
| "idp1" | "jdoe"     | "jdoe1234"     | "Doris Hanes"  |
| "idp1" | "jdoe"     | "jdoe1234"     | "Gail Newman"  |
| "idp1" | "jdoe"     | "jdoe1234"     | "Mark Moody"   |
| "idp2" | "johndoe"  | "johndoe1234"  | "Hal Kessler"  |
| "idp2" | "johndoe"  | "johndoe1234"  | "Brock Ott"    |
| "idp2" | "johndoe"  | "johndoe1234"  | "Elnora Fin"   |
| "idp1" | "ejane"    | "ejane1234"    | "Laven Chaney" |
| "idp1" | "john_doe" | "john_doe1234" | "Laven Chaney" |
| "idp1" | "tbear"    | "tbear1234"    | "Mark Moody"   |

Scenario Outline: Authenticated Educator makes API call to get Student that he/she is not teaching
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get <Student>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | Student          |
| "idp1" | "jdoe"     | "jdoe1234"     | "Austin Durran"  |
| "idp1" | "jdoe"     | "jdoe1234"     | "Millie Lovel"   |
| "idp2" | "johndoe"  | "johndoe1234"  | "Hal Kessler"    |
| "idp2" | "ejane"    | "ejane1234"    | "Laven Chaney"   |
| "idp2" | "ejane"    | "ejane1234"    | "Freeman Marcum" |
| "idp2" | "ejane"    | "ejane1234"    | "Danny Fields"   |
| "idp2" | "johndoe"  | "johndoe1234"  | "Kristy Carillo" |
| "idp1" | "ejane"    | "ejane1234"    | "Forrest Hopper" |
| "idp1" | "john_doe" | "john_doe1234" | "Emil Oneil"     |
| "idp1" | "tbear"    | "tbear1234"    | "Doris Hanes"    |
