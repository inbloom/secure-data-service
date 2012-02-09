require 'selenium-webdriver'

Given /^I have an open web browser$/ do
  puts "open web browser"
  @driver = Selenium::WebDriver.for :firefox
end

When /^I wait for "([^"]*)" seconds$/ do |secs|
  sleep(Integer(secs))
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

Given /^the server is in "([^"]*)" mode$/ do |serverMode|
  @appPrefix = "dashboard_app_prefix_" + serverMode + "_mode"
end

def localLogin (username, password)
  puts "SLI_DEBUG = " + $SLI_DEBUG.to_s
  puts "localLogin" if $SLI_DEBUG
  if @driver == nil 
    @driver = Selenium::WebDriver.for :firefox
  end
  url = getBaseUrl() + PropLoader.getProps['dashboard_landing_page']
  puts "url = " + url
  # Go to login url and verify status of the page/server is up
  @driver.get url
  sleep 1
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
  
  # assertMissingField("Sivan")

  # Perform login and verify
  assertMissingField("j_username", "name")
  assertMissingField("j_password", "name")
  assertMissingField("submit", "name")
  putTextToField(username, "j_username", "name")
  putTextToField(password, "j_password", "name")
  clickButton("submit", "name")
  # url = baseUrl + "/appselector"
  assert(@driver.current_url.start_with?(url),  "Failed to navigate to "+url)
end

def getBaseUrl()
  return "http://"+PropLoader.getProps['dashboard_server_address']+ 
          PropLoader.getProps[@appPrefix] 
end

def assertMissingField(field, by)
  flag = true
  if (by == "id") and (@driver.find_element(:id, field) != nil)
    flag = false
  elsif (by == "name") and (@driver.find_element(:name, field) != nil)
    flag = false
  end
  assert(!flag, "Page does not contain a field with id:" + field)
end

# Asserts a piece of text exists in the body's page
def assertText(text)
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Text is missing from page: " + text )
end

def putTextToField(text, field, by)
  flag = true
  if (by == "id") and (@driver.find_element(:id, field) != nil)
    @driver.find_element(:id, field).send_keys text
  elsif (by == "name") and (@driver.find_element(:name, field) != nil)
    @driver.find_element(:name, field).send_keys text
  end
end    

def clickButton(button, by)
  if (by == "id")
    @driver.find_element(:id, button).click
  elsif (by == "name")
    @driver.find_element(:name, button).click
  end
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

# TODO: add this paramteres (tableRef, by), also may want to add TR class
def countTableRows()
  tableRows = @driver.find_elements(:css, "tr.listRow")
  puts "# of TR = " +  @driver.find_elements(:css, "tr").length.to_s + ", table rows = " + tableRows.length.to_s
  return tableRows.length
end

def listContains(desiredContent)
  result = false

  desiredContentArray = desiredContent.split(";")
  # Find all student names based on their class attribute
  studentNames = @driver.find_elements(:xpath, "//td[@class='name']")
  puts "num of studs = "+ studentNames.length.to_s
  
  nonFoundItems = desiredContentArray.length
  
  
  desiredContentArray.each do |searchValue|
    
    puts "in 1st loop, searchValue = " + searchValue
    studentNames.each do |student|
      # puts "in 2st loop, student.attribute('innerHTML').to_s = " + student.attribute("innerHTML").to_s.lstrip.rstrip[0..15]
      # puts "student.attribute('innerHTML').to_s.include?(searchValue) = " + student.attribute("innerHTML").to_s.include?(searchValue).to_s
      
      if student.attribute("innerHTML").to_s.lstrip.rstrip.include?(searchValue)
        nonFoundItems -= 1
        puts "Found desired item '" + searchValue + "', " + nonFoundItems.to_s + " more items to find"
        # Stop searching for this searchValue and move to the next one
        break
      end
    end
  end
  return nonFoundItems == 0
end

def tableHeaderContains(desiredContent)
  result = false

  desiredContentArray = desiredContent.split(";")
  # Find all student names based on their class attribute
  headerNames = @driver.find_elements(:tag_name, "th")
  puts "num of studs = "+ headerNames.length.to_s
  
  nonFoundItems = desiredContentArray.length
  
  
  desiredContentArray.each do |searchValue|
    
    puts "in 1st loop, searchValue = " + searchValue
    headerNames.each do |header|
      # puts "in 2st loop, student.attribute('innerHTML').to_s = " + student.attribute("innerHTML").to_s.lstrip.rstrip[0..15]
      # puts "student.attribute('innerHTML').to_s.include?(searchValue) = " + student.attribute("innerHTML").to_s.include?(searchValue).to_s
      
      if header.attribute("innerHTML").to_s.lstrip.rstrip.include?(searchValue)
        nonFoundItems -= 1
        puts "Found desired item '" + searchValue + "', " + nonFoundItems.to_s + " more items to find"
        # Stop searching for this searchValue and move to the next one
        break
      end
    end
  end
  return nonFoundItems == 0
end