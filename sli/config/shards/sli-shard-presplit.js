//
// Copyright 2012 Shared Learning Collaborative, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

var sli_collections = ["assessment","attendance","calendarDate","cohort","competencyLevelDescriptor","course","courseOffering","courseSectionAssociation","disciplineAction","disciplineIncident","educationOrganization","educationOrganizationAssociation","educationOrganizationSchoolAssociation","grade","gradebookEntry","gradingPeriod","graduationPlan","learningObjective","learningStandard","parent","program","reportCard","section","sectionAssessmentAssociation","sectionSchoolAssociation","session","sessionCourseAssociation","staff","staffCohortAssociation","staffEducationOrganizationAssociation","staffProgramAssociation","student","studentAcademicRecord","studentAssessmentAssociation","studentCohortAssociation","studentCompetency","studentCompetencyObjective","studentDisciplineIncidentAssociation","studentParentAssociation","studentProgramAssociation","studentSectionAssociation","studentGradebookEntry","studentSchoolAssociation","studentTranscriptAssociation","teacherSchoolAssociation","teacherSectionAssociation"];

var sli_shards = ["kelvin", "constellation", "hood", "stargazer"]

db.runCommand( { enablesharding : "sli" } );

function preSplit(tenantId){
    for (var i in sli_collections) {
        var collection = "sli." + sli_collections[i];

        //shard collection
        db.runCommand({shardcollection:collection,
                       key:{"metaData.tenantId":1, "_id":1} });

        //assume n = 4 shards
        //split collection into n chunks
        db.adminCommand({split:collection,
                         middle:{"metaData.tenantId":tenantId, "_id":"2012f"}
                        });
        db.adminCommand({split:collection,
                         middle:{"metaData.tenantId":tenantId, "_id":"2012m"}
                        });
        db.adminCommand({split:collection,
                         middle:{"metaData.tenantId":tenantId, "_id":"2012r"}
                        });

        //move the chunks to specific shards
        db.adminCommand({moveChunk:collection,
                         find:{"metaData.tenantId":tenantId, "_id":"2012a"},
                         to:sli_shards[0]
                        });
        db.adminCommand({moveChunk:collection,
                         find:{"metaData.tenantId":tenantId, "_id":"2012f"},
                         to:sli_shards[1]
                        });
        db.adminCommand({moveChunk:collection,
                         find:{"metaData.tenantId":tenantId, "_id":"2012m"},
                         to:sli_shards[2]
                        });
        db.adminCommand({moveChunk:collection,
                         find:{"metaData.tenantId":tenantId, "_id":"2012r"},
                         to:sli_shards[3]
                        });
    }
}

preSplit("MIDGAR")
