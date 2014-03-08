############################################################
# SIF tests start
############################################################

desc "Import SIF Sandbox Test Data"
task :importSifSandboxData do
  Rake::Task["importSifBootstrapData"].execute
  setFixture("educationOrganization", "sif/sif_lea_fixture.json", "test/data", false)
  setFixture("educationOrganization", "sif/sif_educationOrganization_fixture.json", "test/data", false)
  setFixture("studentSchoolAssociation", "sif/sif_studentSchoolAssociation_fixture.json", "test/data", true)
  setFixture("student", "sif/sif_student_fixture.json", "test/data", true)
end

desc "Import SIF Bootstrap Test Data"
task :importSifBootstrapData do
  data = {
    "staff" => "sif/sif_bootstrap_staff_fixture.json",
    "educationOrganization" => "sif/sif_bootstrap_educationOrganization_fixture.json",
    "staffEducationOrganizationAssociation" => "sif/sif_bootstrap_staffEducationOrganizationAssociation_fixture.json"
  }
  setMultipleFixtureFiles(data)
end

desc "Run SIF Smoke Tests"
task :sifSmokeTests => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_smoke.feature")
end

desc "Run SIF Integrated Tests"
task :sifIntegratedTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_integrated.feature")
end

desc "Run SIF SchoolInfo Tests"
task :sifSchoolInfoTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_SchoolInfo.feature")
end

desc "Run SIF LEAInfo Tests"
task :sifLEAInfoTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_LEAInfo.feature")
end

desc "Run SIF StudentSchoolEnrollment Tests"
task :sifStudentSchoolEnrollmentTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_StudentSchoolEnrollment.feature")
end

desc "Run SIF StudentLEARelationship Tests"
task :sifStudentLEARelationshipTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_StudentLEARelationship.feature")
end

desc "Run SIF StudentPersonal Tests"
task :sifStudentPersonalTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_StudentPersonal.feature")
end

desc "Run SIF StaffPersonal Tests"
task :sifStaffPersonalTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_StaffPersonal.feature")
end

desc "Run SIF EmployeePersonal Tests"
task :sifEmployeePersonalTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_EmployeePersonal.feature")
end

desc "Run SIF EmploymentRecord Tests"
task :sifEmploymentRecordTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_EmploymentRecord.feature")
end

desc "Run SIF StaffAssignment Tests"
task :sifStaffAssignmentTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_StaffAssignment.feature")
end

desc "Run SIF EmployeeAssignment Tests"
task :sifEmployeeAssignmentTest => [:realmInit] do
  Rake::Task["importSifBootstrapData"].execute
  runTests("test/features/sif/features/sif_EmployeeAssignment.feature")
end

############################################################
# SIF tests end
############################################################

