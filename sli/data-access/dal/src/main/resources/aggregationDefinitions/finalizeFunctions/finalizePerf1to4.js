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



finalizePerf1to4 = function(key,value) {
    
    //determine total number of values
    var total = value.level1 + value.level2 + value.level3 + value.level4;
    
    //compute percentages, rounded to 1 decimal place
    var level1percentage = Math.round(10*value.level1*100.0/total)/10;
    var level2percentage = Math.round(10*value.level2*100.0/total)/10;
    var level3percentage = Math.round(10*value.level3*100.0/total)/10;
    var level4percentage = Math.round(10*value.level4*100.0/total)/10;
    
    return {
        ts: execution_time,
        level1:value.level1,
        level2:value.level2,
        level3:value.level3,
        level4:value.level4,
        level1percentage:level1percentage,
        level2percentage:level2percentage,
        level3percentage:level3percentage,
        level4percentage:level4percentage,
        groupBy: key,
        raw:"true"
    }; 
    
};

db.system.js.save({ "_id" : "finalizePerf1to4", "value" : finalizePerf1to4 })
