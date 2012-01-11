def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\n\n"+message
end

Given /^I have an open web browser$/ do
  @driver = Selenium::WebDriver.for :firefox
end

After do |scenario| 
  #puts "Running the After hook for Scenario: #{scenario}"
  @driver.quit if @driver
end