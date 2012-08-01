# Given a panelName and the panelInfoName, it reads the table for a generic info section
def viewInfoPanel(panelName, panelInfoName = nil)
  panel = @explicitWait.until{@driver.find_element(:class, panelName)}
  name = panel.find_element(:css, "h1") 
  hTable = Hash.new
  hTable["Name"] = name.text
  puts name.text
  
  if (panelInfoName != nil)
    infoPanel = panel.find_element(:class, panelInfoName)
    table_cells = infoPanel.find_elements(:tag_name, "tr")
  
    for i in 0..table_cells.length-1
     th = table_cells[i].find_element(:tag_name, "th") 
     td = table_cells[i].find_element(:tag_name, "td") 
     key = th.text[0..th.text.length-2]
     puts key
     puts td.text
     hTable[key]= td.text
    end
  end
  return hTable
end