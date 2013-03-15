//mongo localhost:27017/<database_name> expectedCounts.js
var entities ={
              'assessment':1,
              'attendance':500,
              'calendarDate':20,
              'cohort':20,
              'compentencyLevelDescriptor':0,
              'course':50,
              'courseOffering':100,
              'courseTranscript':7500,
              'disciplineAction':4875,
              'disciplineIncident':4875,
              'educationOrganization':7,
              'gradingPeriod':40,
              'graduationPlan':5,
              'learningObjective':1,
              'learningStandard':3,
              'parent':763,
              'program':13,
              'section':300,
              'session':10,
              'staff':60,
              'staffCohortAssociation':180,
              'staffProgramAssociation':106,
              'student':500,
              'studentCompetency':1000,
              'studentCompetencyObjective':1,
              'studentGradebookEntry':8,
              'teacherSchoolAssociation':50,
};
var superdocs = {
    'cohort':{
        'studentCohortAssociation':1500,
    },
    'program':{
        'studentProgramAssociation':1000,
    },
    'section':{
        'gradebookEntry':1,
        'studentSectionAssociation':2500,
        'teacherSectionAssociation':300,
    },
    'student':{
        'schools':500,
        'studentDisciplineIncidentAssociation':4875,
        'studentParentAssociation':763,
    },
    'yearlyTranscript':{
        'grade':2500,
        'reportCard':500,
        'studentAcademicRecord':500
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

