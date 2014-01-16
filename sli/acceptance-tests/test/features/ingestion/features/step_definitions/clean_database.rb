############################################################
# Given
############################################################
Given /^all collections are empty$/ do
  steps %Q{
    Given the "Midgar" tenant db is empty
  }
end