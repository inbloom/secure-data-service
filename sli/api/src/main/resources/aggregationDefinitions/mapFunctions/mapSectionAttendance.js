mapSectionAttendance = function() {
    var savedStudentId = this.body.studentId;

    db.attendance.find({"body.studentId":savedStudentId}).forEach(
        function(attendanceDoc) {
            emit(attendanceDoc.body.studentId, { attendanceEventCategory : attendanceDoc.body.attendanceEventCategory, count : 1 });
        }
    );

};

db.system.js.save({ "_id" : "mapSectionAttendance" , "value" : mapSectionAttendance })
