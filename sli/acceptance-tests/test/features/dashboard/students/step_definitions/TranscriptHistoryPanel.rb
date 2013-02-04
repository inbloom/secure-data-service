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


require_relative '../../../utils/sli_utils.rb'
require_relative '../../dash/step_definitions/selenium_common_dash.rb'
require_relative '../../dash/step_definitions/profile.rb'
require_relative '../../dash/step_definitions/student_profile_steps.rb'
require_relative '../../dash/step_definitions/population_widget_steps.rb'
require_relative '../../dash/step_definitions/pages_panel_steps.rb'
require_relative '../../dash/step_definitions/contact_info.rb'

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I look at the "([^\"]*)" panel under "([^\"]*)"$/ do |panelName, tabName|
  tabIndex = getTabIndexAndClick(tabName)
  transcriptHistTab = @driver.find_element(:id, tabIndex)
  checkPanelNameExists(transcriptHistTab, panelName)
  @panel = getPanelFromTabIndex(panelName, tabIndex)
end

When /^I click the expand button of the row "([^\"]*)"$/ do |rowData|
  rowIndex = getIndexFromRowData(@rows, rowData)
  expandBtn = @rows[rowIndex.to_i].find_element(:tag_name, "a")
  
  # Scroll the browser to the button's Y position, Chrome needs this or else it can't click the element
  yLocation = expandBtn.location.y.to_s
  @driver.execute_script("window.scrollTo(0, #{yLocation});")
  
  expandBtn.click
  @lastExpandedRow = rowIndex.to_i - 1
  @subGrids = @tranHistTable.find_elements(:class, "ui-subgrid")
end

When /^I click the "([^"]*)" header to sort$/ do |subjectHeader|
  subHeaderLabels = @subGrids[@lastExpandedRow].find_element(:class, "ui-jqgrid-labels")
  subject = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'subject')]")
  subject.click
end

When /^I click the Expand All link$/ do
  link = @panel.find_element(:xpath, "div[@class='panel-content']/a[text()='Expand All']")
  link.click
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should find (\d+) rows of transcript history$/ do |count|
  @tranHistTable = @panel.find_element(:class, "ui-jqgrid-bdiv")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert(@rows.size - 1 == convert(count), "Expected #{count}, received #{@rows.size-1}")
end

Then /^I should see the table headers "([^"]*)"$/ do |headers|
  expectedRow = headers.split(";")
  headerLabels = @panel.find_element(:class, "ui-jqgrid-labels")
  year = headerLabels.find_element(:xpath, "th/div[contains(@id,'schoolYear')]").text
  term = headerLabels.find_element(:xpath, "th/div[contains(@id,'term')]").text
  school = headerLabels.find_elements(:xpath, "th/div[contains(@id,'school')]")[1].text # index 0 is schoolYear...
  gradeLevel = headerLabels.find_element(:xpath, "th/div[contains(@id,'gradeLevelCode')]").text
  cumulativeGPA = headerLabels.find_element(:xpath, "th/div[contains(@id,'cumulativeGradePointAverage')]").text
  actualRow = [year, term, school, gradeLevel, cumulativeGPA]
  assert(expectedRow == actualRow, "Values do not match")
end

Then /^I should see the sub table headers "([^"]*)"$/ do |headers|
  expectedRow = headers.split(";")
  @subGrid = nil
  @subGrids.each do |sg|
    td = sg.find_element(:class, "tablediv")
    if (td.attribute("id").include? "_#{@lastExpandedRow+1}")
      if (td.find_element(:xpath, "div[contains(@id, '_#{@lastExpandedRow+1}_t')]") != nil)
        @subGrid = sg
        break
      end
    end
  end
  subHeaderLabels = @subGrid.find_element(:class, "ui-jqgrid-labels")
  subject = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'subject')]").text
  course = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'course')]").text
  grade = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'grade')]").text
  actualRow = [subject, course, grade]
  assert(expectedRow == actualRow, "Values do not match")
end

Then /^I should see the row "([^\"]*)"$/ do |rowData|
  getIndexFromRowData(@rows, rowData)
end

Then /^I should find (\d+) expanded rows$/ do |count|
  @subGrids = @tranHistTable.find_elements(:class, "ui-subgrid")
  assert(@subGrids.size == convert(count), "Expected #{count}, received #{@subGrids.size}")
end

Then /^I should find (\d+) sub rows$/ do |count|
  subTable = @subGrid.find_element(:class, "ui-jqgrid-bdiv")
  @subRows = subTable.find_elements(:tag_name, "tr")
  assert(@subRows.size - 1 == convert(count), "Expected #{count}, received #{@subRows.size-1}")
end

Then /^I should see "([^\"]*)" for sub row (\d+)$/ do |rowData, rowIndex|
  expectedRow = rowData.split(";")
  rowIndex = rowIndex.to_i
  subject = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'subject')]").text
  course = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'course')]").text
  grade = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'grade')]").text
  actualRow = [subject, course, grade]
  assert(expectedRow == actualRow, "Values do not match")
end

Then /^I should see the sub row "([^\"]*)"$/ do |rowData|
  getIndexFromSubRowData(@subRows, rowData)
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

def getTabIndexAndClick(tabName)
  allTabs = @driver.find_element(:id, "tabs")
  links = allTabs.find_elements(:tag_name, "a")
  found = nil
  tabIndex = nil
  links.each do |tab|
    if (tab.text.include? tabName)
      found = tab
      url = tab["href"]
      i = url.index('#page') +1
      tabIndex = url[i, url.length-i]
      tab.click
      return tabIndex
    end
  end
  assert(found != nil, "Tab was not found")
end

def getPanelFromTabIndex(panelName, tabIndex)
  tab = @driver.find_element(:id, tabIndex)
  panelsInTab = tab.find_elements(:class, "panel")

  panelsInTab.each do |panel|
    panelHeader = panel.find_element(:class, "panel-header")
    headerName = panelHeader.text
    if (headerName == panelName)
      return panel
    end
  end
  assert(false, "Panel name: " + panelName + " is not found in tab")
end

def getIndexFromRowData(rowsToCheck, rowData)
  found = false
  expectedRow = rowData.split(";")
  for rowIndex in 1..rowsToCheck.size - 1
    schoolYear = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'schoolYear')]").text
    term = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'term')]").text
    school = rowsToCheck[rowIndex].find_elements(:xpath, "td[contains(@aria-describedby,'school')]")[1].text # index 0 is schoolYear...
    gradeLevel = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'gradeLevelCode')]").text
    cumulativeGPA = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'cumulativeGradePointAverage')]").text
    actualRow = [schoolYear, term, school, gradeLevel, cumulativeGPA]
    if (expectedRow == actualRow)
      found = true
      return rowIndex
    end
  end
  assert(found, "Cannot find the specified row")
end

def getIndexFromSubRowData(rowsToCheck, rowData)
  found = false
  expectedRow = rowData.split(";")
  for rowIndex in 1..rowsToCheck.size - 1
    subject = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'subject')]").text
    course = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'course')]").text
    grade = rowsToCheck[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'grade')]").text
    actualRow = [subject, course, grade]
    if (expectedRow == actualRow)
      found = true
      return rowIndex
    end
  end
  assert(found, "Cannot find the specified row")
end
