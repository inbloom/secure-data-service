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


#attributeToCompare is the attribute inside the td that we can use as the value to sort by (ex. in perf level, "title" holds the score)
Then /^I click on "([^"]*)" header to sort an integer column in "([^"]*)" order based on "([^"]*)"$/ do |columnName, order, attributeToCompare|
  sortColumn(columnName, ColumnType.getType("integer"), isAscendingOrder(order), attributeToCompare)
end

Then /^I click on "([^"]*)" header to sort a "([^"]*)" column in "([^"]*)" order$/ do |columnName, columnType, order|
   sortColumn(columnName, ColumnType.getType(columnType), isAscendingOrder(order))
end

Then /^I check "([^"]*)" column is sorted as "([^"]*)" column$/ do |columnName, columnType|
   originalValues = getColumnValues(columnName, columnType)
   checkColumnSorted(columnName, ColumnType.getType(columnType), originalValues.sort)
end

# returns all trs of a grid in a particular panel
# excludes the header of the grid
def getGrid(panel)
  getNthGrid(panel, 1)
end

def getNthGrid(panel, n)
  n = n.to_i-1
  grids = @explicitWait.until{panel.find_elements(:class,"ui-jqgrid-bdiv")}
  assert(n < grids.length && n >=0 , "n must be less than #{grids.length}")
  all_trs = grids[n].find_elements(:css, "tr[class*='ui-widget-content']")
  return all_trs
end

def getTdBasedOnAttribute(tr,attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[aria-describedby*='" + attribute + "']"
  td = tr.find_element(:css, searchText)
  return td
end

def getTdsBasedOnAttribute(tr,attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[aria-describedby*='" + attribute + "']"
  tds = tr.find_elements(:css, searchText)
  return tds
end

def getAttributeByName(tr, attribute, name)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[aria-describedby*='" + attribute + "']"
  td = tr.find_element(:css, searchText)
  return td.attribute(name)
end

def getAttribute(tr, attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[aria-describedby*='" + attribute + "']"
  value = tr.find_element(:css, searchText)
  return value.text
end

def getAttributes(tr, attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[aria-describedby*='" + attribute + "']"
  values = []
  i = 0
  elements = tr.find_elements(:css, searchText)
  elements.each do |element|
    if (element.text.length > 0)
      values[i] = element.text
      i += 1
    end
  end
  return values
end

def getGridHeaders(panel)
  header = @explicitWait.until{panel.find_element(:class,"ui-jqgrid-hbox")}
  return header.find_elements(:tag_name, "th")
end

def getHeaderCellBasedOnId(ths, attribute)
  assert(!ths.nil?, "Row is empty")
  ths.each do |th|
    if (th.attribute("id").include? attribute)
      return th
    end
  end
end

def clickOnRow(expectedItemToClickOn)
  all_trs = getGrid(@currentTab)
  link = nil
  all_trs.each do |tr|
    if tr.attribute("innerHTML").to_s.include?(expectedItemToClickOn)
      link = tr.find_element(:link_text, expectedItemToClickOn)
      break
    end
  end  
  assert(link != nil, "#{expectedItemToClickOn} was not found")
  link.click
end

#Checks against entries in a grid
#use <empty> for empty cells
def checkGridEntries(panel, table, mapping, isExactRowsMatch = true, gridNumber = 1)
  table.headers.each do |current|
    if (mapping[current] == nil)
      puts "Warning: No mapping found for header: " + current
      mapping[current] = current      
    end
  end
  
  grid = getNthGrid(panel, gridNumber)
  if (isExactRowsMatch)
    assert(table.rows.length == grid.length, "Expected entries: " + table.rows.length.to_s + " Actual: " + grid.length.to_s)
  end
  table.hashes.each do |row|
    found = false
    grid.each do |tr|  
      table.headers.each do |header|
        value = nil
        option = nil
        if (mapping[header].kind_of?(Array))
          #example, fuel gauge tests or visualization, search results
           td = getTdBasedOnAttribute(tr, mapping[header][0])
           # option is for the attribute name that we want to compare to
           option = mapping[header][1].downcase
           value = td.attribute(option)
        else
          value = getAttribute(tr, mapping[header])
        end
        # Special case for fuel gauge test
        if (option == "fuelgauge")
          testFuelGauge(td, row[header])
        else
          if (value == row[header] || (value.strip == "" && row[header]=="<empty>"))
            found = true
          else
            found = false
            break
          end
        end
      end #table.headers.each
      if (found)
        break
      end #if
    end #grid.each
    if (!found)
      puts "Error: This is the entry that was not found:"
      outputRow(row)
      assert(found, "Entry was not found")
    end
  end #table.hashes.each
end

def outputRow(row)
  assert(row!=nil)
  keys = row.keys
  output = ""
  keys.each do |key|
    output += key + ": " + row[key] + ", "
  end
  puts output
end

def isAscendingOrder(order)
  return (order.downcase == "ascending")
end

def sortColumn(columnName, columnType, isAscending, attributeToCompare = nil)
  hTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-htable")}
  returnedName = getColumnLookupName(columnName)
  searchText = "div[id*='" + returnedName + "']"
  column = hTable.find_element(:css, searchText)

  sorted = getColumnValues(columnName, columnType, attributeToCompare).sort

  #caveat:  there's a Bug on Student Column, it only happens on first load, and if Student is the first column to be sorted
  column.click
  if (!isAscending)
    column.click
    sorted = sorted.reverse 
  end
  checkColumnSorted(columnName, columnType, sorted, attributeToCompare)
end

def getCellValue(tr, columnName, columnType, attributeName = nil)
  if (attributeName == nil)
    value = getAttribute(tr, columnName)
  else
    value = getAttributeByName(tr, columnName, attributeName)
  end
  if (columnType == ColumnType::INTEGER)
    value = value.to_i
  elsif (columnType == ColumnType::LETTERGRADE)
    value = letterGradeMapping(value)
  end
  return value
end

def getColumnValues(columnName, columnType, attributeName = nil)
  returnedName = getColumnLookupName(columnName)

  all_trs = getStudentGrid()
  
  unsorted = []
  all_trs.each do |tr|
    unsorted = unsorted + [getCellValue(tr, returnedName, columnType, attributeName)]
  end
  return unsorted
end

def checkColumnSorted(columnName, columnType, expectedSortOrder, attributeName = nil)
  returnedName = getColumnLookupName(columnName)

  all_trs = getStudentGrid()
  i  = 0
  all_trs.each do |tr|
    value = getCellValue(tr, returnedName, columnType, attributeName)
    assert(value == expectedSortOrder[i], "sort order is wrong, expected is " + expectedSortOrder[i].to_s + ", got " + value.to_s)
    i +=1
  end     
end

module ColumnType
  STRING = 0
  INTEGER = 1
  LETTERGRADE = 2
  UNDEFINED = -1
  
  def ColumnType.getType(name)
    name.downcase!
    if (name == "string")
      return ColumnType::STRING
    elsif (name == "integer")
      return ColumnType::INTEGER
    elsif (name == "lettergrade")
      return  ColumnType::LETTERGRADE
    else
      return ColumnType::UNDEFINED
    end
  end
end
