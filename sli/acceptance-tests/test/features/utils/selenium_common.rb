def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\nCurrent Time: "+Time.now.getutc.to_s+"\n\n"+message
end

Given /^I have an open web browser$/ do
  profile = Selenium::WebDriver::Firefox::Profile.new
  profile['network.http.prompt-temp-redirect'] = false
  @driver = Selenium::WebDriver.for :firefox, :profile => profile
  @driver.manage.timeouts.implicit_wait = 2 # seconds
end

When /^I wait for a second$/ do
  sleep(1)
end

When /^I wait for "([^"]*)" seconds$/ do |secs|
  sleep(Integer(secs))
end

After do |scenario| 
  #puts "Running the After hook for Scenario: #{scenario}"
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
