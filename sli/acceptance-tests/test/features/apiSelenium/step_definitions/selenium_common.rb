Given /^I have an open web browser$/ do
  @driver = Selenium::WebDriver.for :firefox
end

After do |scenario| 
  puts "Running the After hook"
  @driver.quit if @driver
end