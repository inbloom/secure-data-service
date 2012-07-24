############################################################
# SIF tests start
############################################################

desc "Run SIF SchoolInfo Tests"
task :sifSchoolInfoTest do
  runTests("test/features/sif/features/sif_SchoolInfo.feature")
end

desc "Run SIF LEAInfo Tests"
task :sifLEAInfoTest do
  runTests("test/features/sif/features/sif_LEAInfo.feature")
end

desc "Run SIF SEAInfo Tests"
task :sifSEAInfoTest do
  runTests("test/features/sif/features/sif_SEAInfo.feature")
end

############################################################
# SIF tests end
############################################################

