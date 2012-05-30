def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\nCurrent Time: "+Time.now.getutc.to_s+"\n\n"+message
end

Given /^I have an open web browser$/ do
  @profile ||= Selenium::WebDriver::Firefox::Profile.new
  @profile['network.http.prompt-temp-redirect'] = false
  @driver ||= Selenium::WebDriver.for :firefox, :profile => @profile
  @driver.manage.timeouts.implicit_wait = 10 # seconds
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

When /^I submit the credentials "([^"]*)" "([^"]*)" for the "([^"]*)" login page$/ do |user, pass, idpType|
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
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end

end

After do |scenario| 
  #puts "Running the After hook for Scenario: #{scenario}"s
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
