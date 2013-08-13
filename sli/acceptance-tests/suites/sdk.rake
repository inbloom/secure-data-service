############################################################
# SDK Tests
############################################################
desc "Run JavaSDK sampleApp acceptance tests"
task :JavaSDKSampleAppTest => [:realmInit, :importSandboxData] do
    runTests("test/features/apiV1/end_user_stories/sandbox/JavaSDK/sampleApp/sampleApp.feature")
end

desc "Run JavaSDK CRUD acceptance tests"
task :JavaSDKCRUDTest => [:realmInit,:importSandboxData] do
    runTests("test/features/apiV1/end_user_stories/sandbox/JavaSDK/CRUD/CRUD.feature")
end

desc "Run JavaSDK REST CRUD acceptance tests"
task :JavaRESTCRUDTest => [:realmInit,:importSandboxData] do
    runTests("test/features/apiV1/end_user_stories/sandbox/JavaSDK/CRUD/RESTCRUD.feature")
end
############################################################
# SDK Tests end
############################################################
