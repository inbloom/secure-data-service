Then /^Assessment History includes results for:$/ do |table|
  table.hashes.each do |row|
    puts row["Test"]
    panel = getPanel("Assessments", row["Test"])
    assert(panel!=nil, "This Panel Doesn't Exist")    
  end
end

Then /^the Assessment History for "([^"]*)" has the following entries:$/ do |test, table|
  panel = getPanel("Assessments", test)
  #headers
  mapping = {
    "Date" => "administrationDate",
    "Grade" => "gradeLevelAssessedCode",
    "Assessment Name" => "assessmentTitle",
    "Scale score" => "Scale",
    "Other" => "Other",
    "Percentile" => "Percentile",
    "Perf Level" => ["perfLevel", "fuelGauge"]
  }   
  checkGridEntries(panel, table, mapping)
end


