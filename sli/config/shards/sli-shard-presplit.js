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

// The canonical list of sharded entity collections in tenant databases.
// (The following low cardinality collections have been removed from the shard list. 
//  This list may evolve with super doc and other efforts:
//  "assessment","calendarDate","competencyLevelDescriptor","course","courseOffering","educationOrganization","gradingPeriod","learningObjective","learningStandard","program","session")
// 

var sharded_collections = [
    "attendance",
    "cohort",
    "courseSectionAssociation",
    "disciplineAction",
    "disciplineIncident",
    "educationOrganizationAssociation",
    "educationOrganizationSchoolAssociation",
    "grade",
    "gradebookEntry",
    "graduationPlan",
    "parent",
    "reportCard",
    "section",
    "staff",
    "staffCohortAssociation",
    "staffEducationOrganizationAssociation",
    "staffProgramAssociation",
    "student",
    "studentAcademicRecord",
    "studentCohortAssociation",
    "studentCompetency",
    "studentCompetencyObjective",
    "studentDisciplineIncidentAssociation",
    "studentGradebookEntry",
    "studentParentAssociation",
    "studentProgramAssociation",
    "studentSchoolAssociation",
    "courseTranscript",
    "teacherSchoolAssociation",
    "teacherSectionAssociation"
];

function discoverShards() {
    var shards = [];
    var available_shards = db.runCommand( { listShards : 1 } );
    var shard_list = available_shards["shards"];
    for (var shard in shard_list) {
        print("Shard: " + shard);
        shards.push(shard_list[shard]["_id"]);
    }
    return shards;
}

function preSplit_hashId(shard_list, tenant){
    //make sure the database is sharded
//    print("Enabling sharding on database:" + tenant);
    db.runCommand( { enablesharding : tenant } );

    var char_offset = Math.floor(256 / shard_list.length);

    for (var col_num in sharded_collections) {
        //calculate strings
        var collection = tenant + "." + sharded_collections[col_num];

        //enable sharding on the collection
//        print("Sharding collecion:" + collection);
        db.runCommand({shardcollection:collection,
                       key:{"_id":1} });

        //calculate splits and add to the moves array
        var move_strings = [];
        move_strings.push("00");
        for (var shard_num = 1; shard_num <= shard_list.length; shard_num++) {
            var split_string;
            if (shard_num == shard_list.length) {
                split_string = "  ";
            } else {
                split_string = (char_offset * shard_num).toString(16);
            }
            move_strings.push(split_string);

            //execute db command
            db.adminCommand({split:collection,
                             middle:{"_id":split_string}
                            });
        }
        //explicitly move chunks to each shard
        for (var i in move_strings) {
            //execute db command
            db.adminCommand({moveChunk:collection,
                             find:{"_id":move_strings[i]},
                             to:shard_list[i]
                            });
        }

        //explicitly add end point at beginning of range
        var start_split_string = "  ";
        db.adminCommand({split:collection,
            middle:{"_id":start_split_string}
           });

        //explicitly add an end split at 'year + 1 + "a"'
        //since 'year + "z"' potentially cuts off some records
        var end_split_string = "||";
        db.adminCommand({split:collection,
            middle:{"_id":end_split_string}
           });

    }
}

// tenant is passed in when the script is called
preSplit_hashId(discoverShards(), tenant);
sh.setBalancerState(false);
