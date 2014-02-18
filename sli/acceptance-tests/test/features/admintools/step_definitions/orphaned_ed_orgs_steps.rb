Then /^the edorg "(.*?)" is present in the list of orphans$/ do |edorg_id|
  element = @driver.find_element(:id, "orphan_#{edorg_id}")
  element.displayed?.should == true
end

And /^the edorg "(.*?)" is not present in the tree$/ do |edorg_id|
  elements2 = @driver.find_elements(:xpath, "//input[@type='checkbox' and @class='edorgId' and @id='#{edorg_id}']")
  elements2.size.should == 0
end

=begin

<input id="d004116d-9b
=end