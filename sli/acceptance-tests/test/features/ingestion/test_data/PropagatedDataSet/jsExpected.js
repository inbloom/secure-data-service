//mongo localhost:27017/sli --eval 'var tenant="MegatronIL";' jsExpected.js
var criteria = {};
if( typeof tenant  != 'undefined' 
       &&  tenant  != null 
       &&  tenant.length > 0) 
    criteria['metaData.tenantId']=tenant;
var entities ={
              'assessment':4,
              'attendance':16,
              'calendarDate':0,
              'cohort':32,
              'compentencyLevelDescriptor':2,
              'course':16,
              'disciplineAction':16,
              'disciplineIncident':16,
              'educationOrganization':14,
              'grade':64,
              'gradebookEntry':2,
              'gradingPeriod':64,
              'graduationPlan':16,
              'learningObjective':66,
              'learningStandard':16,
              'parent':25,
              'program':28,
              'reportCard':32,
              'section':64,
              'session':16,
              'staff':20,
              'staffCohortAssociation':80,
              'staffProgramAssociation':84,
              'student':16,
              'studentAcademicRecord':16,
              'studentAssessmentAssociation':32,
              'studentCohortAssociation':46,
              'studentCompetency':128,
              'studentCompetencyObjective':4,
              'studentDisciplineIncidentAssociation':16,
              'studentGradebookEntry':2,
              'studentParentAssociation':25,
              'studentProgramAssociation':14,
              'studentSchoolAssociation':16,
              'studentSectionAssociation':32,
              'studentTranscriptAssociation':32,
              'teacherSchoolAssociation':16,
              'teacherSectionAssociation':64,
};
for(var entity in  entities){
    var expectedCount = entities[entity];
    var mongoCount = db[entity].find(criteria).count();
    if(expectedCount != mongoCount)
        print ("                                  >"  + entity + "[" + expectedCount + "/" + mongoCount + "] Mismatch *"); 
    else
        print (entity + "[" + expectedCount + "/" + mongoCount + "] Match *"); 
}
