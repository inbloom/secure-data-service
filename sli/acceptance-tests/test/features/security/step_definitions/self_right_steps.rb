Transform /staff ed org association for "(.*?)"/ do |staff|
  result = "a29e3113-316d-bfd1-4b00-b9121b8fdfd3" if staff == "agillespie"
  result = "e42b5418-dc5d-4f91-a0cd-d604bd39fc32" if staff == "jvasquez"
  result
end

Given /^I make a call to get the (staff ed org association for ".*?")$/ do |staff|
  restHttpGet("/v1/staffEducationOrgAssignmentAssociations/" + staff)
end
