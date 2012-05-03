When /^I enter the Configuration Area$/ do
  url = getBaseUrl() + "/service/config"
  @driver.get url
end

Then /^I am authorized to the Configuration Area$/ do
  assert(@driver.current_url.include?("/service/config"), "User is not on the service/config page")
end

When /^click Save$/ do
  clickButton("saveButton","id")
  begin
    #@driver.switch_to.alert.accept
    alert = @driver.switch_to.alert
    @alertMessage = alert.text
    puts @alertMessage
    alert.accept
  rescue
  end
end

When /^I paste Invalid json config into the text box$/ do
  invalid = "{
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
            items: [
            "
  putTextForConfigUpload(invalid)
end

Then /^I paste Valid json config into the text box$/ do
  valid = "{
          \"listOfStudents\" :
          {
            id : \"listOfStudents\",
            type : \"PANEL\",
            data :{
              lazy: true,
              entity: \"listOfStudents\",
              cacheKey: \"listOfStudents\"
            },
            root: 'students',
            items : [
              {name: \"Uploaded Custom View\", 
              items: [
                {name: \"My Student\", width: 150, field: \"name.fullName\", formatter:restLink, params: {link:'service/layout/studentProfile/', target:\"_self\"}},
                {name: \"\", width: 60, field: \"programParticipation\", formatter: Lozenge},
                {name: \"Absence Count For Testing\", field: \"attendances.absenceCount\", width:100, sorter: 'int', formatter: CutPointReverse, params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style:'color-widget-green'}, 6:{style: 'color-widget-yellow'}, 11:{style:'color-widget-red'}}}},
                {name: \"Tardy Count For Testing\", field: \"attendances.tardyCount\", width:100, sorter: 'int', formatter: CutPointReverse, params:{cutPoints:{0:{style:'color-widget-darkgreen'}, 1:{style: 'color-widget-green'}, 6:{style:'color-widget-yellow'}, 11:{style:'color-widget-red'}}}}
              ]
              }
            ] 
          }
          ,
          \"studentProfile\" :
          {
            id : \"studentProfile\",
            type: \"LAYOUT\",
            data :{
              entity: \"student\",
              cacheKey: \"student\"
            }, 
            items: [
              {id : \"csi\", name: \"Student Info\", type: \"PANEL\"},
              {id: \"tab8\", name: \"Overview\",  type : \"TAB\", condition: {field: \"gradeLevel\", value: [\"Fourth Grade\", \"Fifth Grade\", \"Sixth Grade\", \"Seventh Grade\", \"Eighth grade\", \"Other\", \"Ungraded\", \"Not Available\"]}, items: [{id : \"contactInfo\", type: \"PANEL\"}, {id : \"enrollmentHist\", name: \"Student Enrollment Panel\", type: \"GRID\"}]},
              {id: \"tab1\", name: \"ELL\", type : \"TAB\", condition: {field: \"limitedEnglishProficiency\", value: [\"Limited\"]}, items: []}
            ]
          }
        }"
  
  putTextForConfigUpload(valid)
end

When /^I logout$/ do
  # current logout functionaly means delete all the cookies
  @driver.manage.delete_all_cookies
end

Then /^I should be shown a success message$/ do
  assert(@alertMessage.include?("Success"), "Actual message: " + @alertMessage)
end

Then /^I should be shown a failure message$/ do
  assert(@alertMessage.include?("input should be a valid JSON string"), "Actual message: " + @alertMessage)
end

Then /^I reset custom config$/ do
  putTextForConfigUpload("{}")
end

def putTextForConfigUpload(uploadText)
  textBoxId = "jsonText"
  textArea = @driver.find_element(:id, textBoxId)
  #this clears the text area
  textArea.clear()
  putTextToField(uploadText, textBoxId ,"id")
end
