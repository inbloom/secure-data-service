# Given a panelName and the panelInfoName, it reads the table for a generic info section
def viewInfoPanel(panelName, panelInfoName)
  panel = @explicitWait.until{@driver.find_element(:class, panelName)}
  infoPanel = panel.find_element(:class, panelInfoName)
  table_cells = infoPanel.find_elements(:tag_name, "tr")
  name = panel.find_element(:css, "h1") 
  hTable = Hash.new
  hTable["Name"] = name.text
  puts name.text
  
  for i in 0..table_cells.length-1
   th = table_cells[i].find_element(:tag_name, "th") 
   td = table_cells[i].find_element(:tag_name, "td") 
   key = th.text[0..th.text.length-2]
   puts key
   puts td.text
   hTable[key]= td.text
  end
  return hTable
end