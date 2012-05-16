require 'selenium-webdriver'
require_relative '../../../utils/sli_utils.rb'


$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']


# TODO: externalize this to a method so we can reuse in the future
When /^I select "([^"]*)" and click go$/ do |arg1|
 realm_select = @explicitWait.until{@driver.find_element(:name=> "realmId")}
  
  options = realm_select.find_elements(:tag_name=>"option")
  found = false
  options.each do |e1|
    if (e1.text == arg1)
      found = true
      e1.click()
      break
    end
  end
  assert(found, "The exact realm cannot be found")
  clickButton("go", "id")
  
end

When /^I login as "([^"]*)" "([^"]*)"/ do | username, password |
    @explicitWait.until{@driver.find_element(:id, "IDToken1")}.send_keys username
    @driver.find_element(:id, "IDToken2").send_keys password
#    @driver.find_element(:name, "Login.Submit").click
    @driver.find_element(:id, "IDToken2").send_keys(:enter)
    # Catches the encryption pop up seen in local box set up
    begin
      @driver.switch_to.alert.accept
    rescue
    end
end

Then /^I should be redirected to the app selection page$/ do
  expected_url = getBaseUrl() + PropLoader.getProps['dashboard_app_selector_page']
  puts "Expected URL = " + expected_url + ", Current URL = " + @driver.current_url 
  assert(@driver.current_url == expected_url)
end


When /^I click on the Dashboard page$/ do
   @explicitWait.until{@driver.find_element(:link_text=> "Dashboard")}.click
  sleep(2)
end

Then /^The students who have an ELL lozenge exist in the API$/ do
  #todo: grab a token id from api 
  @sessionId = "4cf7a5d4-37a1-ca19-8b13-b5f95131ac85"
  
  students_w_lozenges = getStudentsWithELLLozenge()
  students_w_lozenges.each do |student_id|
    urlHeader = makeUrlAndHeaders('get' ,"/v1/students/"+student_id, @sessionId, @format)

    @res = RestClient.get(urlHeader[:url], urlHeader[:headers]){|response, request, result| response }
    @result = JSON.parse(@res.body)
    assert(@result["limitedEnglishProficiency"].to_s == "Limited")
  end  
end

def getStudentsWithELLLozenge()
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_trs = studentTable.find_elements(:xpath,".//tr[contains(@class,'ui-widget')]")
  students_with_lozenges = []
  i = 0
  all_trs.each do |tr|
   fullName = tr.find_element(:xpath, "td[contains(@aria-describedby,'name.fullName')]")
   programParticipation = tr.find_element(:xpath, "td[contains(@aria-describedby,'programParticipation') and title='ELL']")
   if (programParticipation.length > 0)
    students_with_lozenges[i] = fullName
    i+=1
   end
  end  
  return students_with_lozenges    
end

When /^the following students have "([^"]*)" lozenges: "([^"]*)"$/ do |lozengeName, studentList|
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}

  i = 0
  studentList.split(";").each do |studentName|
     studentCell = getStudentCell(studentName)
     programParticipations = getStudentProgramParticipation(studentCell)
     found = false
     programParticipations.each do |pp|
       if (pp.text == lozengeName)
        found = true  
       end
     end
     assert(found, studentName.to_s + " doesn't have " + lozengeName.to_s)
  end
end