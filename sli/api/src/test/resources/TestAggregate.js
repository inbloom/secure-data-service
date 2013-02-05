/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



//clear all previous aggregation data, this is for testing/demo only
db.aggregation.drop();

//all of these strings should come from the aggregationdefinition table
var inputCollectionName = "studentAssessment";
var mapFunction = db.system.js.findOne({"_id":"mapDistrictPerf1to4"}).value;
var reduceFunction = db.system.js.findOne({"_id":"reducePerf1to4"}).value;
var finalizeFunction = db.system.js.findOne({"_id":"finalizePerf1to4"}).value;
var cleanupFunction = db.system.js.findOne({"_id":"cleanupBodyAndId"}).value;
var outputCollectionName = "aggregation";
var aggregationName = "8th Grade EOG";
var params = ["67ce204b-9999-4a11-aaaa-000000000002", "67ce204b-9999-4a11-aaaa-000000000001","67ce204b-9999-4a11-aaaa-000000000000"];
var queryField = "body.assessmentId";
var query = {};
query[queryField] = {$in:params};

//execute aggregation
db.runCommand( { 
    mapreduce:inputCollectionName,
    map:mapFunction,
    reduce:reduceFunction,
    finalize:finalizeFunction,
    query: query,
    scope: { aggregation_name : aggregationName, execution_time : new Date() },
    out: { reduce: outputCollectionName }
});

//clean up results so they can be found properly by the API's handlers
db.runCommand({"$eval" : cleanupFunction });

//show results
db.aggregation.find();
