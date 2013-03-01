=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\nCurrent Time: "+Time.now.getutc.to_s+"\n\n"+message
end

def lower_timeout_for_same_page_validation
  #used for same page validation
  @driver.manage.timeouts.implicit_wait = 10 # seconds
  #@driver.manage.timeouts.implicit_wait = 2 # seconds
end

def reset_timeouts_to_default
  @driver.manage.timeouts.implicit_wait = 10 # seconds
end

Given /^I have an open web browser$/ do
  browser = PropLoader.getProps['browser'].downcase
  if (browser == "ie")
    @driver ||= Selenium::WebDriver.for :ie
  elsif (browser == "chrome")
    @driver ||= Selenium::WebDriver.for :chrome
  else
    @profile ||= Selenium::WebDriver::Firefox::Profile.new
    @profile['network.http.prompt-temp-redirect'] = false
    # if osx, use firefox background script
    #if Selenium::WebDriver::Firefox::Binary.path['/Applications/Firefox.app'] != nil
    #  Selenium::WebDriver::Firefox::Binary.path = 'test/features/utils/firefox_in_background.sh'
    #end
    @driver ||= Selenium::WebDriver.for :firefox, :profile => @profile
  end
  
  reset_timeouts_to_default
end

When /^I wait for a second$/ do
  sleep(1)
end

When /^I wait for "([^"]*)" seconds$/ do |secs|
  sleep(Integer(secs))
end

When /^I was redirected to the "([^"]*)" IDP Login page$/ do |idpType|
  if idpType=="OpenAM"
    assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:id, "IDToken1")}
  elsif idpType=="ADFS"
    assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:id, "ctl00_ContentPlaceHolder1_SubmitButton")}
  elsif idpType=="Simple"
    assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:id, "login_button")}
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end
end
When /^I submit the developer credentials "([^"]*)" "([^"]*)" for the impersonation login page$/ do |user, pass|
  @driver.find_element(:id, "user_id").send_keys user
  @driver.find_element(:id, "password").send_keys pass
  @driver.find_element(:id, "login_button").click
end

When /^I submit the credentials "([^"]*)" "([^"]*)" for the "([^"]*)" login page$/ do |user, pass, idpType|
  disable_NOTABLESCAN
  if idpType=="OpenAM"
    @driver.find_element(:id, "IDToken1").send_keys user
    @driver.find_element(:id, "IDToken2").send_keys pass
    @driver.find_element(:name, "Login.Submit").click
    begin
      @driver.switch_to.alert.accept
    rescue
    end
  elsif idpType=="ADFS"
    @driver.find_element(:id, "ctl00_ContentPlaceHolder1_UsernameTextBox").send_keys user
    @driver.find_element(:id, "ctl00_ContentPlaceHolder1_PasswordTextBox").send_keys pass
    @driver.find_element(:id, "ctl00_ContentPlaceHolder1_SubmitButton").click
  elsif idpType=="Simple"
    @driver.find_element(:id, "user_id").send_keys user
    @driver.find_element(:id, "password").send_keys pass
    @driver.find_element(:id, "login_button").click
    if @driver.title=="Sandbox User Impersonation"
      #handle sandbox admin/impersonation chooser page
        @driver.find_element(:id, "adminLink").click
    end
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end
  sleep 3 # wait 3 seconds before enabling notablescan flag again
  enable_NOTABLESCAN

end

After do |scenario|
  begin
    File.delete("./cats_with_lasers.png")
  rescue Exception => e
  end
  #puts "Running the After hook for Scenario: #{scenario}"s
  begin
    File.rm("./cats_with_lasers.png")
  rescue
  end
  if (scenario.failed? and !@driver.nil?)
    @driver.save_screenshot("./cats_with_lasers.png")
  else
    File.new("./dummy_placeholder.png", "w")
  end
  @driver.quit if @driver
end

AfterStep('@pause') do
  # Debug function only:
  # To enable, tag your feature/scenario/step with @pause and hit enter to step through the selenium test
  print "Press Return to continue..." 
  STDIN.getc  
end 

AfterStep do |scenario|
    @count ||= 0
    if ENV['SCREENSHOTS']
        @count = @count + 1
        #filename = scenario.feature.title + "#" + scenario.title + "#" + Time.new().strftime("%H:%M:%S")+ ".png"
        filename = scenario.line.to_s() + "#" + Time.new().strftime("%H:%M:%S") + ":" + @count.to_s() + ".png"
        #filename = filename.gsub(' ', '_').gsub(',', '')
        system("xwd -root | xwdtopnm 2> /dev/null | pnmtopng -compression 9 > #{ENV['SCREENSHOTS']}/#{filename} 2> /dev/null")
    end
end

def assertWithWait(msg, &blk)
  wait = Selenium::WebDriver::Wait.new(:timeout => 5)
  begin
    wait.until {yield}
  rescue
    puts webdriverDebugMessage(@driver,msg)
  end
  assert(yield, webdriverDebugMessage(@driver,msg))
end

When /^I click on Ok$/ do
  @driver.switch_to.alert.accept
end

