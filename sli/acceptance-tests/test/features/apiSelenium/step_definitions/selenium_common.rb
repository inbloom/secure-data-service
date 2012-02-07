def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\n\n"+message
end

Given /^I have an open web browser$/ do
  @driver = Selenium::WebDriver.for :firefox
  @driver.manage.timeouts.implicit_wait = 2 # seconds
end

When /^I wait for a second&/ do
  sleep(1)
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

def assertWithWait(msg, &blk)
  wait = Selenium::WebDriver::Wait.new(:timeout => 2)
  begin
    wait.until {yield}
  rescue
  end
  assert(yield, webdriverDebugMessage(@driver,msg))
end
