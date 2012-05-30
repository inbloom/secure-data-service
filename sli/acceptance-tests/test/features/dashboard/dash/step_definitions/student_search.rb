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

Then /^"([^"]*)" results are returned in the page$/ do |numResults|
  #make sure we're on the next/prev page of results
  @explicitWait.until{@currentSearchResultsUrl != @driver.current_url}
  if (numResults.to_i > 0)
    grid = getStudentGrid()
    assert(numResults.to_i == grid.length, "Expected number of results: " + numResults + " Actual: " + grid.length.to_s)
  end
end

When /^I enter nothing into either field of student search$/ do
  putTextToField("", "dbrd_inp_search_firstName", "id")
  putTextToField("", "dbrd_inp_search_lastName", "id")
end

Then /^the search results has the following entries:$/ do |table|
  mapping = {
    "Student" => "fullName",
    "Grade" => "gradeLevel",
    "School" => "currentSchoolName"
  }
  checkGridEntries(@driver, table, mapping)
end

Then /^the search results include:$/ do |table|
  mapping = {
    "Student" => "fullName",
    "Grade" => "gradeLevel",
    "School" => "currentSchoolName"
  }
  checkGridEntries(@driver, table, mapping, false)
end

When /^I send the enter key$/ do
  # This only sends the enter key to the last name text box
  @driver.find_element(:id, "dbrd_inp_search_lastName").send_keys(:enter)
end

Then /^I should be informed that "([^"]*)" results are returned$/ do |numResults|
  @explicitWait.until{@driver.current_url.include?("studentSearchPage") == true}
  expectedText = "returned " + numResults + " results"
  if (numResults.to_i == 1)
    expectedText = "returned " + numResults + " result"
  end
  checkForTextInBody(expectedText)
end

Then /^I click on the next page$/ do
  @currentSearchResultsUrl = @driver.current_url
  nextButton = @driver.find_element(:id, "searchNextBtn").click
end

Then /^I click on the previous page$/ do
  @currentSearchResultsUrl = @driver.current_url
  prevButton = @driver.find_element(:id, "searchPrevBtn").click
end

Then /^I select page size of "([^"]*)"$/ do |pageSize|
  selectOption("pageSizeSelect", pageSize.to_s)
end