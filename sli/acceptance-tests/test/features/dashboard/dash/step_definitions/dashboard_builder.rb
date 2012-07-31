When /^I navigate to the Dashboard Builder page$/ do
  url = getBaseUrl() + "/builder/index.html"
  @driver.get url
end

When /^I click on "(.*?)" Profile Builder$/ do |profileName|
  name = "SLC - " + profileName + " Profile"
  profile_list = @explicitWait.until{@driver.find_element(:class, "profile_list").find_element(:link_text, name)}
  profile_list.click
  
  @explicitWait.until{@driver.find_element(:class,"profilePageWrapper").text.downcase.include? profileName.downcase}
end

When /^I delete Page "(.*?)"$/ do |pageName|
  hoverOverPage(pageName, "delete")
end

When /^I add a Page named "(.*?)"$/ do |pageName|
  puts "clicking on add"
  addSection = @driver.find_element(:class, "addPageSection")
  addSection.find_element(:tag_name, "button").click
  ensurePopupLoaded()
  setPageName(pageName) 
  
  uploadText = "[{\"id\":\"sectionList\",\"parentId\":\"sectionList\",\"name\":null,\"type\":\"TREE\",\"condition\":null,\"data\":null,\"items\":null,\"root\":null,\"description\":null,\"field\":null,\"value\":null,\"width\":null,\"datatype\":null,\"color\":null,\"style\":null,\"formatter\":null,\"sorter\":null,\"align\":null,\"params\":null}]"
  @driver.find_element(:id, "textarea").send_keys(:backspace)
  @driver.find_element(:id, "textarea").send_keys(:backspace)
  @driver.find_element(:id, "textarea").send_keys(uploadText)
  saveDashboardBuilder()
end

When /^I append the text "(.*?)" to the name of Page "(.*?)"$/ do |appendedText, pageName|
  hoverOverPage(pageName, "edit")
  getPageByName(pageName)
  setPageName(appendedText)
  saveDashboardBuilder()
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
  checkPageOrder(pages, 1)
end

def getPageByName(pageName)
  pages = @driver.find_element(:id, "tabs").find_elements(:tag_name, "li")
  pages.each do |page|
    puts page.text
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
  popup = @driver.find_element(:class, "modal-body")
  popup.find_element(:tag_name, "input").send_keys pageName
end

def hoverOverPage(pageName, mode)
  page = getPageByName(pageName)
  assert(page != nil, "Page #{pageName} is not found")
  
  @driver.action.move_to(page).perform
  #ensure that it is hovering on it
  page.find_element(:class, "updatePage")
  
  edit = page.find_elements(:tag_name, "span")
  assert(edit.length == 3)
  if (mode == "edit")
    edit[1].click  
    ensurePopupLoaded()
  elsif (mode == "delete")
    edit[2].click
  end  
end

def saveDashboardBuilder()
  save = @driver.find_element(:class, "modal-footer").find_element(:link_text, "Save")
  # Scroll the browser to the button's co-ords
  yLocation = save.location.y.to_s
  xLocation = save.location.x.to_s
  @driver.execute_script("window.scrollTo(#{xLocation}, #{yLocation});")
  
  save.click 
  ensurePopupUnloaded()
end

def ensurePopupLoaded()
  @explicitWait.until {(style = @driver.find_element(:id, "myModal").attribute('style').strip)  == "display: block;" }
end

def ensurePopupUnloaded()
  @explicitWait.until {(style = @driver.find_element(:id, "myModal").attribute('style').strip) == "display: none;"}
end
