=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
  clickOnBuilderMenu(0, profileName)
end

When /^I delete Page "(.*?)"$/ do |pageName|
  hoverOverPage(pageName, "delete")
end

#Add a new page and rename it to 'pageName'
When /^I add a Page named "(.*?)"$/ do |pageName|
  addSection = @driver.find_element(:class, "addPageSection")
  addSection.find_element(:tag_name, "button").click
  @currentPage = @driver.find_element(:css, "[class*='tab-content']").find_element(:css, "div[title='New page']")
  setPageName(pageName) 
end

When /^I delete an available panel named "(.*?)"$/ do |panelName|
  hoverOverPanel(panelName, "delete")
end

When /^I add an available panel named "(.*?)"$/ do |panelName|
  # Click on the 'Add available panels' button
  @currentPage.find_element(:css, "button[class*='btn-block']").click
  # Identify the pop up panel for 'Add a Panel'
  @explicitWait.until {(style = @driver.find_element(:id, "allPanelsModal").attribute('style').strip)  == "display: block;" }
  popupPanel = @driver.find_element(:id, "allPanelsModal")
  # Select the panels from the list
  availablePanels = popupPanel.find_element(:id,"panelSelectable").find_elements(:tag_name,"li")
  found = false
  availablePanels.each do |panel|
  if (panel.attribute("innerHTML").include? panelName)
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
      if (actualPanel.attribute("innerHTML").include? expectedPanel)
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

When /^I click on "(.*?)" Panels$/ do |panelName|
  clickOnBuilderMenu(1, panelName)
end

When /^I see the following available panels "(.*?)"$/ do |listOfPanels|
  expectedPanels = listOfPanels.split(';')
  actualPanels = @driver.find_element(:class, "profilePageWrapper").find_elements(:tag_name,"li")
  assert(expectedPanels.count == actualPanels.count, "Expected Panels: #{expectedPanels.count} Actual Panels: #{actualPanels.count}")
  
  expectedPanels.each do |expected|
    found = false
    actualPanels.each do |actual|
      if (actual.attribute('innerHTML').include? expected)
        found = true
      end
    end
    assert(found, "#{expected} Panel was not found")
  end
end

When /^I click on Panels Menu$/ do
  @driver.find_elements(:class, "accordion-heading")[1].find_element(:tag_name,"a").click
end

# Click the 'Publish Layout' or 'Restore' button after making changes in the page
#When /^I click the Publish Layout button$/ do
When /^I click the "(.*?)" button$/ do |buttonName|  
  activeButton = @currentPage.find_element(:class, "form-actions")
  if (buttonName =="Publish Layout")
    activeButton.find_element(:css, "[ng-click='publishPage()']").click
    @explicitWait.until {(style = @driver.find_element(:css, "div[class*='alert-success']").attribute('style').strip)  == "display: block;" }
  elsif (buttonName =="Restore")
    activeButton.find_element(:css, "[ng-click='restore()']").click
    @explicitWait.until {(style = @driver.find_element(:css, "div[class*='restoreMessage']").attribute('style').strip)  == "display: block;" }     
  end
  sleep 3
end

# Publish Layout Modal Window
When /^I click on "(.*?)" button on the modal window$/ do |action| 
   #Identify the pop up panel
   popupPanel = @driver.find_element(:id, "alertModal")
   
   if (action =="Stay")
     popupPanel.find_element(:class, "modal-footer").find_elements(:tag_name, "button")[1].click
     ensurePopupUnloaded()
   elsif (action =="Leave")
     popupPanel.find_element(:class, "modal-footer").find_elements(:tag_name, "button")[0].click
     ensurePopupUnloaded()
   end
   sleep 2
end

# Click on the profile name to navigate away from the current page without clicking the Publish Layout button 
When /^I navigate away to "(.*?)" Profile Builder without clicking the Publish Layout button$/ do |profileName|
  @currentProfile = profileName.downcase
  name = "inBloom - " + profileName + " Profile"
  @driver.find_element(:class, "profile_list").find_element(:link_text, name).click
end

# Validate that the profile builder loads (for publish layout button functionality)
When /^I view the "(.*?)" profile builder$/ do |profileName|
  @currentProfile = profileName.downcase
  name = "inBloom - " + profileName + " Profile"
  if (@driver.find_element(:class,"profilePageWrapper").text.downcase.include? profileName.downcase)
    found = true
  end
  assert(found, "#{profileName} profile page was not loaded")  
