require 'selenium-webdriver'
require_relative '../../utils/sli_utils.rb'


$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']


When /^I navigate to the Dashboard Live home page$/ do
  url = "https://"+ PropLoader.getProps['dashboard_server_address'] + PropLoader.getProps['dashboard_app_prefix_live_mode']
  print url
  @driver.get url
end


When /^I select "([^"]*)" and click go$/ do |arg1|
  sleep(1)
  realm_select = @driver.find_element(:name=> "realmId")
  
  options = realm_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1 == arg1)
      e1.select()
      break
    end
  end
  clickButton("go", "id")
  
end

When /^I login as "([^"]*)" "([^"]*)"/ do | arg1, arg2 |
    sleep(1)
    @driver.find_element(:id, "IDToken1").send_keys arg1
    @driver.find_element(:id, "IDToken2").send_keys arg2
    @driver.find_element(:name, "Login.Submit").click
end

Then /^I should be redirected to the app selection page$/ do
  expected_url = "https://" + PropLoader.getProps['dashboard_server_address'] + PropLoader.getProps['dashboard_app_prefix_live_mode'] + PropLoader.getProps['dashboard_app_selector_page']
  print expected_url
  assert(@driver.current_url == expected_url)
end


When /^I click on the Dashboard page$/ do
  @driver.find_element(:link_text=> "Dashboard").click
  sleep(2)
end

Then /^The students who have an ELL lozenge exist in the API$/ do
  @res = RestClient.get("https://devapp1.slidev.org/sp/identity/authenticate?username=mario.sanchez&password=mario.sanchez1234"){|response, request, result| response }
  @sessionId = @res.body[@res.body.rindex('=')+1..-2]
  
  students_w_lozenges = getStudentsWithELLLozenge()
  students_w_lozenges.each do |student_id|
    restHttpGet("/students/"+student_id)
    @result = JSON.parse(@res.body)
    assert(@result["limitedEnglishProficiency"].to_s == "Yes")
  end  
end

def getStudentsWithELLLozenge()
  studentTable = @driver.find_element(:id, "studentList");
  student_cells = studentTable.find_elements(:xpath, "//td[@class='name']")
  
  students_with_lozenges = []
  i = 0
  student_cells.each do |student_cell|
    
    all_lozengeSpans = student_cell.find_elements(:tag_name, "span")
    
    
    all_lozengeSpans.each do |lozengeSpan|
      if lozengeSpan.attribute("innerHTML").to_s.include?("ELL")
        lid = lozengeSpan.attribute("id")
        y = lid.split(".")
        students_with_lozenges[i] = y[1]
        i += 1
      end
    end
    

  end  
  return students_with_lozenges    
end

