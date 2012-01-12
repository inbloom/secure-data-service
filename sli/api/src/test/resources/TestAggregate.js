

mapFunction = function() {
    var level1 = level2 = level3 = level4 = 0;
    switch(parseInt(this.body.performanceLevel))
    {
        case 1: level1++; break;
        case 2: level2++; break;
        case 3: level3++; break;
        case 4: level4++; break;
        default: break;
    }
    
    var values = {level1:level1,level2:level2,level3:level3,level4:level4};
    var studentId = this.body.studentId;
    var ssaArray = db.studentschoolassociation.find({"body.studentId":studentId}).toArray();
    var districts = new Array();
    
    for( var i=0; i<ssaArray.length; i++) {    
        var schoolId = ssaArray[i].body.schoolId;
        emit({"schoolId":schoolId,"assessmentType":aggregation_name}, values); 
        
        var eosaArray = db.educationOrganizationschoolassociation.find({"body.schoolId":schoolId}).toArray();
        
        for( var j=0; j<eosaArray.length; j++) {    
            districts[eosaArray[j].body.educationOrganizationId] = eosaArray[j].body.educationOrganizationId;
        }
    }
    
    for( districtId in districts) {    
        emit({"districtId":districtId,"assessmentType":aggregation_name}, values); 
    }
};

reduceFunction = function(key,values) {
    var level1total = level2total = level3total = level4total = 0;
    
    for( var i=0; i<values.length; i++) {    
        level1total+=values[i].level1;
        level2total+=values[i].level2;
        level3total+=values[i].level3;
        level4total+=values[i].level4;
    }
    
    return {level1:level1total,level2:level2total,level3:level3total,level4:level4total}; 
    
};

finalizeFunction = function(key,value) {
    
    var total = value.level1 + value.level2 + value.level3 + value.level4;
    
    var level1percentage = value.level1*100.0/total;
    var level2percentage = value.level2*100.0/total;
    var level3percentage = value.level3*100.0/total;
    var level4percentage = value.level4*100.0/total;
    
    return {
        ts: new Date(),
        level1:value.level1,
        level2:value.level2,
        level3:value.level3,
        level4:value.level4,
        level1percentage:Math.round(level1percentage*10)/10,
        level2percentage:Math.round(level2percentage*10)/10,
        level3percentage:Math.round(level3percentage*10)/10,
        level4percentage:Math.round(level4percentage*10)/10,
        groupBy: key,
        raw:"true"
    }; 
    
};


var cleanUp = function() {
    db.myoutput.find({"value.raw":"true"}).forEach(function (data) {
        var old_id = data._id;
        var doc = data;
        doc._id = new BinData(3, new ObjectId());
        doc.type = "aggregate";
        doc.body = doc.value;
        delete doc.value;
        delete doc.body.raw;
        
        db.myoutput.insert(doc);
        db.myoutput.remove({ _id: old_id});
    });
}


db.myoutput.drop()

db.runCommand( { mapreduce:"studentassessmentassociation",
                                                      map:mapFunction,
                                                      reduce:reduceFunction,
                                                      finalize:finalizeFunction,
                                                      query: {"body.assessmentId":{$in: ["67ce204b-9999-4a11-bfea-000000004682","67ce204b-9999-4a11-bfea-000000004683","67ce204b-9999-4a11-bfea-000000004684"]}},
                                                      scope: { aggregation_name : "8th Grade EOG" },
                                                      out: { reduce: "myoutput" }
                                                    });
cleanUp();
db.myoutput.find()



