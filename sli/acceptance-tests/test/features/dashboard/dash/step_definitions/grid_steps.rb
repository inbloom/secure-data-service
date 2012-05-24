#attributeToCompare is the attribute inside the td that we can use as the value to sort by (ex. in perf level, "title" holds the score)
Then /^I click on "([^"]*)" header to sort an integer column in "([^"]*)" order based on "([^"]*)"$/ do |columnName, order, attributeToCompare|
  sortColumn(columnName, ColumnType.getType("integer"), isAscendingOrder(order), attributeToCompare)
end

Then /^I click on "([^"]*)" header to sort a "([^"]*)" column in "([^"]*)" order$/ do |columnName, columnType, order|
   sortColumn(columnName, ColumnType.getType(columnType), isAscendingOrder(order))
end

# returns all trs of a grid in a particular panel
# excludes the header of the grid
def getGrid(panel)
  grid = @explicitWait.until{panel.find_element(:class,"ui-jqgrid-bdiv")}
  all_trs = panel.find_elements(:xpath,".//tr[contains(@class,'ui-widget-content')]")
  return all_trs
end

def getTdBasedOnAttribute(tr,attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  td = tr.find_element(:xpath, searchText)
  return td
end

def getTdsBasedOnAttribute(tr,attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  tds = tr.find_elements(:xpath, searchText)
  return tds
end

def getAttributeByName(tr, attribute, name)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  td = tr.find_element(:xpath, searchText)
  return td.attribute(name)
end

def getAttribute(tr, attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  value = tr.find_element(:xpath, searchText)
  return value.text
end

def getAttributes(tr, attribute)
  assert(!tr.nil?, "Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  values = []
  i = 0
  elements = tr.find_elements(:xpath, searchText)
  elements.each do |element|
    if (element.text.length > 0)
      values[i] = element.text
      i += 1
    end
  end
  return values
end

#Checks against entries in a grid
#use <empty> for empty cells
def checkGridEntries(panel, table, mapping, isExactRowsMatch = true)
  table.headers.each do |current|
    if (mapping[current] == nil)
      puts "Warning: No mapping found for header: " + current
      mapping[current] = current      
    end
  end
  
  grid = getGrid(panel)
  if (isExactRowsMatch)
    assert(table.rows.length == grid.length, "Expected entries: " + table.rows.length.to_s + " Actual: " + grid.length.to_s)
  end
  table.hashes.each do |row|
    found = false
    grid.each do |tr|  
      table.headers.each do |header|
        if (mapping[header].kind_of?(Array))
          #example, fuel gauge tests or visualization
           td = getTdBasedOnAttribute(tr,mapping[header][0])
           value = row[header]
           verifier = mapping[header][1].downcase

           if (verifier == "fuelgauge")
            testFuelGauge(td, value)
           end
        else
          value = getAttribute(tr, mapping[header])
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
  searchText = ".//div[contains(@id,'" + returnedName + "')]"
  column = hTable.find_element(:xpath, searchText)

  all_trs = getStudentGrid()
  
  unsorted = []
  all_trs.each do |tr|
    if (attributeToCompare == nil)
      value = getAttribute(tr, returnedName)
    else
      value = getAttributeByName(tr, returnedName, attributeToCompare)
    end
    if (columnType == ColumnType::INTEGER)
      value = value.to_i
    elsif (columnType == ColumnType::LETTERGRADE)
      value = letterGradeMapping(value)
    end
    unsorted = unsorted + [value]
  end

  #caveat:  there's a Bug on Student Column, it only happens on first load, and if Student is the first column to be sorted
  column.click
  if (!isAscending)
    column.click
    unsorted = unsorted.sort.reverse
  else
    unsorted = unsorted.sort   
  end
  all_trs = getStudentGrid()
  i  = 0
  all_trs.each do |tr|
    if (attributeToCompare == nil)
      value = getAttribute(tr, returnedName)
    else
      value = getAttributeByName(tr, returnedName, attributeToCompare)
    end
    
    if (columnType == ColumnType::LETTERGRADE)
      value = letterGradeMapping(value).to_s
    end
    assert(value == unsorted[i].to_s, "sort order is wrong")
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
