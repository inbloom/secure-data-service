require_relative '../../utils/sli_utils.rb'

Then /^I should receive a collection of "([^"]*)" entities$/ do |number_of_entities|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_entities), "Expected response of size #{number_of_entities}, received #{@result.length}");
end