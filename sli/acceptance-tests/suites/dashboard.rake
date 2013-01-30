############################################################
# Dashboard tests start
############################################################
desc "Run Dashboard population widget Tests"
task :dashboardPopWidgetTests do
  runTests("test/features/dashboard/dash/population_widget.feature")
end

desc "Run Dashboard section profile tests Tests"
task :dashboardSdsSectionProfileTests do
  runTests("test/features/dashboard/dash/section_profile.feature")
end

desc "Run Dashboard K-3 view Tests"
task :dashboardK3ViewTests do
  runTests("test/features/dashboard/dash/k3view.feature")
end

desc "Run Dashboard High School view Tests"
task :dashboardHSViewTests do
  runTests("test/features/dashboard/dash/high_school_view.feature")
end

desc "Run Dashboard user based view selection tests"
task :dashboardUserBasedViewTests do
  runTests("test/features/dashboard/dash/user_based_views.feature")
end

desc "Run Dashboard integration acceptance tests"
task :dashboardIntegratedTests do
    runTests("test/features/dashboard/dash/dashboard_api_integration.feature")
end


desc "Run Dashboard login acceptance tests"
task :dashboardLoginTests do
    runTests("test/features/dashboard/dash/dashboard_login.feature")
end

desc "Run Dashboard Assessment tests"
task :dashboardAssessmentTests do
    runTests("test/features/dashboard/dash/assessments.feature")
end

desc "Run Dashboard most recent result acceptance tests"
task :MostRecentResultTests do
    runTests("test/features/dashboard/dash/MostRecent.feature")
end

desc "Run Dashboard highest ever result acceptance tests"
task :HighestEverResultTests do
    runTests("test/features/dashboard/dash/Highest_AssessmentScore.feature")
end

desc "Run Dashboard performance level acceptance tests"
task :PerformanceLevels do
    runTests("test/features/dashboard/assessments/PerformanceLevels_MostRecentWindow.feature")
end

desc "Run dashboard attendance related tests"
task :dashboardAttendance do
  runTests("test/features/dashboard/dash/attendance.feature")
end

desc "Run dashboard student profile tests"
task :dashboardStudentProfileTests do
  runTests("test/features/dashboard/dash/student_profile.feature")
end

desc "Run dashboard student historical data tests"
task :dashboardStudentHistoricalDataTests do
  runTests("test/features/dashboard/students/HistoricalData.feature")
end

desc "Run dashboard student contact info live tests"
task :dashboardStudentContactInfoLiveTests do
  runTests("test/features/dashboard/dash/contact_info_live.feature")
end

desc "Run dashboard sad path tests"
task :dashboardSadPathTests do
  runTests("test/features/dashboard/dash/dashboard_sad_path.feature")
end

desc "Run dashboard HTML escape tests"
task :dashboardHTMLEscapeTests do
  runTests("test/features/dashboard/dash/dashboard_html_escape.feature")
end

desc "Run dashboard teacher profile test"
task :dashboardSdsTeacherProfileTests do
  runTests("test/features/dashboard/dash/teacher_profile.feature")
end

desc "Dashboard Sad Path Test Suite"
task :dashboardSadPathTestSuite => [:ingestionDashboardSadPathTest,
                                    :realmInitNoPeople,
                                    :dashboardSadPathTests] do
end

desc "Run Local Dashboard Tests - Import Realm, Import Data Data, Run Tests"
task :localDashboardTests do
  Rake::Task["loadDefaultIngestionTenants"].invoke
  Rake::Task["dashboardSadPathTestSuite"].invoke
  Rake::Task["realmInitNoPeople"].invoke
  Rake::Task["importUnifiedData"].invoke
  Rake::Task["dashboardTests"].invoke
  OTHER_TAGS = OTHER_TAGS+" --tags @integration"
  Rake::Task["ingestionAcceptanceSdsTest"].execute
  Rake::Task["dashboardSdsTests"].invoke
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run dashboard integration tests"
task :dashboardSdsCoreStudentsTests do
  runTests("test/features/dashboard/dash/dashboard_sds.feature")
