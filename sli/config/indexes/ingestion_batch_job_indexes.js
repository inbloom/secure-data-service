db["error"].ensureIndex({"batchJobId":1});
db["error"].ensureIndex({"resourceId":1});
db["error"].ensureIndex({"batchJobId":1,"severity":1});
db["newBatchJob"].ensureIndex({"_id":1});
db["newBatchJob"].ensureIndex({"_id":1, "stages.$.chunks.stageName":1});
db["batchJobStage"].ensureIndex({"jobId":1, "stageName":1});
