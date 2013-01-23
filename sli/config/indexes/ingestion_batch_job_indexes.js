//
// Copyright 2012 Shared Learning Collaborative, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


db["error"].ensureIndex({"batchJobId":1, "resourceId":1});
db["newBatchJob"].ensureIndex({"jobStartTimestamp":1}); // only for job reporting tool
db["batchJobStage"].ensureIndex({"jobId":1, "stageName":1});
db["transformationLatch"].ensureIndex({"jobId" : 1, "syncStage" : 1, "recordType" : 1}, {unique : true});
db["persistenceLatch"].ensureIndex({"jobId" : 1, "syncStage" : 1, "entities" : 1}, {unique : true});
db["stagedEntities"].ensureIndex({"jobId" : 1}, {unique : true});
db["fileEntryLatch"].ensureIndex({"batchJobId" : 1}, {unique : true});
