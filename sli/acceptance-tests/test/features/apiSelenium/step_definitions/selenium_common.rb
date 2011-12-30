Given /^I have an open web browser$/ do
  @driver = Selenium::WebDriver.for :firefox
end

After do |scenario|
  @driver.quit if @driver
end