

Given /^the SLI_SMALL dataset is loaded$/ do
  #pending
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students$/ do
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+@user+"&password="+@passwd
  res = RestClient.get(url){|response, request, result| response }
  @cookie = res.body[res.body.rindex('=')+1..-1]
  assert(@cookie != nil, "Cookie retrieved was nil")
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
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
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
      
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client POST is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET "([^"]*)"$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    data = JSON.parse(@res.body)
    data['birthData']['birthDate'].should_not == @bdate
    data['birthData']['birthDate'] = @bdate
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
  elsif @format == "application/xml"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    
    doc = Document.new(@res.body)  
    doc.root.elements["birthDate"].text.should_not == @bdate
    doc.root.elements["birthDate"].text = @bdate
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.put(url, doc, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to DELETE "([^"]*)"$/ do |arg1|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.delete(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client DELETE is nil")
end