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


require 'selenium-webdriver'

And /^there are "([^"]*)" Tabs$/ do |tabCount|
  allTabs = @driver.find_element(:id, "tabs").find_element(:tag_name, "ul")
  tab = allTabs.find_elements(:tag_name, "li")
  tabsToIgnore = 0
  # dashboard builder has the "+" tab that we need to ignore
  if (@driver.current_url.include? "builder")
    tabsToIgnore = 1
  end
  assert(tabCount.to_i == tab.length - tabsToIgnore, "Actual Tab Count: " + tab.length.to_s)
end

Given /^Tab "([^"]*)" is titled "([^"]*)"$/ do |tabIndex, tabTitle|
  allTabs = @driver.find_element(:id, "tabs").find_element(:tag_name, "ul")
  tab = allTabs.find_elements(:tag_name, "li")
  title = tab[tabIndex.to_i-1].find_element(:tag_name, "a")
  assert(title.text == tabTitle, "Actual Title: " + title.text)
end

When /^in Tab ID "([^"]*)", there is "([^"]*)" Panels$/ do |tabId, panelCount|
  element = @driver.find_element(:id, tabId)
  tabs = element.find_elements(:class, "panel")
  assert(tabs.length == panelCount.to_i, "Actual # of Panels: " + tabs.length.to_s)
end

Then /^in "([^"]*)" tab, there are "([^"]*)" Panels$/ do |tabName, panelCount|
  tab = getTab(tabName)
  actualPanels = tab.find_elements(:class, "panel")
  assert(actualPanels.length == panelCount.to_i, "Actual # of Panels: " + actualPanels.length.to_s)
end

When /^Tab has a title named "([^"]*)"$/ do |tabTitle|
  getTabIndex(tabTitle)
end

When /^I click on "([^"]*)" Tab$/ do |tabName|
  tab = getTabIndex(tabName)
  searchPattern = "a[href*='" + tab + "']"
  @driver.find_element(:css, searchPattern).click()
  @currentTab = getTab(tabName)
end

When /^I see the following tab order "(.*?)"$/ do |pages|
  checkPageOrder(pages)
end

def getTab(tabName)
  tabIndex = getTabIndex(tabName)
  return @driver.find_element(:id, tabIndex)
end

# Given a tabname, find the tab ID index to know which tab to read from
def getTabIndex(tabName)
  allTabs = @driver.find_element(:id, "tabs").find_element(:tag_name, "ul")
  links = allTabs.find_elements(:tag_name, "a")
  found = nil
  tabIndex = nil
  links.each do |tab|
    if (tab.text.include? tabName)
      found = tab
      url = tab["href"]
      i = url.index('#page') +1
      tabIndex = url[i, url.length-i]
      return tabIndex
    end
  end
  assert(found != nil, "Tab was not found")
end

# checks whether panel name exists in a particular tab
def checkPanelNameExists(tab, panelName)
  found = false;
  if tab.attribute("innerHTML").to_s.lstrip.rstrip.include?(panelName)
    found = true;
  end
  assert(found == true, "Panel Name is not found: " + panelName)
end

#Given a tabName and panelName, return the panel
#Note: exact panelName is not required
# if panelName is nil, return the first panel
def getPanel(tabName, panelName = nil)
  tab = getTab(tabName)
  panelsInTab = tab.find_elements(:class, "panel")
  if (panelName != nil)
    checkPanelNameExists(tab, panelName)
  else
    return panelsInTab[0]
  end
  
  panelsInTab.each do |panel|
    panelHeader = panel.find_element(:class, "panel-header")
    if (panelHeader.text.include? panelName)
      return panel
    end
  end
  assert(false, "Panel name: " + panelName + " is not found in tab: " + tabName)
end

def checkPageOrder(expectedPages)
  expected = expectedPages.split(';')  
  actual = @driver.find_element(:id, "tabs").find_element(:tag_name, "ul").find_elements(:tag_name, "li") 
  assert(expected.length == actual.length, "size of pages are not equal Actual: " + actual.length.to_s + " Expected: " + expected.length.to_s )
  for index in 0..actual.length - 1  
    page = actual[index]
    pageText = page.find_element(:tag_name,"a").text
    assert((pageText.include? expected[index]), "Order is incorrect. Expected #{expected[index]} Actual #{pageText}")
  end
end
