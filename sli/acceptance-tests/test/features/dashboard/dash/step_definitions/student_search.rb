When /^I enter "([^"]*)" into the "([^"]*)" search box$/ do |query, textboxName|
  
end

When /^I click the search button$/ do
  
end

Then /^"([^"]*)" results are returned$/ do |numResults|
  
end

When /^I enter nothing into either field of student search$/ do
  
end

Then /^the search results include:$/ do |table|
  table.hashes.each do |row|
    student = row['Student']
    grade = row['Grade']
    school = row['School']
  end
end

Then /^I click on the browser back button$/ do
  @driver.navigate().back()
end
