Feature: Context-based Permissions for Educator 

I would like to implement context-based permissions, so that when a SEA/LEA end user (that represents an Educator) access the SLI, he/she is provided with the student data that the Educator has relationship with.

#School

Scenario Outline: Authenticated Educator makes API call to get own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the school <School>
Then I receive a JSON response that includes the school <School> and its attributes
Examples:
| Realm | Username  | Password      | School                      |
| "IL"  | "jdoe"    | "jdoe1234"    | "Fry High School"           |
| "NY"  | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "IL"  | "ejane"   | "ejane1234"   | "Watson Elementary School"  |

Scenario Outline: Authenticated Educator makes API call to get not own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | School                      | OtherSchool                 |
| "IL"  | "tbear"    | "tbear1234"    | "Fry High School"           | "Parker-Dust Middle School" |
| "IL"  | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Fry High School"           |
| "NY"  | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "NY"  | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "IL"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

#Teacher

Scenario Outline: Authenticated Educator makes API call to get self (Teacher)
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I receive a JSON response that includes the teacher <Teacher> and its attributes
Examples:
| Realm | Username | Password    | School                      | Teacher          |
| "IL"  | "tbear"  | "tbear1234" | "Fry High School"           | "Ted Bear"       |
| "NY"  | "ejane"  | "ejane1234" | "Parker-Dust Middle School" | "Emily Jane"     |
| "IL"  | "ejane"  | "ejane1234" | "Watson Elementary School"  | "Elizabeth Jane" |
| "IL"  | "jdoe"   | "jdoe1234"  | "Fry High School"           | "John Doe 1"     |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers within own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <School>
Then I receive a JSON response that includes a list of teachers from school <School>
Examples:
| Realm | Username  | Password      | School                      |
| "IL"  | "jdoe"    | "jdoe1234"    | "Fry High School"           |
| "NY"  | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "IL"  | "ejane"   | "ejane1234"   | "Watson Elementary School"  |
| "NY"  | "ejane"   | "ejane1234"   | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get list of Teachers not in own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get list of teachers from the school <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | School                      | OtherSchool                 |
| "IL"  | "jdoe"     | "jdoe1234"     | "Fry High School"           | "Parker-Dust Middle School" |
| "NY"  | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "NY"  | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "IL"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

Scenario Outline: Authenticated Educator makes API call to get Teacher in own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I receive a JSON response that includes the teacher <Teacher> and its attributes
Examples:
| Realm | Username   | Password       | School                      | Teacher      |
| "IL"  | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 1" |
| "NY"  | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "John Doe 3" |
| "IL"  | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "John Doe 2" |

Scenario Outline: Authenticated Educator makes API call to get Teacher not in own School
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get the teacher <Teacher>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | School                      | Teacher          |
| "IL"  | "tbear"    | "tbear1234"    | "Fry High School"           | "John Doe 3"     |
| "NY"  | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Elizabeth Jane" |
| "NY"  | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Elizabeth Jane" |
| "IL"  | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Ted Bear"       |

#Section
Scenario Outline: Authenticated Educator makes API call to get own list of Sections
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get the list of sections taught by the teacher <Teacher>
Then I receive a JSON response that includes the list of sections that <Teacher> teaches
Examples:
| Realm | Username   | Password       | Teacher      |
| "IL"  | "tbear"    | "tbear1234"    | "Ted Bear"   |
| "NY"  | "johndoe"  | "johndoe1234"  | "John Doe 3" |
| "NY"  | "ejane"    | "ejane1234"    | "Emily Jane" |
| "IL"  | "john_doe" | "john_doe1234" | "John Doe 2" |

Scenario Outline: Authenticated Educator makes API call to get other teacher's' list of Sections
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get the list of sections taught by the teacher <Teacher>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password      | Teacher          |
| "IL"  | "tbear"    | "tbear1234"   | "John Doe 3"     |
| "NY"  | "johndoe"  | "johndoe1234" | "Ted Bear"       |
| "IL"  | "ejane"    | "ejane1234"   | "Emily Jane"     |
| "NY"  | "ejane"    | "ejane1234"   | "Elizabeth Jane" |

