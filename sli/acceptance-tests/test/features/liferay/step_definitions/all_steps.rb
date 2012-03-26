require 'rubygems'
require 'selenium-webdriver'#run the file ruby selenium.rb 'Name of your selection releam' 'username' 'password'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
#driver = Selenium::WebDriver.for :firefox


Given /^an admin user Demo exists with "([^\"]*)" and "([^\"]*)"$/ do |username,password|
  

end

Given /^a normal user Educator exists with "([^\"]*)" and "([^\"]*)"$/ do |username,password|
  
  #User.new(:username => username, :password => password, :password_confirmation => password).save! unless User.exists?(:username => username)
 # User.exists?(:username => username)
end

Then /^I am on the Realm selection page$/ do
  @driver.navigate.to "https://devlr2.slidev.org"
end

Then /^I select "([^\"]*)"$/ do |text|

a=@driver.find_element(:name,'realmId') #realmId should be the html tag name of select tag
options=a.find_elements(:tag_name=>"option") # all the options of that select tag will be selected
options.each do |g|
  if g.text == text
    g.click
    break
  end
end
ele=driver.find_element(:id, "go")
ele.click

  #select(text, :from => 'realmId') 
end

Then /^I click "([^\"]*)"$/ do |btn_text|
  click_button(btn_text)
end 

Given /^EULA has been accepted$/ do

end

When /^I go to the login page$/ do
@driver.navigate.to "https://devlr2.slidev.org"
a=@driver.find_element(:name,'realmId') #realmId should be the html tag name of select tag
options=a.find_elements(:tag_name=>"option") # all the options of that select tag will be selected
options.each do |g|
  if g.text == 'Shared Learning Infrastructure'
    g.click
    break
  end
end
ele=driver.find_element(:id, "go")
ele.click


  #visit "https://devlr2.slidev.org"
 # select('Shared Learning Infrastructure', :from => 'realmId')
  #click_button('Go')
end




Then /^I should logged out$/ do
  @driver.find_element(:link, 'Logout').click
  #click_link('Logout')
end

Then /^I should be on the home page$/ do
  @driver.find_element(:link, 'Logout').displayed? ||   @driver.find_element(:link, 'Sign out').displayed?

end



#Then /^(?:|I )should be on (.+)$/ do |page_name|
#  current_path = URI.parse(current_url).select(:path, :query).compact.join('?')
#  if defined?(Spec::Rails::Matchers)
#    current_path.should == path_to(page_name)
#  else
#    assert_equal path_to(page_name), current_path
#  end
#end


When /^I login with "([^\"]*)" and "([^\"]*)"$/ do |username, password|
  
  element = driver.find_element(:id, 'IDToken1') #the username field id is IDToken1
  element.send_keys username

  element = driver.find_element(:id, 'IDToken2') #the username field id is IDToken2
  element.send_keys password
  element=driver.find_element(:class, "Btn1Def")
  element.click
end
Then /^I should be on the authentication failed page$/ do
 @driver.navigate.to "https://devopenam1.slidev.org:80/idp2/UI/Login"
end

#When /^I login as Normal with "([^\"]*)" and "([^\"]*)"$/ do |username, password|
  
  
 # fill_in "IDToken1", :with=>username
 # fill_in "IDToken2", :with=>password
 # click_button "Log In"
#end


Then /^(?:|I )should see "([^\"]*)"$/ do |text|
  begin
   link=@driver.find_element(:link, text).displayed? || @driver.find_element(:name, text).displayed? 
   link=true
  rescue
   link=false
  end
  link 
  #page.should have_content(text)
end

Then /^(?:|I )should not see "([^\"]*)"$/ do |text|
  begin
   link=@driver.find_element(:link, text).displayed? || @driver.find_element(:name, text).displayed? 
   link=true
  rescue
   link=false
  end 
  link

#  page.should_not have_content(text)
end
When /^(?:|I )follow "([^\"]*)"$/ do |link|
  @driver.find_element(:link, link).click
  #click_link(link)
end


#Then /^(?:|I )should be on (.+)$/ do |page_name|
#  current_path = URI.parse(current_url).select(:path, :query).compact.join('?')
#  if defined?(Spec::Rails::Matchers)
#    current_path.should == path_to(page_name)
#  else
#    assert_equal path_to(page_name), current_path
#  end
#end
