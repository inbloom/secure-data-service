Transform /^teacher (\w+)$/ do |step_arg|
  id = "/teachers/556CC906-D921-4D72-B67F-FE336A702C6C" if step_arg == "Macey"
  id = "/teachers/A006388D-7678-492C-B4E1-49E910901342" if step_arg == "Belle"
  id = "/teachers/EAD6E32C-F902-417C-AFC2-6ED7AF22732C" if step_arg == "Christian"
  id = "/teachers/94BC9152-0364-4778-8165-40D06070EF60" if step_arg == "Illiana"
  id = "/teachers/88B0028C-42C0-4583-9126-170809FA36F2" if step_arg == "Daphne"
  id = "/teachers/C15AB913-73B4-4DB6-B60E-9FD51F4A5036" if step_arg == "Harding"
  id = "/teachers/5B5A7984-505C-4F67-8629-6C90525D3EF5" if step_arg == "Simone"
  id = "/teachers/E29D8551-23B8-4472-9B52-2112C0F67AC9" if step_arg == "Micah"
  id = "/teachers/AA4D01C6-501A-4092-A961-44700B8E37B5" if step_arg == "Quemby"
  id = "/teachers/1739326E-4F18-4979-85D6-86A1FE13321B" if step_arg == "Bert"
  id = "/teachers/11111111-1111-1111-1111-111111111111" if step_arg == "Invalid"
  id = "/teachers/2B5AB1CC-F082-46AA-BE47-36A310F6F5EA" if step_arg == "Unknown"
  id = "/teachers/11111111-1111-1111-1111-111111111111" if step_arg == "WrongURI"
  id = "/teachers"                                      if step_arg == "NoGUID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all teachers$/ do
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+@user+"&password="+@passwd
  res = RestClient.get(url){|response, request, result| response }
  @cookie = res.body[res.body.rindex('=')+1..-1]
  assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    data = Hash[
      "teacherUniqueStateId" => @teacherUniqueStateId,
      "name" => Hash[ "first" => @fname, "middle" => @mname, "last" => @lname ],
      "sex" => @sex,
      "levelOfEducation" => @levelOfEducation]
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data.to_json, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client POST is nil")
    
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.teacher { |b| 
      b.teacherUniqueStateId(@teacherUniqueStateId)
      b.name(@name) 
      b.sex(@sex)
      b.birthDate(@bdate)}
      
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+arg1
    @res = RestClient.post(url, data, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client POST is nil")

  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to GET (teacher \w+)$/ do |teacher_uri|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
  @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT (teacher \w+)$/ do |teacher_uri|
  if @format == "application/json"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
    @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    data = JSON.parse(@res.body)
    data['levelOfEducation'].should_not == @levelOfEducation
    data['levelOfEducation'] = @levelOfEducation
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
    @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client PUT is nil")
    
  elsif @format == "application/xml"
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
    @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
    
    doc = Document.new(@res.body)  
    doc.root.elements["levelOfEducation"].text.should_not == @levelOfEducation
    doc.root.elements["levelOfEducation"].text = @levelOfEducation
    
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
    @res = RestClient.put(url, doc, {:content_type => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response } 
    assert(@res != nil, "Response from rest-client PUT is nil")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to DELETE (teacher \w+)$/ do |teacher_uri|
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+teacher_uri
  @res = RestClient.delete(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
  assert(@res != nil, "Response from rest-client DELETE is nil")
end