end

When /^I "(.*?)" a page named "(.*?)" without clicking the Publish Layout button$/ do |action, pageName|
  if (action == "Add")
    @driver.find_element(:class, "addPageSection").find_element(:tag_name, "button").click  
  #elsif (action == "View")
    #TODO
  end #end if
end

#Add/Edit a new title to the page 
When /^I "(.*?)" the page title as "(.*?)"$/ do |action, pageName|
  if (action == "Add")
    @currentPage = @driver.find_element(:css, "[class*='tab-content']").find_element(:class, "active")
  elsif (action == "Edit")
    @currentPage = @driver.find_element(:css, "[class*='tab-content']").find_element(:class, "active")
    @currentPage.find_element(:css, "[ng-click='editPageTitle();']").click
  end
  input = @currentPage.find_element(:class,"show-true").find_element(:tag_name, "input")  
  input.clear
  input.send_keys pageName
  @currentPage.find_element(:class,"show-true").find_element(:tag_name, "button").click
  sleep 0.30
end

################################################################################################################
################################################################################################################
#Description: Finds the page by name.
def getPageByName(pageName)
  pages = @driver.find_element(:css, "[class*='tabbable']").find_elements(:tag_name, "li")
  pages.each do |page|
    if (page.text == pageName)
     return page
    end
  end
  return nil
end

#Description: Finds the page by index.
def getPageByIndex(index)
  pages = @driver.find_element(:id, "tabs").find_elements(:tag_name, "li")
  assert(index < pages.length && index >= 0, "Invalid index")
  return pages[index]
end

#Description: Sets up the page name.
def setPageName(pageName)
  input = @currentPage.find_element(:tag_name, "input")  
  input.clear
  input.send_keys pageName
  @currentPage.find_element(:class,"show-true").find_element(:tag_name, "button").click
end

#Description: Navigate to the Page.Includes the Edit/Delete operations
def hoverOverPage(pageName, mode = nil)
  page = getPageByName(pageName)
  assert(page != nil, "Page #{pageName} is not found")
  
  @driver.action.move_to(page).perform
  page.find_element(:tag_name,"a").click

  if (mode == "edit")
    #TODO
  elsif (mode == "delete")
    @driver.find_element(:css, "[class*='tab-content']").find_element(:css,"[class*='active']").find_element(:css, "[ng-click='removePage()']").click
    begin
      #Find the Remove Tab? pop up window
      popupPanel = @driver.find_element(:id, "removeTab")
      popupPanel.find_element(:class, "modal-footer").find_elements(:tag_name, "button")[0].click
      ensurePopupUnloaded()
    rescue
    end
    sleep 1
  end  
end

#Description: Finds the panel by name.
def getPanelByName(panelName)
  panels = @driver.find_element(:css, "[class*='tab-content']").find_element(:css, "[class*='active']").find_element(:class,"unstyled").find_elements(:tag_name,"li")
  panels.each do |panel|
    if (panel.text == panelName)
     return panel
    end
  end
  return nil
end

#Description: Navigate to the Panel. Includes the Delete panel operation
def hoverOverPanel(panelName, mode = nil)
  panel = getPanelByName(panelName)
  assert(panel != nil, "Panel #{panelName} is not found")
  @driver.action.move_to(panel).perform
    if (mode == "delete")  
    panel.find_element(:css, "[class*='span1']").find_element(:tag_name, "a").click
    begin
      @driver.switch_to.alert.accept
    rescue
    end
  end  
end

#Description: Click on the 'Show Page Source Code' link
def viewSourceCode()
  @currentPage.find_element(:link_text, "+ Show Page Source Code").click
  ensurePopupLoaded()  
end

#Description: Copy the JSON on the source code window
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

#Description: Save on pop up 
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

#Description: Click on the profile builder
def clickOnBuilderMenu(index, itemName)
  @currentProfile = itemName.downcase
  name = "inBloom - " + itemName + " Profile"
  menuItem = @explicitWait.until{@driver.find_elements(:class, "profile_list")[index].find_element(:link_text, name)}
  menuItem.click
  
  @explicitWait.until{@driver.find_element(:class,"profilePageWrapper").text.downcase.include? itemName.downcase}
end
