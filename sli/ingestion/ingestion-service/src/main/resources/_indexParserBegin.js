//mongo localhost:27017/sli indexParser.js
//US3478:Add index validation check to ingestion startup (Stretch)

var matching    = [];
var nonMatching = [];

var database = db;
function Checker(collection){
	this.cname  = collection;
	this.ensureIndex = function(index) {
		var matched = null;
		var a = []; for(var x in index)a.push(x + ':' + index[x]);a.sort();a=a.join(',');
		var indexes = database[this.cname].getIndexes();
		for(var i = 0; i < indexes.length; i++) {
			var thisIndex  = indexes[i].key;
			var b = []; for(var x in thisIndex) 
				b.push(x + ':' + thisIndex[x]);
			b.sort();b=b.join(',');
			if(a == b) {
				matched = b;
				break;
			} else {
			}
		}
		if(matched != null) {
		    //print("Matched     [" + this.cname + "] [" + a + "]");
		    matching.push("Matched     [" + this.cname + "] [" + a + "]");
		} else {
		    //print("Not Matched [" + this.cname + "] [" + a + "]");
		    nonMatching.push("UnMatched     [" + this.cname + "] [" + a + "]");
		}

	}
	return this;
}
var db = {};
var collections = database.getCollectionNames();
for(var i = 0; i < collections.length; i++) {
	var collection = collections[i];
	db[collection] = new Checker(collection);
}

try {
