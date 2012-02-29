db.attendanceEvent.ensureIndex({ "body.eventDate" : 1, "body.studentId" : 1, "body.attendanceEventCategory" : 1 },{ "unique" : true });
