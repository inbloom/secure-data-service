mapSchoolPerf1to4 = function() {
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

    //new array to track associated schools (cannot hold duplicates)
    var schools = new Array();

	db.studentschoolassociation.find({"body.studentId" : this.body.studentId}).forEach(
		function(ssa) {
			schools[ssa.body.schoolId] = ssa.body.schoolId;
		}
	);
	
	for (schoolId in schools) {
		emit({"schoolId" : schoolId, "assessmentType" : aggregation_name}, values);
	}
};

db.system.js.save({ "_id" : "mapSchoolPerf1to4" , "value" : mapSchoolPerf1to4 })