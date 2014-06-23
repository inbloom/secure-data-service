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

def webdriverDebugMessage(driver, message='Webdriver could not achieve expected results')
  "Debug Information\nCurrent Page: #{driver.title}\nCurrent URL: #{driver.current_url}\nCurrent Time: #{Time.now.getutc.to_s}\n\n#{message}"
end

def lower_timeout_for_same_page_validation
  #used for same page validation
  @driver.manage.timeouts.implicit_wait = 10 # seconds
end

def reset_timeouts_to_default
  @driver.manage.timeouts.implicit_wait = 10 # seconds
end

Given /^I have an open web browser$/ do
  browser = Property[:browser]
  if (browser == "ie")
    @driver ||= Selenium::WebDriver.for :ie
  elsif (browser == "chrome")
    @driver ||= Selenium::WebDriver.for :chrome
  else
    @profile ||= Selenium::WebDriver::Firefox::Profile.new
    @profile['network.http.prompt-temp-redirect'] = false
    if ENV['HEADLESS'] and RUBY_PLATFORM.include? "darwin"
      Selenium::WebDriver::Firefox::Binary.path="/opt/local/bin/firefox-x11"
    end
    client = Selenium::WebDriver::Remote::Http::Default.new
    client.timeout = 120 # seconds
    @driver ||= Selenium::WebDriver.for :firefox, :profile => @profile, :http_client => client
  end
  
  reset_timeouts_to_default
end

When /^I wait for "([^"]*)" seconds$/ do |secs|
  sleep secs.to_i
end

Given /^I ping Simple IDP$/ do
  @driver.get Property['simpleIDP_login_url']
end

When /^I was redirected to the "([^"]*)" IDP Login page$/ do |idpType|
  if idpType=="Simple"
    assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:id, "login_button")}
  elsif idpType=="Shibboleth"
    assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:css, ".form-button")}
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end
end

When /^I submit the developer credentials "([^"]*)" "([^"]*)" for the impersonation login page$/ do |user, pass|
  @driver.find_element(:id, "user_id").send_keys user
  @driver.find_element(:id, "password").send_keys pass
  @driver.find_element(:id, "login_button").click
end

def ignore_security_alert
  #This function may be useful when Firefox generates a security alert
  begin
    @driver.switch_to.alert.accept
    puts 'Browser alert message accepted.'
  rescue
  end
end

When /^I submit the credentials "([^"]*)" "([^"]*)" for the "([^"]*)" login page$/ do |user, pass, idpType|
  disable_NOTABLESCAN
  puts "Logging in with credentials \"#{user}\" \"#{pass}\"" if $SLI_DEBUG
  if idpType=="Simple"
    puts "Logging in with credentials \"#{user}\" \"#{pass}\""
    @driver.find_element(:id, "user_id").send_keys user
    @driver.find_element(:id, "password").send_keys pass
    @driver.find_element(:id, "login_button").click
    if @driver.title=="Sandbox User Impersonation"
      #handle sandbox admin/impersonation chooser page
        @driver.find_element(:id, "adminLink").click
    end
  elsif idpType=="Shibboleth"
    @driver.find_element(:name, "j_username").send_keys user
    @driver.find_element(:name, "j_password").send_keys pass
    @driver.find_element(:css, ".form-button").click
    ignore_security_alert
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end
  sleep 3 # wait 3 seconds before enabling notablescan flag again
  enable_NOTABLESCAN

end

When /^I select "(.*?)" from the dropdown and click go$/ do |arg1|
  attempts  = 0
  maxAttempts = 3
  selectTag = nil
  begin
     selectTag = @driver.find_element(:tag_name, "select")
  rescue
    attempts += 1
    if attempts < maxAttempts
      puts "Attempt #{attempts}. Could not find select tag. Will retry in 5 seconds!"
      sleep 5
      retry
    else
      puts  "Could not find select tag in #{maxAttempts} attempts!"
    end
  end
  select = Selenium::WebDriver::Support::Select.new(selectTag)
  select.select_by(:text, arg1)
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

After do |scenario|
  base = "./cats_with_lasers_#{Time.now.strftime('%H-%M-%S')}" # Maybe a better name, eh?
  url_fn = base + ".txt"
  html_fn = base + ".html"
  screenshot_fn = base + ".png"
  
  begin
    File.delete(url_fn)
    File.delete(html_fn)
    File.delete(screenshot_fn)
  rescue Exception => e
  end
  #puts "Running the After hook for Scenario: #{scenario}"s
  begin
    File.delete(url_fn)
    File.delete(html_fn)
    File.delete(screenshot_fn)
  rescue
  end
  if (scenario.failed? and !@driver.nil?)

    # Save current URL and source code to file
    File.open(url_fn, 'w') {|f| f.write("FAILED at URL:\n" + @driver.current_url + "\n") }

    # Save current URL and source code to file
    File.open(html_fn, 'w') {|f| f.write(@driver.page_source) }
    
    # Save screenshot of file
    @driver.save_screenshot(screenshot_fn)
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

def assertWithWait(msg, timeout = 30, &blk)
  wait = Selenium::WebDriver::Wait.new(:timeout => timeout)
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

