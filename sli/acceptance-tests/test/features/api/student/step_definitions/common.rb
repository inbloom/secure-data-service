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
    data = Hash["studentSchoolId" => @studentSchoolId,
      "firstName" => @fname,
      "lastSurname" => @lname,
      "sex" => @sex,
      "birthDate" => @bdate]
    
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data.to_json, :content_type => @format){|response, request, result| response }
    assert(@res != nil, "Response from rest-client POST is nil")
    puts @res.body
    puts @res.raw_headers
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.school { |b| 
      b.fullName(@fullName)
      b.shortName(@shortName) 
      b.stateOrganizationId("50")
      b.webSite(@websiteName)}
    url = "http://"+@user+":"+@passwd+"@"+PropLoader.getProps['server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data, :content_type => @format){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client POST is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to DELETE "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end