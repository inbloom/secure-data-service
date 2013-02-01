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


When /^I enter the Configuration Area$/ do
  url = getBaseUrl() + "/service/config"
  @driver.get url
end

Then /^I am authorized to the Configuration Area$/ do
  assertWithWait("User is not on the service/config page") {@driver.page_source.include?("Save Config")}
end

Then /^I am unauthorized to the Configuration Area$/ do
  assertText("This page is only available for district level IT Administrator.")
end

When /^click Save$/ do
  clickButton("saveButton","id")
  begin
    alert = nil
    @explicitWait.until{ (alert = isAlertPresent()) != nil}
    @alertMessage = alert.text
    puts "Alert: " + @alertMessage
    alert.accept
  rescue
  end
  assert(@alertMessage != nil, "Alert Pop-up message was not found")
end

When /^I paste Invalid json config into the text box$/ do
  invalid = "{config: 
          {
          \"listOfStudents\" :
          id : \"listOfStudents\",
          type : \"PANEL\",
          data :{
            lazy: true,
            entity: \"listOfStudents\",
            cacheKey: \"listOfStudents\"
          },
          root: 'students',
          items : [
            {name: \"Invalid View\", 
            items: 
            "
  putTextForConfigUpload(invalid)
end

Then /^I paste Valid json config into the text box$/ do
        
  customConfig = "{config: {
\"listOfStudents\" :
{
  id : \"listOfStudents\",
  type : \"PANEL\",
  data :{
    lazy: true,
    entity: \"listOfStudents\",
    cacheKey: \"listOfStudents\",
    params: {
      assessmentFilter: {\"StateTest Reading\": \"HIGHEST_EVER\",
                         \"StateTest Writing\": \"MOST_RECENT_WINDOW\",
                         \"READ 2.0\": \"MOST_RECENT_WINDOW\",
                         \"TRC\": \"MOST_RECENT_WINDOW\",
                         \"SAT Reading\": \"HIGHEST_EVER\",
                         \"SAT Writing\": \"HIGHEST_EVER\",
                         \"AP Language\": \"HIGHEST_EVER\",
                         \"AP Literature\": \"HIGHEST_EVER\" }
    }
  },
  root: 'students',
  items : [
    {name: \"Middle School ELA View\",
     condition: {field: \"gradeLevel\", value: [\"Third grade\", \"Fourth grade\", \"Fifth grade\", \"Sixth grade\", \"Seventh grade\", \"Eighth grade\"]},
     items: [
      {name: \"Student\", width: 150, field: \"name.fullName\", style:\"ui-ellipsis\", formatter:'restLink', params: {link:'student', target:\"_self\"}},
      {name: \"\", width: 60, field: \"programParticipation\", formatter: 'Lozenge'},
      {name: \"StateTest Reading (highest ever)\", items:[
        {name: \"Performance Level\", field: \"assessments.StateTest Reading.perfLevel\", width:150, sorter: 'ProxyInt', formatter: 'FuelGaugeWithScore', params:{sortField: 'assessments.StateTest Reading.Scale score', name:'StateTest Reading', valueField:'Scale score', fieldName: \"StateTestReading\", cutPoints:{ 5:{style:'color-widget-darkgreen'}, 4:{style:'color-widget-green'}, 3:{style:'color-widget-yellow'}, 2:{style:'color-widget-orange'}, 1:{style:'color-widget-red'}}}},
        {name: \"SS\", field: \"assessments.StateTest Reading.Scale score\", width:45, sorter: 'int'},
        {name: \"Lexile\", field: \"assessments.StateTest Reading.Other\", width:45, sorter: 'int'}]},
      {name: \"StateTest Writing (most recent)\", items:[
        {name: \"Performance Level\", field: \"assessments.StateTest Writing.perfLevel\", width:150, sorter: 'ProxyInt', formatter: 'FuelGaugeWithScore', params:{sortField: 'assessments.StateTest Writing.Scale score', name:'StateTest Writing', valueField:'Scale score', fieldName: \"StateTestWriting\", cutPoints:{ 5:{style:'color-widget-darkgreen'}, 4:{style:'color-widget-green'}, 3:{style:'color-widget-yellow'}, 2:{style:'color-widget-orange'}, 1:{style:'color-widget-red'}}}},
        {name: \"SS\", field: \"assessments.StateTest Writing.Scale score\", width:45, sorter: 'int'}]},
      {name: \"Current Grades\", items:[
               {name: \"Unit Test 1\", field: \"currentSession-0\", width:100, sorter: 'LettersAndNumbers', formatter: 'Grade'}
            ]
      },
      {name: \"Final Grades\", items:[
               {name: \"Last Semester\", field: \"previousSemester\", width:100, sorter: 'LetterGrade', formatter: 'TearDrop'},
               {name: \"2 Semesters ago\", field: \"twoSemestersAgo\", width:90, sorter: 'LetterGrade', formatter: 'TearDrop'}
            ]
      },
      {name: \"Attendance (current school year)\", items:[
        {name: \"Absence Count\", field: \"attendances.absenceCount\", width:60, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style:'color-widget-red'}}}},
        {name: \"Attendance Rate %\", field: \"attendances.attendanceRate\", width:75, sorter: 'int', formatter: 'CutPoint', params:{cutPoints:{89:{style:'color-widget-red'}, 94:{style: 'color-widget-yellow'}, 98:{style:'color-widget-green'}, 100:{style:'color-widget-darkgreen'}}}},
        {name: \"Tardy Count\", field: \"attendances.tardyCount\", width:50, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style:'color-widget-red'}}}},
        {name: \"Tardy Rate %\", field: \"attendances.tardyRate\", width:60, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style:'color-widget-red'}}}}]}
    ]},
    {name: \"Early Literacy View\",
     condition: {field: \"gradeLevel\", value: [\"Kindergarten\", \"First grade\", \"Second grade\", \"Third grade\"]},
     items: [
      {name: \"Student\", field: \"name.fullName\", width:150, formatter:'restLink', style:\"ui-ellipsis\", params: {link:'student', target:\"_self\"}},
      {name: \"\", width: 60, field: \"programParticipation\", formatter: 'Lozenge'},
      {name: \"READ 2.0\", items:[
        {name: \"Performance Level\", field: \"assessments.READ 2_0.perfLevel\", width:100}]},
      {name: \"Reading\", items:[
        {name: \"RL\", field: \"assessments.TRC.readingLevel\", width:100},
        {name: \"Prof. Level\", field: \"assessments.TRC.profLevel\", width:100}]},
      {name: \"Attendance\", items:[
        {name: \"Absence Count\", field: \"attendances.absenceCount\", sorter: 'int', width:60, formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style: 'color-widget-red'}}}},
        {name: \"Tardy Count\", field: \"attendances.tardyCount\", sorter: 'int', width:50, formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style: 'color-widget-red'}}}}]}
    ]},
    {name: \"College Ready ELA View\",
     condition: {field: \"gradeLevel\", value: [\"Ninth grade\", \"Tenth grade\", \"Eleventh grade\", \"Twelfth grade\"]},
     items: [
      {name: \"Student\", field: \"name.fullName\", width:150, formatter:'restLink', style:\"ui-ellipsis\", params: {link:'student', target:\"_self\"}},
      {name: \"\", width: 60, field: \"programParticipation\", formatter: 'Lozenge'},
      {name: \"Reading Test Scores (Highest)\", items:[
        {name: \"SAT\", field: \"assessments.SAT Reading.x\", width:100, sorter: 'int'},
        {name: \"%ile\", field: \"assessments.SAT Reading.percentile\", width:100, sorter: 'int'}]},
      {name: \"Writing Test Scores (Highest)\", items:[
        {name: \"SAT\", field: \"assessments.SAT Writing.x\", width:100, sorter: 'int'},
        {name: \"%ile\", field: \"assessments.SAT Writing.percentile\", width:100, sorter: 'int'}]},
      {name: \"AP Eng. Exam Scores (Highest)\", items:[
        {name: \"Lang.\", field: \"assessments.AP Language.x\", width:100},
        {name: \"Lit.\", field: \"assessments.AP Literature.x\", width:100}]},
      {name: \"Final Grades\", items:[
        {name: \"Last Semester\", field: \"previousSemester\", width:100, sorter: 'LetterGrade', formatter: 'TearDrop'},
        {name: \"2 Semesters ago\", field: \"twoSemestersAgo\", width:90, sorter: 'LetterGrade', formatter: 'TearDrop'}]},
      {name: \"Attendance\", items:[
        {name: \"Absence Count\", field: \"attendances.absenceCount\", width: 60, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style: 'color-widget-red'}}}},
        {name: \"Attendance Rate %\", field: \"attendances.attendanceRate\", width: 75, sorter: 'int', formatter: 'CutPoint', params:{cutPoints:{89:{style: 'color-widget-red'}, 94:{style:'color-widget-yellow'}, 98:{style:'color-widget-green'}, 100:{style: 'color-widget-darkgreen'}}}},
        {name: \"Tardy Count\", field: \"attendances.tardyCount\", width: 50, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style: 'color-widget-red'}}}},
        {name: \"Tardy Rate %\", field: \"attendances.tardyRate\", width: 60, sorter: 'int', formatter: 'CutPointReverse', params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style: 'color-widget-red'}}}}]}
    ]}
  ]
}
,
\"student\" :
{
  id : \"student\",
  name: \"inBloom - Student Profile\",
  type: \"LAYOUT\",
  data :{
    entity: \"student\",
    cacheKey: \"student\"
  },
  items: [
    {id : \"csi\", name: \"Student Info\", type: \"PANEL\"},
    {id: \"tab7\", name: \"Elementary School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Infant/toddler\", \"Early Education\", \"Preschool/Prekindergarten\", \"Transitional Kindergarten\", \"Kindergarten\", \"First Grade\", \"Second Grade\", \"Third Grade\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab8\", name: \"Middle School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Fourth Grade\", \"Fifth Grade\", \"Sixth Grade\", \"Seventh Grade\", \"Eighth grade\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab9\", name: \"High School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Ninth grade\", \"Tenth grade\", \"Eleventh grade\", \"Twelfth grade\", \"Adult Education\", \"Grade 13\", \"Postsecondary\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab2\", name: \"Attendance and Discipline\", type : \"TAB\", items: [{id : \"attendanceHist\", type: \"REPEAT_HEADER_GRID\"}, {id: \"studentAttendanceCalendar\", type: \"PANEL\"}]},
    {id: \"tabE\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistREAD2\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Infant/toddler\", \"Early Education\", \"Preschool/Prekindergarten\", \"Transitional Kindergarten\", \"Kindergarten\", \"First Grade\", \"Second Grade\", \"Third Grade\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tabM\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistStateTestR\", type: \"GRID\"}, {id : \"assessmentHistStateTestW\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Fourth Grade\", \"Fifth Grade\", \"Sixth Grade\", \"Seventh Grade\", \"Eighth grade\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tabH\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistAPE\", type: \"GRID\"}, {id : \"assessmentHistSATR\", type: \"GRID\"}, {id : \"assessmentHistSATW\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Ninth grade\", \"Tenth grade\", \"Eleventh grade\", \"Twelfth grade\", \"Adult Education\", \"Grade 13\", \"Postsecondary\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tab4\", name: \"Grades and Credits\",  type : \"TAB\", items: [ {id : \"transcriptHistory\", type : \"PANEL\"}, {id: \"currentCoursesAndGrades\", type: \"GRID\"}]}
  ]
}
,
\"assessmentHistAPE\" :
{
    id : \"assessmentHistAPE\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : AP English\",
    data : {
        cacheKey: 'assessmentHistAPE',
        params: { assessmentFamily: \"AP.AP Eng\"}
    }
}
,
\"assessmentHistREAD2\" :
{
    id : \"assessmentHistREAD2\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : READ 2.0\",
    data : {
        cacheKey: 'assessmentHistREAD2',
        params: { assessmentFamily: \"READ 2.0.READ 2.0 Grade 1\"}
    }
}
,
\"assessmentHistStateTestR\" :
{
    id : \"assessmentHistStateTestR\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : StateTest Reading\",
    data : {
        cacheKey: 'assessmentHistStateTestR',
        params: { assessmentFamily: \"StateTest Reading\"}
    }
}
,
\"assessmentHistStateTestW\" :
{
    id : \"assessmentHistStateTestW\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : StateTest Writing\",
    data : {
        cacheKey: 'assessmentHistStateTestW',
        params: { assessmentFamily: \"StateTest Writing\"}
    }
}
,
\"assessmentHistSATR\" :
{
    id : \"assessmentHistSATR\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : SAT Reading\",
    data : {
        cacheKey: 'assessmentHistSATR',
        params: { assessmentFamily: \"SAT Reading\"}
    }
}
,
\"assessmentHistSATW\" :
{
    id : \"assessmentHistSATW\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : SAT Writing\",
    data : {
        cacheKey: 'assessmentHistSATW',
        params: { assessmentFamily: \"SAT Writing\"}
    }
}
}
}
"
  
  putTextForConfigUpload(customConfig)
end

When /^I logout$/ do
  # current logout functionaly means delete all the cookies
  @driver.manage.delete_all_cookies
  browser = PropLoader.getProps['browser'].downcase
  # cannot delete httponly cookie in IE
  if (browser == "ie")
    @driver.quit
    @driver = Selenium::WebDriver.for :ie
  end
end

Then /^I should be shown a success message$/ do
  assert(@alertMessage.include?("Success"), "Actual message: " + @alertMessage)
end

Then /^I should be shown a failure message$/ do
  assert(@alertMessage.include?("Your request could not be completed."), "Actual message: " + @alertMessage)
end

Then /^I reset custom config$/ do
  putTextForConfigUpload("{}")
end

Then /^I see an error$/ do
  @driver.find_element(:class,"error-container")
end

Then /^I paste configuration to hide csi panel$/ do
  hideCSIconfig = "{config: {
\"studentProfile\" :
{
  id : \"studentProfile\",
  name: \"inBloom - Student Profile\",
  type: \"LAYOUT\",
  data :{
    entity: \"student\",
    cacheKey: \"student\"
  }, 
  items: [
    {id : \"csi\", name: \"Student Info\", type: \"PANEL\",  condition: {field: \"gradeLevel\", value: [\"Eleventh Grade\"]}},
    {id: \"tab7\", name: \"Elementary School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Infant/toddler\", \"Early Education\", \"Preschool/Prekindergarten\", \"Transitional Kindergarten\", \"Kindergarten\", \"First Grade\", \"Second Grade\", \"Third Grade\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab8\", name: \"Middle School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Fourth Grade\", \"Fifth Grade\", \"Sixth Grade\", \"Seventh Grade\", \"Eighth grade\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab9\", name: \"High School Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Ninth grade\", \"Tenth grade\", \"Eleventh grade\", \"Twelfth grade\", \"Adult Education\", \"Grade 13\", \"Postsecondary\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
    {id: \"tab2\", name: \"Attendance and Discipline\", type : \"TAB\", items: [{id : \"attendanceHist\", type: \"GRID\"}]},
    {id: \"tabE\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistREAD2\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Infant/toddler\", \"Early Education\", \"Preschool/Prekindergarten\", \"Transitional Kindergarten\", \"Kindergarten\", \"First Grade\", \"Second Grade\", \"Third Grade\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tabM\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistStateTestR\", type: \"GRID\"}, {id : \"assessmentHistStateTestW\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Fourth Grade\", \"Fifth Grade\", \"Sixth Grade\", \"Seventh Grade\", \"Eighth grade\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tabH\", name: \"Assessments\",  type : \"TAB\", items: [{id : \"assessmentHistAPE\", type: \"GRID\"}, {id : \"assessmentHistSATR\", type: \"GRID\"}, {id : \"assessmentHistSATW\", type: \"GRID\"}], condition: {field: \"gradeLevel\", value: [\"Ninth grade\", \"Tenth grade\", \"Eleventh grade\", \"Twelfth grade\", \"Adult Education\", \"Grade 13\", \"Postsecondary\", \"Other\", \"Ungraded\", \"Not Available\"]}},
    {id: \"tab4\", name: \"Grades and Credits\",  type : \"TAB\", items: [ {id : \"transcriptHistory\", type : \"PANEL\"} ]}
  ]
}
,
\"assessmentHistAPE\" :
{
    id : \"assessmentHistAPE\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : AP English\",
    data : {
        cacheKey: 'assessmentHistAPE',
        params: { assessmentFamily: \"AP.AP Eng\"}
    }
}
,
\"assessmentHistREAD2\" :
{
    id : \"assessmentHistREAD2\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : READ 2.0\",
    data : {
        cacheKey: 'assessmentHistREAD2',
        params: { assessmentFamily: \"READ 2.0.READ 2.0 Grade 1\"}
    }
}
,
\"assessmentHistStateTestR\" :
{
    id : \"assessmentHistStateTestR\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : StateTest Reading\",
    data : {
        cacheKey: 'assessmentHistStateTestR',
        params: { assessmentFamily: \"StateTest Reading\"}
    }
}
,
\"assessmentHistStateTestW\" :
{
    id : \"assessmentHistStateTestW\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : StateTest Writing\",
    data : {
        cacheKey: 'assessmentHistStateTestW',
        params: { assessmentFamily: \"StateTest Writing\"}
    }
}
,
\"assessmentHistSATR\" :
{
    id : \"assessmentHistSATR\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : SAT Reading\",
    data : {
        cacheKey: 'assessmentHistSATR',
        params: { assessmentFamily: \"SAT Reading\"}
    }
}
,
\"assessmentHistSATW\" :
{
    id : \"assessmentHistSATW\",
    parentId: \"assessmentHist\",
    type : \"GRID\",
    name : \"Test History : SAT Writing\",
    data : {
        cacheKey: 'assessmentHistSATW',
        params: { assessmentFamily: \"SAT Writing\"}
    }
}
} 
}"
  putTextForConfigUpload(hideCSIconfig)
end

def putTextForConfigUpload(uploadText)
  textBoxId = "jsonText"
  textArea = @driver.find_element(:id, textBoxId)
  #this clears the text area
  textArea.clear()

  # On slower machines, the old send_keys method would timeout after a minute.
  # This is an attempt to set it through javascript to speed up execution and prevent timeout
  uploadText = uploadText.gsub(/\r/,"\\n").gsub(/\n/,"\\n").gsub(/\"/,'\\\"')
  @driver.execute_script("document.getElementById('jsonText').value = \"#{uploadText}\";")
end

def isAlertPresent()
 begin
    return @driver.switch_to.alert
  rescue Selenium::WebDriver::Error::NoAlertPresentError => e  
     puts e.message  
     return nil
  end 
  
end
