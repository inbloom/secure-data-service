
mapDistrictPerf1to4 = function() {
    
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
    //new array to track associated districts (cannot hold duplicates)
    var districts = new Array();
    
    //list schools for student
    db.studentschoolassociation.find({"body.studentId":this.body.studentId}).forEach(
        function (ssa) {
            //list districts for school
            db.educationOrganizationschoolassociation.find({"body.schoolId":ssa.body.schoolId}).forEach(
                function (eosa) {
                    //register district as associated
                    districts[eosa.body.educationOrganizationId] = eosa.body.educationOrganizationId;
                }
            );
        }
    );
    
    //for each associated district
    for( districtId in districts) {    
        emit({"districtId":districtId,"assessmentType":aggregation_name}, values); 
    }
};
