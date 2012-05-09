require "selenium-webdriver"
require 'net/imap'
require "mongo"
require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../utils/selenium_common.rb'

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  connection = Mongo::Connection.new
  @db = connection.db("sli")
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |link|
  url = link
  url = @link                        if link == "VALID VERIFICATION LINK"
  url = "http://www.fhasjkdhkqw.com" if link == "INVALID VERIFICATION LINK"
  url
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I check my email inbox as user "([^\"]*)" "([^\"]*)"$/ do |user, password|
  @imap = Net::IMAP.new('mon.slidev.org', 993, true, nil, false)
  @imap.authenticate('LOGIN', user, password)
  @imap.examine('INBOX')
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I navigate to "([^\"]*)"$/ do |link|
  @driver.get link
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should see the text "([^\"]*)"$/ do |text|
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text #{text}")
end

Then /^I should find an email sent from "([^\"]*)" with subject "([^\"]*)"$/ do |sender, subject|
  @emailIds = []
  @imap.search(["FROM", sender]) do |msgFromSender|
    if (@imap.fetch(msgFromSender, "ENVELOPE[subject]") == subject)
      @emailIds << msgFromSender
    end
  end
end

Then /^the email should contain a link to verify my account$/ do
  @link = @imap.fetch(@emailIds, "BODY[TEXT]")[0].attr["BODY[TEXT]"]
  @imap.disconnect
end