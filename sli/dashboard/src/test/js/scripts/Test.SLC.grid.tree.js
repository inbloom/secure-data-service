/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module("SLC.grid.tree");

	test('Test SLC.grid.tree Namespace', function () {
		ok(SLC.grid.tree !== undefined, 'SLC.grid.tree namespace should be defined');
		ok(typeof SLC.grid.tree === 'object', 'SLC.grid.tree should be an object');
	});
	
module("SLC.grid.tree.create", {
	setup: function () {
		$("body").append("<table id='gridTable'></table>");
	},
	teardown: function () {
		$(".ui-jqgrid").remove();
	}
});
	
	test('Test create method', function () {
		ok(SLC.grid.tree.create !== undefined, 'SLC.grid.tree create method should be defined');
		ok(typeof SLC.grid.tree.create === 'function', 'SLC.grid.tree create method should be function');
		
		var treeData = {
		    "root":[
		        {
		            "name":"English Language and Literature",
		            "id":"English Language and Literature",
		            "level":"0",
		            "parent":"",
		            "isLeaf":false,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "dateCourseAdopted":"2000-01-01",
		            "courseLevelCharacteristics":[
		                
		            ],
		            "highSchoolCourseRequirement":false,
		            "entityType":"course",
		            "courseCode":[
		                {
		                    "identificationSystem":"CSSC course code",
		                    "ID":"7th Grade English"
		                }
		            ],
		            "id":"2012bo-2828b05d-c2e5-11e1-980a-1040f388f05e",
		            "maximumAvailableCredit":{
		                "credit":1.0
		            },
		            "subjectArea":"English Language and Literature",
		            "minimumAvailableCredit":{
		                "credit":1.0
		            },
		            "courseTitle":"7th Grade English",
		            "numberOfParts":1,
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "name":"7th Grade English",
		            "level":"1",
		            "parent":"English Language and Literature",
		            "expanded":false,
		            "loaded":true,
		            "isLeaf":false
		        },
		        {
		            "id":"2012am-28c1bc93-c2e5-11e1-980a-1040f388f05e",
		            "teacherSectionAssociation":[
		                {
		                    "id":"2012ty-29ec0b76-c2e5-11e1-980a-1040f388f05e",
		                    "sectionId":"2012am-28c1bc93-c2e5-11e1-980a-1040f388f05e",
		                    "classroomPosition":"Teacher of Record",
		                    "entityType":"teacherSectionAssociation",
		                    "teacherId":"2012vp-218c2f05-c2e5-11e1-980a-1040f388f05e"
		                }
		            ],
		            "sessionId":"2012sv-2839525b-c2e5-11e1-980a-1040f388f05e",
		            "courseOfferingId":"2012ys-28b0093a-c2e5-11e1-980a-1040f388f05e",
		            "populationServed":"Regular Students",
		            "sequenceOfCourse":3,
		            "mediumOfInstruction":"Independent study",
		            "uniqueSectionCode":"7th Grade English - Sec 5",
		            "programReference":[
		                
		            ],
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "entityType":"section",
		            "sectionName":"7th Grade English - Sec 5",
		            "name":"7th Grade English - Sec 5",
		            "level":"2",
		            "parent":"2012bo-2828b05d-c2e5-11e1-980a-1040f388f05e",
		            "isLeaf":true,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "dateCourseAdopted":"2000-01-01",
		            "courseLevelCharacteristics":[
		                
		            ],
		            "highSchoolCourseRequirement":false,
		            "entityType":"course",
		            "courseCode":[
		                {
		                    "identificationSystem":"CSSC course code",
		                    "ID":"8th Grade English"
		                }
		            ],
		            "id":"2012tg-282099fb-c2e5-11e1-980a-1040f388f05e",
		            "maximumAvailableCredit":{
		                "credit":1.0
		            },
		            "subjectArea":"English Language and Literature",
		            "minimumAvailableCredit":{
		                "credit":1.0
		            },
		            "courseTitle":"8th Grade English",
		            "numberOfParts":1,
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "name":"8th Grade English",
		            "level":"1",
		            "parent":"English Language and Literature",
		            "expanded":false,
		            "loaded":true,
		            "isLeaf":false
		        },
		        {
		            "id":"2012zx-28bfe7cf-c2e5-11e1-980a-1040f388f05e",
		            "teacherSectionAssociation":[
		                {
		                    "id":"2012er-29ebbd55-c2e5-11e1-980a-1040f388f05e",
		                    "sectionId":"2012zx-28bfe7cf-c2e5-11e1-980a-1040f388f05e",
		                    "classroomPosition":"Teacher of Record",
		                    "entityType":"teacherSectionAssociation",
		                    "teacherId":"2012vp-218c2f05-c2e5-11e1-980a-1040f388f05e"
		                }
		            ],
		            "sessionId":"2012qg-281e9e26-c2e5-11e1-980a-1040f388f05e",
		            "courseOfferingId":"2012sg-28a13c0d-c2e5-11e1-980a-1040f388f05e",
		            "populationServed":"Regular Students",
		            "sequenceOfCourse":3,
		            "mediumOfInstruction":"Independent study",
		            "uniqueSectionCode":"8th Grade English - Sec 6",
		            "programReference":[
		                
		            ],
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "entityType":"section",
		            "sectionName":"8th Grade English - Sec 6",
		            "name":"8th Grade English - Sec 6",
		            "level":"2",
		            "parent":"2012tg-282099fb-c2e5-11e1-980a-1040f388f05e",
		            "isLeaf":true,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "name":"Mathematics",
		            "id":"Mathematics",
		            "level":"0",
		            "parent":"",
		            "isLeaf":false,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "dateCourseAdopted":"2000-01-01",
		            "courseLevelCharacteristics":[
		                
		            ],
		            "highSchoolCourseRequirement":false,
		            "entityType":"course",
		            "courseCode":[
		                {
		                    "identificationSystem":"CSSC course code",
		                    "ID":"6th Grade Math"
		                }
		            ],
		            "id":"2012sh-2845aea4-c2e5-11e1-980a-1040f388f05e",
		            "maximumAvailableCredit":{
		                "credit":1.0
		            },
		            "subjectArea":"Mathematics",
		            "minimumAvailableCredit":{
		                "credit":1.0
		            },
		            "courseTitle":"6th Grade Math",
		            "numberOfParts":1,
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "name":"6th Grade Math",
		            "level":"1",
		            "parent":"Mathematics",
		            "expanded":false,
		            "loaded":true,
		            "isLeaf":false
		        },
		        {
		            "id":"2012qj-28be883d-c2e5-11e1-980a-1040f388f05e",
		            "teacherSectionAssociation":[
		                {
		                    "id":"2012sg-29ed43fa-c2e5-11e1-980a-1040f388f05e",
		                    "sectionId":"2012qj-28be883d-c2e5-11e1-980a-1040f388f05e",
		                    "classroomPosition":"Teacher of Record",
		                    "entityType":"teacherSectionAssociation",
		                    "teacherId":"2012vp-218c2f05-c2e5-11e1-980a-1040f388f05e"
		                }
		            ],
		            "sessionId":"2012sv-2839525b-c2e5-11e1-980a-1040f388f05e",
		            "courseOfferingId":"2012vg-28afbb19-c2e5-11e1-980a-1040f388f05e",
		            "populationServed":"Regular Students",
		            "sequenceOfCourse":3,
		            "mediumOfInstruction":"Independent study",
		            "uniqueSectionCode":"6th Grade Math - Sec 1",
		            "programReference":[
		                
		            ],
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "entityType":"section",
		            "sectionName":"6th Grade Math - Sec 1",
		            "name":"6th Grade Math - Sec 1",
		            "level":"2",
		            "parent":"2012sh-2845aea4-c2e5-11e1-980a-1040f388f05e",
		            "isLeaf":true,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "dateCourseAdopted":"2000-01-01",
		            "courseLevelCharacteristics":[
		                
		            ],
		            "highSchoolCourseRequirement":false,
		            "entityType":"course",
		            "courseCode":[
		                {
		                    "identificationSystem":"CSSC course code",
		                    "ID":"7th Grade Math"
		                }
		            ],
		            "id":"2012ss-284623d7-c2e5-11e1-980a-1040f388f05e",
		            "maximumAvailableCredit":{
		                "credit":1.0
		            },
		            "subjectArea":"Mathematics",
		            "minimumAvailableCredit":{
		                "credit":1.0
		            },
		            "courseTitle":"7th Grade Math",
		            "numberOfParts":1,
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "name":"7th Grade Math",
		            "level":"1",
		            "parent":"Mathematics",
		            "expanded":false,
		            "loaded":true,
		            "isLeaf":false
		        },
		        {
		            "id":"2012uo-28c258d5-c2e5-11e1-980a-1040f388f05e",
		            "teacherSectionAssociation":[
		                {
		                    "id":"2012dd-29ec5997-c2e5-11e1-980a-1040f388f05e",
		                    "sectionId":"2012uo-28c258d5-c2e5-11e1-980a-1040f388f05e",
		                    "classroomPosition":"Teacher of Record",
		                    "entityType":"teacherSectionAssociation",
		                    "teacherId":"2012vp-218c2f05-c2e5-11e1-980a-1040f388f05e"
		                }
		            ],
		            "sessionId":"2012cr-281a3151-c2e5-11e1-980a-1040f388f05e",
		            "courseOfferingId":"2012ye-289db995-c2e5-11e1-980a-1040f388f05e",
		            "populationServed":"Regular Students",
		            "sequenceOfCourse":3,
		            "mediumOfInstruction":"Independent study",
		            "uniqueSectionCode":"7th Grade Math - Sec 2",
		            "programReference":[
		                
		            ],
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "entityType":"section",
		            "sectionName":"7th Grade Math - Sec 2",
		            "name":"7th Grade Math - Sec 2",
		            "level":"2",
		            "parent":"2012ss-284623d7-c2e5-11e1-980a-1040f388f05e",
		            "isLeaf":true,
		            "expanded":false,
		            "loaded":true
		        },
		        {
		            "dateCourseAdopted":"2000-01-01",
		            "courseLevelCharacteristics":[
		                
		            ],
		            "highSchoolCourseRequirement":false,
		            "entityType":"course",
		            "courseCode":[
		                {
		                    "identificationSystem":"CSSC course code",
		                    "ID":"6th Grade English"
		                }
		            ],
		            "id":"2012lt-2823a742-c2e5-11e1-980a-1040f388f05e",
		            "maximumAvailableCredit":{
		                "credit":1.0
		            },
		            "subjectArea":"English Language and Literature",
		            "minimumAvailableCredit":{
		                "credit":1.0
		            },
		            "courseTitle":"6th Grade English",
		            "numberOfParts":1,
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "name":"6th Grade English",
		            "level":"1",
		            "parent":"English Language and Literature",
		            "expanded":false,
		            "loaded":true,
		            "isLeaf":false
		        },
		        {
		            "id":"2012wy-28c20ab4-c2e5-11e1-980a-1040f388f05e",
		            "teacherSectionAssociation":[
		                {
		                    "id":"2012cr-29eca7b8-c2e5-11e1-980a-1040f388f05e",
		                    "sectionId":"2012wy-28c20ab4-c2e5-11e1-980a-1040f388f05e",
		                    "classroomPosition":"Teacher of Record",
		                    "entityType":"teacherSectionAssociation",
		                    "teacherId":"2012vp-218c2f05-c2e5-11e1-980a-1040f388f05e"
		                }
		            ],
		            "sessionId":"2012sv-2839525b-c2e5-11e1-980a-1040f388f05e",
		            "courseOfferingId":"2012ne-28af45e8-c2e5-11e1-980a-1040f388f05e",
		            "populationServed":"Regular Students",
		            "sequenceOfCourse":3,
		            "mediumOfInstruction":"Independent study",
		            "uniqueSectionCode":"6th Grade English - Sec 4",
		            "programReference":[
		                
		            ],
		            "schoolId":"2012ms-28012b05-c2e5-11e1-980a-1040f388f05e",
		            "entityType":"section",
		            "sectionName":"6th Grade English - Sec 4",
		            "name":"6th Grade English - Sec 4",
		            "level":"2",
		            "parent":"2012lt-2823a742-c2e5-11e1-980a-1040f388f05e",
		            "isLeaf":true,
		            "expanded":false,
		            "loaded":true
		        }
		    ]
		}
		var columnItems = {
		    id : "subjectsCourses",
		    type : "TREE",
		    name : "Subjects and Courses",
		    data : {
		        cacheKey: 'treeView',
		        params: { assessmentFamily: "SAT Writing"}
		    },
		    root: "root",
		    items : [
		        {id : "subject", name : "Subject", type : "FIELD", field : "name", width : 300 }
		    ]
		}
		SLC.grid.tree.create("gridTable", columnItems, treeData);
		deepEqual($(".ui-jqgrid-htable").length, 1, 'Create method should create grid view');
	});
