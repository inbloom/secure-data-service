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


mapTeacherPerf1to4 = function() {
	//initialize result to all zeroes
    var level1 = level2 = level3 = level4 = 0;
    //increment variable representing this record's performance level
    switch(parseInt(this.body.performanceLevel))
    {
        case 1: level1++; break;
        case 2: level2++; break;
        case 3: level3++; break;
        case 4: level4++; break;
        default: break;
    }

    //create new result
    var values = {level1:level1,level2:level2,level3:level3,level4:level4};
    //save the assessmentId
    var assessmentId = this.body.assessmentId;
    
    //iterate through the student section assoctions that matches the studentId from the map collection
    db.studentSectionAssociation.find({"body.studentId":this.body.studentId}).forEach(
    	function(studSecAssoc) {
    		//iterate through the section assessment association that matches the assessment from the map collection
    		db.sectionAssessmentAssociation.find({"body.assessmentId":assessmentId, "body.sectionId":studSecAssoc.body.sectionId}).forEach(
    			function(secAssessAssoc) {
    				//iterate through the teacher section association that matches the section that includes the assessment 
    				//we are looking for
    				db.teacherSectionAssociation.find({"body.sectionId":secAssessAssoc.body.sectionId}).forEach(
    					function(teachSecAssoc) {
    						//emit the map 
    						emit({"teacherId":teachSecAssoc.body.teacherId,"assessmentType":aggregation_name}, values);
    					}
    				);
    			}
    		);
    	}
    );
};

db.system.js.save({ "_id" : "mapTeacherPerf1to4" , "value" : mapTeacherPerf1to4 })
