//mongo localhost:27017/<database_name> expectedCounts.js
var entities ={
              'assessment':19,
              'attendance':81,
              'calendarDate':556,
              'cohort':3,
              'compentencyLevelDescriptor':0,
              'course':95,
              'courseOffering':95,
              'courseTranscript':196,
              'disciplineAction':2,
              'disciplineIncident':2,
              'educationOrganization':5,
              'gradingPeriod':17,
              'graduationPlan':0,
              'learningObjective':257,
              'learningStandard':1509,
              'parent':9,
              'program':2,
              'section':97,
              'session':22,
              'staff':14,
              'staffCohortAssociation':3,
              'staffProgramAssociation':7,
              'student':78,
              'studentCompetency':59,
              'studentCompetencyObjective':4,
              'studentGradebookEntry':315,
              'teacherSchoolAssociation':3,
};
var superdocs = {
    'cohort':{
        'studentCohortAssociation':6,
    },
    'program':{
        'studentProgramAssociation':6,
    },
    'section':{
        'gradebookEntry':12,
        'studentSectionAssociation':325,
        'teacherSectionAssociation':11,
    },
    'student':{
        'schools':167,
        'studentDisciplineIncidentAssociation':4,
        'studentParentAssociation':9,
    },
    'yearlyTranscript':{
        'grade':4,
        'reportCard':2,
        'studentAcademicRecord':117
    }
}
for(var entity in  entities){
    var expectedCount = entities[entity];
    var mongoCount = db[entity].find().count();
    if(expectedCount != mongoCount) {
        print ("                                  >"  + entity + "[" + expectedCount + "/" + mongoCount + "] Mismatch *"); 
    } else {
        print (entity + "[" + expectedCount + "/" + mongoCount + "] Match *"); 
    }
}
print("\nChecking sub-document counts:");
for (var superdoc in superdocs) {
    var fields = superdocs[superdoc];
    var expectedCounts = {};
    var mongoCounts = {};
    for (var field in fields) {
        expectedCounts[field] = fields[field];
        mongoCounts[field] = 0;
    }
    db[superdoc].find().forEach( function(x){
        for (var field in fields) {
            if (field in x) {
                mongoCounts[field] += x[field].length;
            }
        }
    })
    for (var field in fields) {
        var s = superdoc + "." + field + "[" + expectedCounts[field] + "/" + mongoCounts[field] + "]";
        if (expectedCounts[field] != mongoCounts[field]) {
            print ("                                  >"  + s + "Mismatch *"); 
        } else {
            print ("  " + s + " Match *"); 
        }
    }
}

