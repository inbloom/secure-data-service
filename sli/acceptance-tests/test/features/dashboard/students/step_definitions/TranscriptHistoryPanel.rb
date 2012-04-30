require_relative '../../../utils/sli_utils.rb'
require_relative '../../dash/step_definitions/selenium_common_dash.rb'
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

When /^I click the expand button of row (\d+)$/ do |rowIndex|
  expandBtn = @rows[rowIndex.to_i].find_element(:tag_name, "a")
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
  assert(@rows.size - 1 == convert(count), "Expected #{count}, received #{@rows.size}")
end

Then /^I should see the table headers "([^"]*)"$/ do |headers|
  headerLabels = @panel.find_element(:class, "ui-jqgrid-labels")
  year = headerLabels.find_element(:xpath, "th/div[contains(@id,'schoolYear')]").text
  term = headerLabels.find_element(:xpath, "th/div[contains(@id,'term')]").text
  school = headerLabels.find_elements(:xpath, "th/div[contains(@id,'school')]")[1].text # index 0 is schoolYear...
  gradeLevel = headerLabels.find_element(:xpath, "th/div[contains(@id,'gradeLevel')]").text
  cumulativeGPA = headerLabels.find_element(:xpath, "th/div[contains(@id,'cumulativeGPA')]").text
  expectedRow = headers.split(";")
  actualRow = [year, term, school, gradeLevel, cumulativeGPA]
  for i in 0..actualRow.size-1
    assert(expectedRow[i] == actualRow[i], "Values do not match")
  end
end

Then /^I should see the sub table headers "([^"]*)"$/ do |headers|
  subHeaderLabels = @subGrids[@lastExpandedRow].find_element(:class, "ui-jqgrid-labels")
  subject = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'subject')]").text
  course = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'course')]").text
  grade = subHeaderLabels.find_element(:xpath, "th/div[contains(@id,'grade')]").text
  expectedRow = headers.split(";")
  actualRow = [subject, course, grade]
  for i in 0..actualRow.size-1
    assert(expectedRow[i] == actualRow[i], "Values do not match")
  end
end

Then /^I should see "([^\"]*)" for row (\d+)$/ do |rowData, rowIndex|
  rowIndex = rowIndex.to_i
  schoolYear = @rows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'schoolYear')]").text
  term = @rows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'term')]").text
  school = @rows[rowIndex].find_elements(:xpath, "td[contains(@aria-describedby,'school')]")[1].text # index 0 is schoolYear...
  gradeLevel = @rows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'gradeLevel')]").text
  cumulativeGPA = @rows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'cumulativeGPA')]").text
  expectedRow = rowData.split(";")
  actualRow = [schoolYear, term, school, gradeLevel, cumulativeGPA]
  for i in 0..actualRow.size-1
    assert(expectedRow[i] == actualRow[i], "Values do not match")
  end
end

Then /^I should find (\d+) expanded rows$/ do |count|
  @subGrids = @tranHistTable.find_elements(:class, "ui-subgrid")
  assert(@subGrids.size == convert(count), "Expected #{count}, received #{@subGrids.size}")
end

Then /^I should find (\d+) sub rows$/ do |count|
  subTable = @subGrids[@lastExpandedRow].find_element(:class, "ui-jqgrid-bdiv")
  @subRows = subTable.find_elements(:tag_name, "tr")
  assert(@subRows.size - 1 == convert(count), "Expected #{count}, received #{@subRows.size}")
end

Then /^I should see "([^\"]*)" for sub row (\d+)$/ do |rowData, rowIndex|
  rowIndex = rowIndex.to_i
  subject = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'subject')]").text
  course = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'course')]").text
  grade = @subRows[rowIndex].find_element(:xpath, "td[contains(@aria-describedby,'grade')]").text
  expectedRow = rowData.split(";")
  actualRow = [subject, course, grade]
  for i in 0..actualRow.size-1
    assert(expectedRow[i] == actualRow[i], "Values do not match")
  end
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