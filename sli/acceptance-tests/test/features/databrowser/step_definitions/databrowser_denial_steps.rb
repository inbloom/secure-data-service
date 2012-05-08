
Then /^I get message that I am not authorized$/ do
  assertWithWait("Should have received message that databrowser could not be accessed") { @driver.page_source.index("You are not authorized to use this app." ) != nil}
end
