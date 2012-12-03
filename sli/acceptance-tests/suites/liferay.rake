###########################################################
#SLI Liferay tests start
###########################################################

desc "Run Portal Integration Test"
task :portalIntegrationDashboardTests do
  runTests("test/features/liferay/portal_integration.feature")
end

###########################################################
#SLI Liferay tests end
###########################################################
