############################################################
# SIF tests start
############################################################

desc "Import SIF Sandbox Test Data"
task :importSifSandboxData do
  testHash = Hash[
    "staff" => "sif/sif_staff_fixture.json",
    "educationOrganization" => "sif/sif_educationOrganization_fixture.json",
    "staffEducationOrganizationAssociation" => "sif/sif_staffEducationOrganizationAssociation_fixture.json"
  ]
  setMultipleFixtureFiles(testHash)
end

desc "Run SIF SchoolInfo Tests"
task :sifSchoolInfoTest => [:realmInit] do
  Rake::Task["importSifSandboxData"].execute
  runTests("test/features/sif/features/sif_SchoolInfo.feature")
end

desc "Run SIF LEAInfo Tests"
task :sifLEAInfoTest => [:realmInit] do
  Rake::Task["importSifSandboxData"].execute
  runTests("test/features/sif/features/sif_LEAInfo.feature")
end

desc "Run SIF SEAInfo Tests"
task :sifSEAInfoTest => [:realmInit] do
  Rake::Task["importSifSandboxData"].execute
  runTests("test/features/sif/features/sif_SEAInfo.feature")
end

############################################################
# SIF tests end
############################################################

