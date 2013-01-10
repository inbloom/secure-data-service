//mongo localhost:27017/sli jsExpected.js 
//cut -d ',' -f 2 errors.txt  |sort |uniq  |wc -l
var entities ={
              'assessment':1,
              'attendance':500,
              //'calendarDate':20,
              'cohort':20,
              //'compentencyLevelDescriptor':2,
              'course':50,
              'disciplineAction':4875,
              'disciplineIncident':4875,
              'educationOrganization':7,
              'grade':2500,
              'gradebookEntry':1,
              'gradingPeriod':40,
              'graduationPlan':5,
              'learningObjective':1,
              'learningStandard':3,
              'parent':763,
              'program':13,
              'reportCard':500,
              'section':300,
              'session':10,
              'staff':60,
              'staffCohortAssociation':20,
              'staffProgramAssociation':13,
              'student':500,
              'studentAcademicRecord':500,
              'studentAssessmentAssociation':2500,
              'studentCohortAssociation':1500,
              'studentCompetency':1000,
              //'studentCompetencyObjective':1,
              'studentDisciplineIncidentAssociation':4875,
              'studentGradebookEntry':500,
              'studentParentAssociation':763,
              'studentProgramAssociation':1000,
              'studentSchoolAssociation':500,
              'studentSectionAssociation':2500,
              'studentTranscriptAssociation':7500,
              'teacherSchoolAssociation':50,
              'teacherSectionAssociation':300,
};

var errorCount    = 0;
var errorEntities = {};
db.tenant.find({"body.tenantId":{$regex:".*LTEST.*"}}, {"body.tenantId":1}).forEach(function fx(tenant) {
    //for(var i = 0; i < tenantIds.count(); i++) {
        var tenantId                     =tenant["body"]["tenantId"];
        for(var entity in  entities){
            var criteria                     ={};
            criteria["metaData.tenantId"]    = tenantId;
            var expectedCount            =entities[entity];
            var mongoCount               = db[entity].find(criteria).count();
            var csv                      = [];
            if(mongoCount == expectedCount) {
                csv.push("ok");
            } else {
                errorCount++;
                errorEntities[entity]    = 1;
                csv.push("error");
            }
            csv.push(tenantId);
            csv.push(entity);
            csv.push("expected:" + expectedCount  + "/got:" + mongoCount );
            print(csv.join(","));
        }
    //}
});
print("errorCount:" + errorCount);
for(var e in errorEntities) {print (e);}
