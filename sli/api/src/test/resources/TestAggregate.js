//Make sure to load uuid.js and uuidhelpers.js into mongo before running this!

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

var cleanupBodyAndId = function() {
    //list aggregates needing cleanup
    db.aggregation.find({"value.raw":"true"}).forEach(
        function (data) {
            var old_id = data._id;
            var doc = data;
            
            doc._id = JUUID(UUID.generate());
            doc.type = "aggregation";
            doc.body = doc.value;

            delete doc.value;
            delete doc.body.raw;
            
            db.aggregation.insert(doc);
            db.aggregation.remove({ _id: old_id});
        }
    );
}

db.aggregation.drop()

var mapFunction = mapDistrictPerf1to4;
var reduceFunction = reducePerf1to4;
var finalizeFunction = finalizePerf1to4;
var collectionName = "aggregation";
var aggregationName = "8th Grade EOG";

db.runCommand( { 
    mapreduce:"studentassessmentassociation",
    map:mapFunction,
    reduce:reduceFunction,
    finalize:finalizeFunction,
    query: {"body.assessmentId":{$in: ["67ce204b-9999-4a11-bfea-000000004682","67ce204b-9999-4a11-bfea-000000004683","67ce204b-9999-4a11-bfea-000000004684"]}},
    scope: { aggregation_name : aggregationName, execution_time : new Date() },
    out: { reduce: collectionName }
});

cleanupBodyAndId();

db.aggregation.find()



