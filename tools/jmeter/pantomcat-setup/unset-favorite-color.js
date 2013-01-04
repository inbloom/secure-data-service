db.student.update( {"body.favoriteColor" : {$exists :true} }, { $unset: { "body.favoriteColor" : 1 } }, false, true);
