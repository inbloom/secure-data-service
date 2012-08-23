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

function sli_shards() {
    return ["kelvin", "constellation", "hood", "stargazer"];
}

function discoverShards() {
    var shards = [];
    var available_shards = db.runCommand( { listShards : 1 } );
    var shard_list = available_shards["shards"];
    for (var shard in shard_list) {
        shards.push(shard_list[shard]["_id"]);
    }
    return shards;
}

function preSplit(shard_list, database_name, tenantId){
    //make sure the database is sharded
    print("Enabling sharding on database:" + database_name);
    db.runCommand( { enablesharding : database_name } );

    var a_code = "a".charCodeAt(0);
    var char_offset = Math.floor(26 / shard_list.length);

    for (var col_num in sli_collections) {
        //calculate strings
        var collection = database_name + "." + sli_collections[col_num];
        var year = new Date().getFullYear();
        var move_strings = [];

        //enable sharding on the collection
        print("Sharding collecion:" + collection);
        db.runCommand({shardcollection:collection,
                       key:{"metaData.tenantId":1, "_id":1} });

        //calculate splits and add to the moves array
        move_strings.push(year + "a");
        for (var shard_num = 1; shard_num < shard_list.length; shard_num++) {
            var split_letter = String.fromCharCode(a_code + (char_offset * shard_num));
            var split_string = year + split_letter;
            move_strings.push(split_string);

            //execute db command
            db.adminCommand({split:collection,
                             middle:{"metaData.tenantId":tenantId, "_id":split_string}
                            });
        }

        //explicitly move chunks to each shard
        for (var i in move_strings) {
            //execute db command
            db.adminCommand({moveChunk:collection,
                             find:{"metaData.tenantId":tenantId, "_id":move_strings[i]},
                             to:shard_list[i]
                            });
        }
    }
}

preSplit(discoverShards(), "sli", "Midgar");
