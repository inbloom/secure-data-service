=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end
When /^I navigate to the Dashboard Builder page$/ do
  url = getBaseUrl() + "/builder/index.html"
  @driver.get url
end

When /^I click on "(.*?)" Profile Builder$/ do |profileName|
  @currentProfile = profileName.downcase
  name = "SLC - " + profileName + " Profile"
  profile_list = @explicitWait.until{@driver.find_element(:class, "profile_list").find_element(:link_text, name)}
  profile_list.click
  
  @explicitWait.until{@driver.find_element(:class,"profilePageWrapper").text.downcase.include? profileName.downcase}
end

When /^I delete Page "(.*?)"$/ do |pageName|
  hoverOverPage(pageName, "delete")
end

When /^I add a Page named "(.*?)"$/ do |pageName|
  addSection = @driver.find_element(:class, "addPageSection")
  addSection.find_element(:tag_name, "button").click
  
  @currentPage = @driver.find_element(:css, "[class*='tab-content']").find_element(:css, "div[title='New page']")
  
  setPageName(pageName) 
end

When /^I add an available panel named "(.*?)"$/ do |panelName|
  @currentPage.find_element(:css, "button[class*='btn-block']").click
  popupPanel = @driver.find_element(:id, "allPanelsModal")
  availablePanels = popupPanel.find_element(:id,"panelSelectable").find_elements(:tag_name,"li")
  found = false
  availablePanels.each do |panel|
    name = panel.find_element(:css, "span[class*='panelName']")
    if (name.attribute("innerHTML").include? panelName)
      found = true
      panel.click
      break
    end
  end
  assert(found, "#{panelName} is not found in the list")
  popupPanel.find_element(:class,"modal-footer").find_elements(:tag_name, "button")[1].click
  
  ensurePopupUnloaded()
end

When /^I upload custom json for it$/ do
  viewSourceCode()
  uploadJson()
end

When /^in "(.*?)" Page, it has the following panels: "(.*?)"$/ do |pageName, listOfPanels|
  hoverOverPage(pageName)
  expectedPanels = listOfPanels.split(';')
  
  @currentPage = @driver.find_element(:css, "[class*='tab-content']").find_element(:css, "div[title='#{pageName}']")
  actualPanels = @currentPage.find_element(:class,"unstyled").find_elements(:tag_name,"li")
  assert(actualPanels.length == expectedPanels.length, "Expected: #{expectedPanels.length.to_s} Actual: #{actualPanels.length.to_s}")
  
  expectedPanels.each do |expectedPanel|
    found = false
    actualPanels.each do |actualPanel|
      if (actualPanel.text == expectedPanel)
        found = true  
      end  
    end  
    assert(found, "#{expectedPanel} was not found")
  end
end

When /^I move Page "(.*?)" to become Page Number "(.*?)"$/ do |pageName, pageIndex|
  source = getPageByName(pageName)
  assert(source != nil, "Page #{pageName} is not found")
  
  target = getPageByIndex(pageIndex.to_i)
  assert(target != nil, "Page not found")
  
  src_x = source.location.x
  tar_x = target.location.x 

  @driver.action.move_to(source).perform
  #ensure that it is hovering on it
  source.find_element(:class, "updatePage")
  
  edit = source.find_elements(:tag_name, "span")
  assert(edit.length == 3)

  @driver.action.click_and_hold(edit[0]).perform
  @driver.action.move_by(tar_x-src_x, 0).release.perform
  @driver.action.move_by(0,100).move_by(0,-100).perform 
end

When /^I see the following page order "(.*?)" in the builder$/ do |pages|
  expected = pages.split(';')  
  actual = @driver.find_element(:css, "[class*='tabbable']").find_element(:tag_name, "ul").find_elements(:tag_name, "li") 
  assert(expected.length == actual.length - 1 , "size of pages are not equal Actual: " + (actual.length - 1).to_s + " Expected: " + expected.length.to_s )
  for index in 0..actual.length - 2 #ignore the las page  
    page = actual[index]
    pageText = page.find_element(:tag_name,"a").text
    assert((pageText.include? expected[index]), "Order is incorrect. Expected #{expected[index]} Actual #{pageText}")
  end

end

def getPageByName(pageName)
  pages = @driver.find_element(:css, "[class*='tabbable']").find_elements(:tag_name, "li")
  pages.each do |page|
    if (page.text == pageName)
     return page
    end
  end
  return nil
end

def getPageByIndex(index)
  pages = @driver.find_element(:id, "tabs").find_elements(:tag_name, "li")
  assert(index < pages.length && index >= 0, "Invalid index")
  return pages[index]
end

def setPageName(pageName)
  input = @currentPage.find_element(:tag_name, "input")  
  input.clear
  input.send_keys pageName
  @currentPage.find_element(:class,"show-true").find_element(:tag_name, "button").click
end

def hoverOverPage(pageName, mode = nil)
  page = getPageByName(pageName)
  assert(page != nil, "Page #{pageName} is not found")
  
  @driver.action.move_to(page).perform
  page.find_element(:tag_name,"a").click

  if (mode == "edit")
    #TODO
  elsif (mode == "delete")
    @driver.find_element(:css, "[class*='tab-content']").find_element(:css,"[class*='active']").find_element(:css, "[ng-click='removePage()']").click
  end  
end

def viewSourceCode()
  @currentPage.find_element(:class,"page-actions").find_element(:tag_name,"button").click
  ensurePopupLoaded()  
end

def uploadJson()
  uploadText = "[{\"id\":\"sectionList\",\"parentId\":\"sectionList\",\"name\":null,\"type\":\"TREE\"}]"
  if (@currentProfile == "section")
    uploadText = "[{\"id\":\"listOfStudents\",\"parentId\":\"listOfStudents\",\"name\":null,\"type\":\"PANEL\"}]"
  end
  inputBox = @driver.find_element(:id, "content_json")
  inputBox.clear
  inputBox.send_keys(uploadText)
    
  saveDashboardBuilder()
end

def saveDashboardBuilder()
  save = @driver.find_element(:id, "modalBox").find_element(:class, "modal-footer").find_elements(:tag_name, "button")[1]
  # Scroll the browser to the button's co-ords
  yLocation = save.location.y.to_s
  xLocation = save.location.x.to_s
  @driver.execute_script("window.scrollTo(#{xLocation}, #{yLocation});")
  
  save.click 
  ensurePopupUnloaded()
end

def ensurePopupLoaded()
  @explicitWait.until {(style = @driver.find_element(:id, "modalBox").attribute('style').strip)  == "display: block;" }
end

def ensurePopupUnloaded() 
   @driver.manage.timeouts.implicit_wait = 2
   @explicitWait.until{(@driver.find_elements(:id, "simplemodal-overlay").length) == 0}
   @driver.manage.timeouts.implicit_wait = 10
end