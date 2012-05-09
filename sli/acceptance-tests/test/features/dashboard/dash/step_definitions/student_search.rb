When /^I enter "([^"]*)" into the "([^"]*)" search box$/ do |query, textboxName|
  @explicitWait.until{@driver.find_element(:class,"student-search-form")}
  if (textboxName == "firstName")
    textboxName = "dbrd_inp_search_firstName"
  elsif (textboxName == "lastName")
    textboxName = "dbrd_inp_search_lastName"
  else
    puts "Invalid textbox name " + textboxName
  end
  putTextToField(query, textboxName, "id")
end

When /^I click the search button$/ do
  searchSection = @driver.find_element(:class, "student-search-form")
  searchSection.find_element(:class,"btn").click
end

Then /^"([^"]*)" results are returned$/ do |numResults|
  assert(@driver.current_url.include?("studentSearchPage"), "Current URL doesn't seem to be the expected search results page: " + @driver.current_url)
  grid = getStudentGrid()
  assert(numResults.to_i == grid.length, "Expected number of results: " + numResults + " Actual: " + grid.length.to_s)
end

When /^I enter nothing into either field of student search$/ do
  putTextToField("", "dbrd_inp_search_firstName", "id")
  putTextToField("", "dbrd_inp_search_lastName", "id")
end

Then /^the search results include:$/ do |table|
  mapping = {
    "Student" => "fullName",
    "Grade" => "gradeLevel",
    "School" => "currentSchoolName"
  }
  checkGridEntries(@driver, table, mapping)
end

When /^I send the enter key$/ do
  # This only sends the enter key to the last name text box
  @driver.find_element(:id, "dbrd_inp_search_lastName").send_keys(:enter)
end