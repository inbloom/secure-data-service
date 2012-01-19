
var cleanupBodyAndId = function() {  
    //list aggregates needing cleanup
    db.aggregation.find({"value.raw":"true"}).forEach(
        function (data) {
            var old_id = data._id;
            var doc = data;
			
            doc._id = makeID(generateUUID());
            doc.type = "aggregation";
            doc.body = doc.value;

            delete doc.value;
            delete doc.body.raw;

            db.aggregation.insert(doc);
            db.aggregation.remove({ _id: old_id});
        }
    );
}

db.system.js.save({ "_id" : "cleanupBodyAndId", "value" : cleanupBodyAndId })