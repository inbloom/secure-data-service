var c = db.tenant.find({'dbName': { $exists : true }});
do {
	if(c.hasNext()) {
        var doc = c.next();
        doc.body.dbName = doc.dbName;
        delete doc.dbName;
        db.tenant.update({_id : doc._id}, doc);
   }
} while ( c.hasNext());