#This is only for contact information panel
Given /^I look at the panel "([^"]*)"$/ do |panelName|
  tabIndex = getTabIndex("Overview")
  
  overviewTab = @driver.find_element(:id, tabIndex)
  # contact info is in the first panel
  panel = getPanel("Overview", panelName)
  #the first table is the student's contact info
  contactSections = panel.find_element(:xpath, ".//div[@class='tabular']/table/tbody")
  
  all_trs = contactSections.find_elements(:tag_name, "tr")

  sectionId = 0
  @section = []
  @sectionType = []
  
  #right now we only have 3 section  
  # 0 is phone, 1 is email, 2 is address, 
  #assert(contactSections.length == 3, "# of Contact Sections" + contactSections.length.to_s)
  
  for i in ( 0..2)
    @section[i] = []
    @sectionType[i] = []
  end   
  
  all_trs.each do |row|
   th = row.find_element(:tag_name, "th")
   td = row.find_element(:tag_name, "td") 
      
   if (td.text.length > 0)
     if (td.text.include? '@')
       sectionId = 1
     elsif (td.text =~ /[A-Za-z,]/ and td.text.include? ',')
       sectionId = 2
     else
       sectionId = 0
     end
    puts sectionId.to_s + " " + th.text + " " + td.text 
    contentType = [th.text]
    @sectionType[sectionId] = @sectionType[sectionId] + contentType
    content = [td.text]
    @section[sectionId] = @section[sectionId] + content
   end
  end
end

When /^there are "([^"]*)" phone numbers$/ do |phoneNumberCount|  
  assert(@section[0].length == phoneNumberCount.to_i, "Actual phone number count: " + @section[0].length.to_s)
end

Given /^the list of phone number includes "([^"]*)"$/ do |phoneNumber|
  foundPhone = isItemInList(phoneNumber, @section[0])
  assert(foundPhone == true, "Phone number was not found")
end

Given /^the phone number "([^"]*)" is of type "([^"]*)"$/ do  |phoneNumber, phoneType|
  checkType(0, phoneNumber, phoneType)
end

Given /^the email "([^"]*)" is of type "([^"]*)"$/ do |emailAddress, emailType|
  checkType(1, emailAddress, emailType)
end

Given /^the address "([^"]*)" is of type "([^"]*)"$/ do |address, addressType|
  checkType(2, address, addressType)
end

Given /^there are "([^"]*)" email addresses$/ do |emailCount|
  assert(@section[1].length == emailCount.to_i, "Actual email count: " + @section[1].length.to_s)
end

Given /^the list of email address includes "([^"]*)"$/ do |emailAdress|
  found = isItemInList(emailAdress, @section[1])
  assert(found == true, "Email was not found")
end

Given /^there are "([^"]*)" addresses$/ do |addressCount|
  assert(@section[2].length == addressCount.to_i, "Actual address count: " + @section[2].length.to_s)
end

Given /^the phone number "([^"]*)" is in bold$/ do |phoneNumber|
  pending # express the regexp above with the code you wish you had
end

Given /^the list of address includes$/ do |address|
  found = isItemInList(address, @section[2])
  assert(found == true, "Address is not found")
end

Given /^the order of the phone numbers is "([^"]*)"$/ do |listOfNumbers|
   areItemsInOrder(listOfNumbers, @section[0])
end

Given /^the order of the email addresses is "([^"]*)"$/ do |listOfEmails|
  areItemsInOrder(listOfEmails, @section[1])
end

# we don't perform an exact match
Given /^the order of the addressess is "([^"]*)"$/ do |listOfAddresses|
  array = listOfAddresses.split(";")
   
  assert(array.length == @section[2].length, "Address Counts do not match")
   
  for i in (0..array.length-1)
    current = @section[2][i]
    searchKey = array[i]
    found = current.include? searchKey
    assert(found == true, "Address ordering is incorrect")
  end
end

def isItemInList(searchValue, content)
  found = false
  content.each do |currentContent|
    if (currentContent == searchValue)
      found = true
    end
  end 
 return found
end

def areItemsInOrder(listOfItems, content)
  array = listOfItems.split(";")
   
  assert(array.length == content.length, "Counts do not match")
   
  for i in (0..array.length-1)
    assert(array[i] == content[i], "Ordering is incorrect")
  end
end

def checkType(index, content, type)
  found = false
  i = 0
  @section[index].each do |item|
    if (item.include? content)
      found = true
      pType = @sectionType[index][i]
      assert(pType.index(':') > 0 && pType.length > 0)
      assert(pType[0, pType.length-1] == type, "Actual type: " + @sectionType[index][i]) 
    end
    i=i+1
  end
  assert(found, content + " is not found")
end
