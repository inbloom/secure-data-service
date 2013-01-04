db.student.update( {"body.favoriteColor" : {$exists :true} }, { $unset: { "body.favoriteColor" : 1 } }, false, true);
db.student.update( {"metaData.version" : 2}, { $set : {"metaData.version" : NumberInt(1)}}, false, true)
