#desc "Run Liferay Tests"
#task :liferay => [:liferayTest] do
 # displayFailureReport()
#  if $SUCCESS
 #   puts "Completed All Tests"
 # else
 #   raise "Tests have failed"
 # end
#end

###########################################################
#SLI Liferay testing
###########################################################

#desc "Run the testing for life ray SLI for admin"
#task :liferayTest do
#  runTests("test/features/liferay/SLI_admin.feature")
#  runTests("test/features/liferay/SLI_normal_user.feature")
#  runTests("test/features/liferay/NY_Realm_EULA_agree.feature")
 # runTests("test/features/liferay/report_problem.feature")

# runTests("test/features/liferay/IL_Realm_EULA_agree.feature")

#end