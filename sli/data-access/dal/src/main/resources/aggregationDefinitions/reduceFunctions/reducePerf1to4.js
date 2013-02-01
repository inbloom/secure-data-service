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



reducePerf1to4 = function(key,values) {
    
    //initialize sum variables to zero
    var level1total = level2total = level3total = level4total = 0;
    
    //for each value provided
    for( var i=0; i<values.length; i++) {    
        //add each value to sum
        level1total+=values[i].level1;
        level2total+=values[i].level2;
        level3total+=values[i].level3;
        level4total+=values[i].level4;
    }
    
    //return sum document
    return {level1:level1total,level2:level2total,level3:level3total,level4:level4total}; 
};

db.system.js.save({ "_id" : "reducePerf1to4" , "value" : reducePerf1to4 })
