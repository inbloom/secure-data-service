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
    
    //iterate through the student section assoctions that matches the studentId from the map collection
    db.studentsectionassociation.find({"body.studentId":this.body.studentId}).forEach(
    	function(studSecAssoc) {
    		//iterate through the section assessment association that matches the assessment from the map collection
    		db.sectionassessmentassociation.find({"body.assessmentId":this.body.assessmentId}).forEach(
    			function(secAssessAssoc) {
    				//iterate through the teacher section association that matches the section that includes the assessment 
    				//we are looking for
    				db.teachersectionassociation.find({"body.sectionId":secAssessAssoc.body.sectionId}).forEach(
    					function(teachSecAssoc) {
    						//emit the map 
    						emit({"teacherId":teachSecAssoc.body.teacherId,"assessmentType":aggregation_name}, values);
    					}
    				);
    			}
    		);
    	}
    );

}