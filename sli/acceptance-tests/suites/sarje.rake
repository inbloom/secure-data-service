desc "Run Search Tests"
task :sarjeTests do
  TODROP = true
  DB_NAME = "eventbus"
  setFixture("jobs", "test.json", "../sarje/eventbus/test/data", true)
  TODROP = false
  DB_NAME = "sli"
  # Wait until scheduler populates subscription into activemq, which happens every 5s
  sleep 12 
  Rake::Task["importUnifiedData"].execute
  runTests("test/features/sarje/queue_binding.feature")
end
