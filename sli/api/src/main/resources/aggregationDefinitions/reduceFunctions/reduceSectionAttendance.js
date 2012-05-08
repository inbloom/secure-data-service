reduceSectionAttendance = function(key, values) {
    var result = { attendance : {}, Total : 0 };

    values.forEach(function(value) {
        var count = result.attendance[value.attendanceEventCategory];

        if(count==undefined) {
            result.attendance[value.attendanceEventCategory] = 1;
        } else {
            ++count;
            result.attendance[value.attendanceEventCategory] = count;
        }

        result.Total += value.count;
    });

    result.attendance["Total"] = result.Total;

    return result;
};

db.system.js.save({ "_id" : "reduceSectionAttendance" , "value" : reduceSectionAttendance })
