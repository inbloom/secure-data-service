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


Then /^the cutpoints for "([^"]*)" is "([^"]*)"$/ do |testName, cutPoints|
  if (@cutPointMapping == nil)
    @cutPointMapping = Hash.new
  end
  @cutPointMapping[testName] = cutPoints
end

Then /^the fuel gauge for "([^"]*)" in "([^"]*)" column "([^"]*)" is "([^"]*)"$/ do |studentName, assessment, column, score|
  studentCell = getStudentCell(studentName)
  td = getTdBasedOnAttribute(studentCell, assessment + "." + column)
  setCutPoints(assessment)
  testFuelGauge(td, score)
end

def setCutPoints(assessmentName)
  if (@cutPointMapping[assessmentName] != nil)
    @currentCutPoints = @cutPointMapping[assessmentName]
  else
    assert(false, "CutPoints are not defined for " + assessmentName)
  end
end

def testFuelGauge(td, score)

  cutpoints = []
  colorCode = ["#eeeeee","#b40610", "#e58829","#dfc836", "#7fc124","#438746"]
  colorClass = ["fuelGauge","color-widget-red","color-widget-orange", "color-widget-yellow", "color-widget-green"]
  scoreValue = nil
  cutpoints = @currentCutPoints.split(',')

  scoreValue = td.attribute("title")
  assert(score == scoreValue, "Expected: " + score + " but found: " + scoreValue)
  
  index = 0
  
  cutpoints.each do |cutPoint|
    if (cutPoint.to_i <= score.to_i)
         index += 1
     else
      break;
    end
  end
  
  searchText = "div[class*='" + colorClass[index] + "']"
  fuelGaugeColor = td.find_element(:css, searchText)
  assert(fuelGaugeColor != nil, "Fuel Gauge Color is nil")
  
  # Test that it's rendering the rects correctly
  rects = td.find_elements(:tag_name,"rect")
  if (rects.length > 0)
    # use the 2nd rect
    filledPercentage = rects[1].attribute("width")
    color = rects[1].attribute("fill")
    
    # we need to look at the previous index count to get the color
    colorIndex = 0
    if (index > 0 )
      colorIndex = index 
    end
    assert(color == colorCode[colorIndex], "Actual Color: " + color + " Expected Color: " + colorCode[colorIndex] + " at index " + index.to_s)
    
    expectedMaxPercentage = (index.to_f/4)*100
    expectedMinPercentage = 0
    if (index > 0)
      expectedMinPercentage = ((index-1).to_f/4)*100
    end
   
    puts "expected percentage range: " + expectedMinPercentage.to_s + " to " + expectedMaxPercentage.to_s + " Actual: " + filledPercentage
    assert((expectedMinPercentage <= filledPercentage.to_f && expectedMaxPercentage >= filledPercentage.to_f), "Actual Fuel Gauge Percentage is not within range")
  end
end
