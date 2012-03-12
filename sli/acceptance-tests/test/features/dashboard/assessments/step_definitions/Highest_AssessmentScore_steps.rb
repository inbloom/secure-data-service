require_relative '../../../utils/sli_utils.rb'

Then /^I should see his\/her highest ISAT Writing Scale Score is "([^"]*)"$/ do |scoreResult|
  scores = @driver.find_elements(:id, @studentName+".ISAT Writing.Scale score")
  scores.should_not be_nil
  scores[1].text.should == scoreResult
end