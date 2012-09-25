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

// These collections use the old style doc Ids to split on.
var collections_docId = ["assessment","attendance","calendarDate","cohort","competencyLevelDescriptor","course","courseOffering","courseSectionAssociation","disciplineAction","disciplineIncident","educationOrganization","educationOrganizationAssociation","educationOrganizationSchoolAssociation","grade","gradebookEntry","gradingPeriod","graduationPlan","learningObjective","learningStandard","parent","program","reportCard","section","sectionAssessmentAssociation","sectionSchoolAssociation","session","sessionCourseAssociation","staff","staffCohortAssociation","staffEducationOrganizationAssociation","staffProgramAssociation","student","studentAcademicRecord","studentAssessmentAssociation","studentCohortAssociation","studentCompetency","studentCompetencyObjective","studentDisciplineIncidentAssociation","studentParentAssociation","studentProgramAssociation","studentSectionAssociation","studentGradebookEntry","studentSchoolAssociation","studentTranscriptAssociation","teacherSchoolAssociation","teacherSectionAssociation"];

// These collections use the new deterministic (hash) id
var collections_hashId = [""];

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

function preSplit_docId(shard_list, database_name, tenantId, num_years){
    //make sure the database is sharded
    print("Enabling sharding on database:" + database_name);
    db.runCommand( { enablesharding : database_name } );

    var a_code = "a".charCodeAt(0);
    var char_offset = Math.floor(26 / shard_list.length);

    for (var col_num in collections_docId) {
        //calculate strings
        var collection = database_name + "." + collections_docId[col_num];
        var this_year = new Date().getFullYear();

        //enable sharding on the collection
        print("Sharding collecion:" + collection);
        db.runCommand({shardcollection:collection,
                       key:{"metaData.tenantId":1, "_id":1} });

        for (var year_num = this_year; year_num < this_year+num_years; year_num++) {
        	
            var move_strings = [];
        	year = year_num;
            //calculate splits and add to the moves array
            move_strings.push(year + "a");
            for (var shard_num = 1; shard_num <= shard_list.length; shard_num++) {
                if (shard_num == shard_list.length) {
                	var split_letter = "|";
                	var split_string = year + split_letter;
                } else {
                    var split_letter = String.fromCharCode(a_code + (char_offset * shard_num));
                    var split_string = year + split_letter;              	
                }
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
        //explicitly add end point at beginning of range
        var split_string = this_year + " ";
        db.adminCommand({split:collection,
            middle:{"metaData.tenantId":tenantId, "_id":split_string}
           });        
        
        //explicitly add an end split at 'last_year + "|"'
        this_year = this_year + num_years - 1;
        var split_string = this_year + "|";
        db.adminCommand({split:collection,
            middle:{"metaData.tenantId":tenantId, "_id":split_string}
           });        
    	

    }
}

function preSplit_hashId(shard_list, database_name, tenantId, num_years){
    //make sure the database is sharded
    print("Enabling sharding on database:" + database_name);
    db.runCommand( { enablesharding : database_name } );
	
    var char_offset = Math.floor(256 / shard_list.length);

    for (var col_num in collections_hashId) {
        //calculate strings
        var collection = database_name + "." + collections_hashId[col_num];

        //enable sharding on the collection
        print("Sharding collecion:" + collection);
        db.runCommand({shardcollection:collection,
                       key:{"metaData.tenantId":1, "_id":1} });
        	
        //calculate splits and add to the moves array
        var move_strings = [];
        move_strings.push("00");
        for (var shard_num = 1; shard_num <= shard_list.length; shard_num++) {
            if (shard_num == shard_list.length) {
            	var split_string = "  ";
            } else {
            	var split_string = (char_offset * shard_num).toString(16);
            }
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
        
        //explicitly add end point at beginning of range
        var split_string = "  ";
        db.adminCommand({split:collection,
            middle:{"metaData.tenantId":tenantId, "_id":split_string}
           });        
        
        //explicitly add an end split at 'year + 1 + "a"'
        //since 'year + "z"' potentially cuts off some records
        var split_string = "||";
        db.adminCommand({split:collection,
            middle:{"metaData.tenantId":tenantId, "_id":split_string}
           });        
    	
    }
}

// tenant and num_years are passed in when the script is called
// num_years will become obsolete when all of the entities
// are switched to deterministic ids, at which point it
// should be permanently set to '1'
preSplit_docId(discoverShards(), "sli", tenant, num_years);
preSplit_hashId(discoverShards(), "sli", tenant, num_years);
sh.setBalancerState(false);
