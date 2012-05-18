############################################################
# SDK Tests
############################################################
desc "Run JavaSDK sampleApp acceptance tests"
task :JavaSDKSampleAppTest => [:importUnifiedData, :realmInitNoPeople] do
    runTests("test/features/apiV1/end_user_stories/sandbox/JavaSDK/sampleApp/sampleApp.feature")
end

desc "Run JavaSDK CRUD acceptance tests"
task :JavaSDKCRUDTest => [:realmInitNoPeople,:importStoriedData] do
    runTests("test/features/apiV1/end_user_stories/sandbox/JavaSDK/CRUD/CRUD.feature")
end
############################################################
# SDK Tests end
############################################################