#DE719
#@wip
Scenario Outline: Authenticated Educator makes API call to get own Section
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And I teach in <Section>
When I make an API call to get the section <Section>
Then I receive a JSON response that includes the section <Section> and its attributes
Examples:
| Realm | Username  | Password      | Section          |
| "IL"  | "jdoe"    | "jdoe1234"    | "FHS-Science101" |
| "NY"  | "johndoe" | "johndoe1234" | "PDMS-Geometry"  |
| "NY"  | "ejane"   | "ejane1234"   | "WES-Math"       |
| "IL"  | "jdoe"    | "jdoe1234"    | "FHS-Math101"    |
| "IL"  | "tbear"   | "tbear1234"   | "FHS-Science101" |

Scenario Outline: Authenticated Educator makes API call to get not own Section
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get the section <Section>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | Section          |
| "NY"  | "johndoe"  | "johndoe1234"  | "FHS-Math101"    |
| "IL"  | "ejane"    | "ejane1234"    | "WES-Math"       |
| "NY"  | "ejane"    | "ejane1234"    | "PDMS-Trig"      |
| "IL"  | "john_doe" | "john_doe1234" | "FHS-English101" |

#Student

#DE719
#@wip
Scenario Outline: Authenticated Educator makes API call to get list of Students in section they teach
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get a list of students in the section <Section>
Then I receive a JSON response that includes the list of students in section <Section>
Examples:
| Realm | Username  | Password      | Section          |
| "IL"  | "jdoe"    | "jdoe1234"    | "FHS-Science101" |
| "NY"  | "johndoe" | "johndoe1234" | "PDMS-Geometry"  |
| "IL"  | "ejane"   | "ejane1234"   | "WES-Math"       |
| "IL"  | "jdoe"    | "jdoe1234"    | "FHS-Math101"    |
| "IL"  | "tbear"   | "tbear1234"   | "FHS-Science101" |

#DE719
#@wip
Scenario Outline: Authenticated Educator makes API call to get list of Students in section they do not teach
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get a list of students in the section <Section>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | Section          |
| "NY"  | "johndoe"  | "johndoe1234"  | "FHS-Math101"    |
| "NY"  | "ejane"    | "ejane1234"    | "WES-Math"       |
| "IL"  | "ejane"    | "ejane1234"    | "PDMS-Trig"      |
| "IL"  | "john_doe" | "john_doe1234" | "FHS-English101" |

#DE719
#@wip
Scenario Outline: Authenticated Educator makes API call to get Student that he/she is teaching
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
And I teach the student <Student>
When I make an API call to get the student <Student>
Then I receive a JSON response that includes the student <Student> and its attributes
Examples:
| Realm | Username   | Password       | Student         |
| "IL"  | "jdoe"     | "jdoe1234"     | "Doris Hanes"   |
| "IL"  | "jdoe"     | "jdoe1234"     | "Gail Newman"   |
| "IL"  | "jdoe"     | "jdoe1234"     | "Mark Moody"    |
| "NY"  | "johndoe"  | "johndoe1234"  | "Hal Kessler"   |
| "NY"  | "johndoe"  | "johndoe1234"  | "Brock Ott"     |
| "NY"  | "johndoe"  | "johndoe1234"  | "Elnora Fin"    |
| "IL"  | "ejane"    | "ejane1234"    | "Lavern Chaney" |
| "IL"  | "john_doe" | "john_doe1234" | "Lavern Chaney" |
| "IL"  | "tbear"    | "tbear1234"    | "Mark Moody"    |

Scenario Outline: Authenticated Educator makes API call to get Student that he/she is not teaching
Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Educator"
When I make an API call to get the student <Student>
Then I should get a message that I am not authorized
Examples:
| Realm | Username   | Password       | Student          |
| "IL"  | "jdoe"     | "jdoe1234"     | "Austin Durran"  |
| "IL"  | "jdoe"     | "jdoe1234"     | "Millie Lovel"   |
| "NY"  | "ejane"    | "ejane1234"    | "Lavern Chaney"  |
| "NY"  | "ejane"    | "ejane1234"    | "Freeman Marcum" |
| "NY"  | "ejane"    | "ejane1234"    | "Danny Fields"   |
| "NY"  | "johndoe"  | "johndoe1234"  | "Kristy Carillo" |
| "IL"  | "ejane"    | "ejane1234"    | "Forrest Hopper" |
| "IL"  | "john_doe" | "john_doe1234" | "Emil Oneil"     |
| "IL"  | "tbear"    | "tbear1234"    | "Doris Hanes"    |