end

desc "Run dashboard integrated Tests - Student Contact info"
task :dashboardSdsContactInfoTests do
  runTests("test/features/dashboard/dash/contact_info_sds.feature")
end

desc "Run dashboard integrated Tests - Login Tests"
task :dashboardSdsLoginTests do
  runTests("test/features/dashboard/dash/dashboard_login_sds.feature")
end

desc "Run Dashboard URL Validation tests"
task :dashboardSdsURLValidationTests do
    runTests("test/features/dashboard/dash/dashboard_url_validation_sds.feature")
end

desc "Run dashboard integrated Tests - High School View Tests"
task :dashboardSdsHSViewTests do
  runTests("test/features/dashboard/dash/high_school_view_sds.feature")
end

desc "Run dashboard integrated Tests - K3 View Tests"
task :dashboardSdsK3ViewTests do
  runTests("test/features/dashboard/dash/k3view_sds.feature")
end

desc "Run dashboard integrated Tests - Population Widget Tests"
task :dashboardSdsPopWidgetTests do
  runTests("test/features/dashboard/dash/population_widget_sds.feature")
end

desc "Run dashboard integrated Tests - Student Profile Tests"
task :dashboardSdsStudentProfileTests do
  runTests("test/features/dashboard/dash/student_profile_sds.feature")
end

desc "Run dashboard integrated Tests - User Based Views Tests"
task :dashboardSdsUserBasedViewsTests do
  runTests("test/features/dashboard/dash/user_based_views_sds.feature")
end

desc "Run dashboard integrated Tests - Attendance Tests"
task :dashboardSdsAttendanceTests do
  runTests("test/features/dashboard/dash/attendance_sds.feature")
end

desc "Run dashboard integrated Tests - Transcript History Panel"
task :dashboardSdsTranscriptPanelTests do
  runTests("test/features/dashboard/students/TranscriptHistoryPanel_sds.feature")
end

desc "Run dashboard integrated Tests - Simple Search Tests"
task :dashboardStudentSearchTests do
  runTests("test/features/dashboard/dash/student_search.feature")
end

desc "Run dashboard integrated Tests - Upload Config"
task :dashboardSdsUploadConfigTests do
  runTests("test/features/dashboard/dash/upload_config_sds.feature")
end

desc "Run dashboard student gradebook entry tests"
task :dashboardStudentGradebookEntryTests do
  runTests("test/features/dashboard/students/StudentGradebookEntry.feature")
end

desc "Run dashboard builder tests"
task :dashboardSdsDashboardBuilderTests do
  runTests("test/features/dashboard/dash/dashboard_builder.feature")
end

desc "Run dashboard attendance calendar tests"
task :dashboardSdsAttendanceCalendarTests do
  runTests("test/features/dashboard/dash/attendance_calendar.feature")
end

desc "Run Dashboard Smoke Tests - Assumes Daybreak SDS previously ingested"
task :dashboardSmokeTests do
  @tags = ["~@wip", "@smoke", "~@sandbox"]
  runTests("test/features/dashboard/dash/smoked_dashboard.feature")
end

desc "Run dashboard qunit tests"
task :dashboardQunitTests do
  runTests("test/features/dashboard/dash/qunit_tests.feature")
end

desc "Run dashboard qunit tests"
task :dashboardJmeterTests do
  runTests("test/features/dashboard/jmeter/dashboard_jmeter_performance.feature")
end


############################################################
# Dashboard local dev environmnet
############################################################

desc "Setup local dashboard dev enviroment"
task :dashboardSetup  => [:realmInitNoPeople] do
  OTHER_TAGS = OTHER_TAGS+" --tags @integration"
  Rake::Task["ingestionAcceptanceSdsTest"].execute
  Rake::Task["addBootstrapAppAuths"].execute
end

############################################################
# Dashboard tests end
############################################################
