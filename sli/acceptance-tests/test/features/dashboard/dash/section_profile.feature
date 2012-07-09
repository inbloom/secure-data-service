=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'selenium-webdriver'

Feature: Section Profile View

As a teacher in a school district, I want to navigate to a section, and be directed to the section profile page.

Background:
Given I have an open web browser

@RALLY_US2817
Scenario: View section's profile
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"

When I view its section profile
#Name check
Then the section name shown in section profile is "8th Grade English - Sec 6"
And the teacher name shown in section profile is "Mrs Linda Kim"
And the course name shown in section profile is "8th Grade English"
And the subject name shown in section profile is "!"