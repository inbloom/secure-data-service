require 'selenium-webdriver'

And /^there are "([^"]*)" Tabs$/ do |tabCount|
  allTabs = @driver.find_element(:id, "tabs")
  tab = allTabs.find_elements(:tag_name, "li")
  assert(tabCount.to_i == tab.length)
end

Given /^in Tab "([^"]*)", there is "([^"]*)" Panels$/ do |tabIndex, panelCount|
  element = @driver.find_element(:id, "page-tab" + tabIndex)
  tabs = element.find_elements(:class, "panel")
  assert(tabs.length == panelCount.to_i)
end

Given /^Tab "([^"]*)" is titled "([^"]*)"$/ do |tabIndex, tabTitle|
  allTabs = @driver.find_element(:id, "tabs")
  tab = allTabs.find_elements(:tag_name, "li")
  title = tab[tabIndex.to_i-1].find_element(:tag_name, "a")
  assert(title.text == tabTitle)
end
