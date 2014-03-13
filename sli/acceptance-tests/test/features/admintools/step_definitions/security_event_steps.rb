Before('@security_event') do
  clear_security_events
end

def clear_security_events
  db_client = DbClient.new
  db_client.allow_table_scan!
  db_client.for_sli.open do |db|
    db.remove 'securityEvent', {}
  end
end

After('@security_event') do
  DbClient.new.disallow_table_scan!
end

Then /^I should see the error header for (\d+)/ do |error|
  browser.page.should have_selector('.error-header', :text => error)
end

Then /^I should (not )?see an error message indicating (?:that )?"([^"]+)"$/ do |not_see, message|
  selector = '.error-content,.alert-error'
  if not_see
    browser.page.should have_no_selector(selector, :text => message)
  else
    browser.page.should have_selector(selector, :text => message)
  end
end

Then /^I should see security events for:$/ do |table|
  db_client = DbClient.new.for_sli
  table.raw.each do |row|
    db_client.count('securityEvent',{'body.logMessage'=>/#{row.first}/i}).should be(1), "Expected to find a security event with logMessage containing '#{row.first}'"
  end
  count = db_client.count('securityEvent')
  count.should be(table.raw.count), "Expected a total of #{table.raw.count} security events, got #{count}."
end
