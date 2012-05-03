Then /^Student Enrollment History has the following entries:$/ do |table|
  panel = getPanel("Overview", "Enrollment History")
  #headers
  mapping = {
    "Year" => "schoolYear",
    "School" => "nameOfInstitution",
    "Gr" => "entryGradeLevel",
    "Entry Date" => "entryDate",
    "Entry Type" => "entryType",
    "Transfer" => "transfer",
    "Withdraw Date" => "exitWithdrawDate",
    "Withdraw Type" => "exitWithdrawType" 
  }   
  checkGridEntries(panel, table, mapping)
end
