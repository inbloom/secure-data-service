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


mapSectionAttendance = function() {
    var savedStudentId = this.body.studentId;

    db.attendance.find({"body.studentId":savedStudentId}).forEach(
        function(attendanceDoc) {
            emit(attendanceDoc.body.studentId, { attendanceEventCategory : attendanceDoc.body.attendanceEventCategory, count : 1 });
        }
    );

};

db.system.js.save({ "_id" : "mapSectionAttendance" , "value" : mapSectionAttendance })
