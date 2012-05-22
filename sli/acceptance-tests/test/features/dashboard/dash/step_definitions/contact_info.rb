#This is only for contact information panel
Given /^I look at the panel "([^"]*)"$/ do |panelName|
  tabIndex = getTabIndex("Overview")
  
  overviewTab = @driver.find_element(:id, tabIndex)
  # contact info is in the first panel
  panel = getPanel("Overview", panelName)
  contactSections = panel.find_elements(:xpath, ".//div[@class='tabular']/table/tbody")
  
  #right now we only have 3 sections  
  # 0 is phone, 1 is email, 2 is address
  sectionId = 0
  
  @curPerson = nil
  
  #size of 2:  Student, Parent
  size = contactSections.length
  @people = Array.new(size)
  for k in (0..size)
    @people[k] = Person.new 
  end
  i = 0
  
  contactSections.each do |contactSection|
    all_trs = contactSection.find_elements(:tag_name, "tr") 
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
      @people[i].addContactCategory(sectionId, [th.text])
      @people[i].addContactValue(sectionId, [td.text])  
     end
    end
    i+=1
  end
  #Set the current person to be the student 
  @curPerson = @people[0]
end


When /^there are "([^"]*)" phone numbers$/ do |phoneNumberCount|  
  numOfPhone =  @curPerson.getContactValue(0).length
  assert(numOfPhone == phoneNumberCount.to_i, "Actual phone number count: " + numOfPhone.to_s)
end

Given /^the list of phone number includes "([^"]*)"$/ do |phoneNumber|
  foundPhone = isItemInList(phoneNumber, @curPerson.getContactValue(0))
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
  numOfEmail = @curPerson.getContactValue(1).length
  assert(numOfEmail == emailCount.to_i, "Actual email count: " + numOfEmail.to_s)
end

Given /^the list of email address includes "([^"]*)"$/ do |emailAdress|
  found = isItemInList(emailAdress, @curPerson.getContactValue(1))
  assert(found == true, "Email was not found")
end

Given /^there are "([^"]*)" addresses$/ do |addressCount|
  numOfAddress = @curPerson.getContactValue(2).length
  assert(numOfAddress == addressCount.to_i, "Actual address count: " + numOfAddress.to_s)
end

Given /^the phone number "([^"]*)" is in bold$/ do |phoneNumber|
  pending # express the regexp above with the code you wish you had
end

Given /^the list of address includes$/ do |address|
  found = isItemInList(address, @curPerson.getContactValue(2))
  assert(found == true, "Address is not found")
end

Given /^the order of the phone numbers is "([^"]*)"$/ do |listOfNumbers|
   areItemsInOrder(listOfNumbers, @curPerson.getContactValue(0))
end

Given /^the order of the email addresses is "([^"]*)"$/ do |listOfEmails|
  areItemsInOrder(listOfEmails, @curPerson.getContactValue(1))
end

Given /^I look at "([^"]*)" Contact Info$/ do |personType|
  mapping = {
    "student" => 0,
    "parent" => 1
  }   
  id = mapping[personType.downcase]
  assert(id != nil, "Mapping for person to id is not defined")
  @curPerson = @people[id]
end

# we don't perform an exact match
Given /^the order of the addressess is "([^"]*)"$/ do |listOfAddresses|
  array = listOfAddresses.split(";")
  assert(array.length == @curPerson.getContactValue(2).length, "Address Counts do not match") 
   
  for i in (0..array.length-1)
    current = @curPerson.getContactValue(2)[i]
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
  valueArray = @curPerson.getContactValue(index)
  valueArray.each do |item|
    if (item.include? content)
      found = true
      category = @curPerson.getContactCategory(index)[i]
      assert(category.index(':') > 0 && category.length > 0)
      assert(category[0, category.length-1] == type, "Actual type: " + category) 
    end
    i=i+1
  end
  assert(found, content + " is not found")
end

class Person

  def initialize()
    # 0 is phone, 1 is email, 2 is address
    @contactCategory = []
    @contactValue = []
    for i in (0..2)
      @contactCategory[i] = []
      @contactValue[i] = []
    end     
  end
  
  def addContactCategory(id, value)
    @contactCategory[id] = @contactCategory[id] + value    
  end
    
  def addContactValue(id, value)
    @contactValue[id] = @contactValue[id] + value
  end
  
  def getContactCategory(id)
    return @contactCategory[id]
  end
  
  def getContactValue(id)
    return @contactValue[id]
  end
  
end
