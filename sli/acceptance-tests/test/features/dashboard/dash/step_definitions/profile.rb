=begin

Copyright 2012 Shared Learning Collaborative, LLC

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