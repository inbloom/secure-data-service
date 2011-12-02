

Given /^the SLI_SMALL dataset is loaded$/ do
  #pending
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students$/ do
  #pending # express the regexp above with the code you wish you had
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash[
      "studentSchoolId" => @studentSchoolId,
      "firstName" => @fname,
      "lastSurname" => @lname,
      "middleName" => @mname,
      "sex" => @sex,
      "birthDate" => @bdate]
    
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data.to_json, :content_type => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client POST is nil")
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.student { |b| 
      b.studentSchoolId(@studentSchoolId)
      b.firstName(@fname) 
      b.lastSurname(@lname)
      b.middleName(@mname)
      b.sex(@sex)
      b.birthDate(@bdate)}
      
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data, :content_type => @format){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client POST is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET "([^"]*)"$/ do |arg1|
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to DELETE "([^"]*)"$/ do |arg1|
  url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['api_server_url']+"/api/rest/"+arg1
  @res = RestClient.delete(url,:accept => @format){|response, request, result| response }
  assert(@res != nil, "Response from rest-client DELETE is nil")
end