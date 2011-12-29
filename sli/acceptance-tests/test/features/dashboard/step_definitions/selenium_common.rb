Given /^I have an open web browser$/ do
  puts "open web browser"
  @driver = Selenium::WebDriver.for :firefox
end

After do |scenario|
  @driver.quit if @driver
end

When /^I enter "([^"]*)" in the "([^"]*)" text field$/ do |value, fieldName|
  putTextToField(value,fieldName)
end

When /^I click the Go button$/ do
  clickButton("submit")
end

def localLogin (username, password)
  puts "SLI_DEBUG = " + $SLI_DEBUG.to_s
  puts "localLogin" if $SLI_DEBUG
  if @driver == nil 
    @driver = Selenium::WebDriver.for :firefox
  end
  baseUrl = "http://"+PropLoader.getProps['dashboard_server_address']+ 
          PropLoader.getProps['dashboard_app_prefix'] 
  url = baseUrl + PropLoader.getProps['dashboard_login_page']
  puts "url = " + url
  # Go to login url and verify status of the page/server is up
  @driver.get url
  assert(@driver.current_url == url, "Failed to navigate to "+url)
  
  # assertMissingField("Sivan")

  # Perform login and verify
  assertMissingField("username")
  assertMissingField("password")
  assertMissingField("submit")
  putTextToField(username, "username")
  putTextToField(password, "password")
  clickButton("submit")
  url = baseUrl + "/appselector"
  assert(@driver.current_url.start_with?(url),  "Failed to navigate to "+url)
end

def assertMissingField(fieldId)
  assert(@driver.find_element(:id, fieldId) != nil, "Page does not contain a field with id:" + fieldId)
end

def putTextToField(text, fieldId)
  @driver.find_element(:id, fieldId).send_keys text
end

def clickButton(buttonId)
  @driver.find_element(:id, buttonId).click
end

def selectOption(selectFieldId, optionToSelect)
  select = @driver.find_element(:id, selectFieldId)
  all_options = select.find_elements(:tag_name, "option")
  optionFound = false
  all_options.each do |option|
    if option.attribute("text") == optionToSelect
      optionFound = true
      option.click
    end
  end  
  assert(optionFound, "Desired option '" + optionToSelect + "' was not found in '" + @dropDownId + "' list")
end
