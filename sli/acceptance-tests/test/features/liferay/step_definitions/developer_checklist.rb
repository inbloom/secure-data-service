Then /^I should see Developer Checklist$/ do
  @checklist = @driver.find_element(:css,"div[class*='devCheckList']")
end

Then /^Nothing is checked off in Developer Checklist$/ do
  rows = @checklist.find_element(:tag_name, "tbody").find_elements(:tag_name, "tr")
  # Ignore the first row, which is "task"
  assert(rows.count == 6, "Checklist count is not equal to 6. Actual count #{rows.count}")
  for i in (1..rows.size-1)
    verifyRowIsUnchecked(rows[i])
  end
end

Then /^I should see a check in "(.*?)"$/ do |taskName|
  rows = @checklist.find_element(:tag_name, "tbody").find_elements(:tag_name, "tr")
  rows.each do |row|
    html = row.attribute('innerHTML')
    if (html.include? "taskName")
      assert(html.include? "icon-ok", "row is not checked")
    end
  end
end

Then /^I click on Don't show this again$/ do
  @checklist.find_element(:id, "doNotShowCheckList").click
end

Then /^I see an Apply button$/ do
  @checklist.find_element(:id, "apply_button")
end

def verifyRowIsUnchecked(row)
  assert((!(row.attribute('innerHTML')).include? "icon-ok"), "row is checked as complete")
end

