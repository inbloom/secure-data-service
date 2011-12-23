

Transform /^student "([^"]*)"$/ do |step_arg|
  id = "/students/714c1304-8a04-4e23-b043-4ad80eb60992" if step_arg == "Alfonso"
  id = "/students/e1af7127-743a-4437-ab15-5b0dacd1bde0"  if step_arg == "Priscilla"
  id = "/students/61f13b73-92fa-4a86-aaab-84999c511148" if step_arg == "Alden"
  id = "/students/11111111-1111-1111-1111-111111111111"  if step_arg == "Invalid"
  id = "/students/289c933b-ca69-448c-9afd-2c5879b7d221" if step_arg == "Donna"
  id = "/students/c7146300-5bb9-4cc6-8b95-9e401ce34a03" if step_arg == "Rachel"
  id = "/student/11111111-1111-1111-1111-111111111111" if step_arg == "WrongURI"
  id = "/students"                                     if step_arg == "NoGUID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students$/ do
  idpLogin(@user, @passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentUniqueStateId" => @studentSchoolId,
      "name" => Hash[
        "firstName" => @fname,
        "lastSurname" => @lname,
        "middleName" => @mname],
      "sex" => @sex,
      "birthData" => Hash[
        "birthDate" => @bdate
        ]
      ]
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.student { |b| 
      b.studentSchoolId(@studentSchoolId)
      b.firstName(@fname) 
      b.lastSurname(@lname)
      b.middleName(@mname)
      b.sex(@sex)
      b.birthDate(@bdate)}      
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to GET (student "[^"]*")$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to PUT (student "[^"]*")$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  if @format == "application/json"
    dataH = JSON.parse(@res.body)
    dataH['birthData']['birthDate'].should_not == @bdate
    dataH['birthData']['birthDate'] = @bdate
    data = dataH.to_json
  elsif @format == "application/xml"    
    doc = Document.new(@res.body)  
    doc.root.elements["birthDate"].text.should_not == @bdate
    doc.root.elements["birthDate"].text = @bdate
    data = doc
  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(student_uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I navigate to DELETE (student "[^"]*")$/ do |student_uri|
  restHttpDelete(student_uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end