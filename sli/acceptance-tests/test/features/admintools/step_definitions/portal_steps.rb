Then /^I should see a portal header with my e\-mail address$/ do
  browser.page.should have_selector('a#email-link', :text => 'slcoperator@slidev.org')
end