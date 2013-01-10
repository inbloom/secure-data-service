// mongo localhost:27017/ingestion_batch_job stressAggregate.js

var res = db.runCommand({
    "mapreduce":      "ingestionStats",
    "map"      :       map,
    "reduce"   :       reduce,
//    "finalize" :       finalize,
    "out"      :       "ingestionStatsResult",
    "jsMode"   :       true,
    "verbose"  :       true
});


function map() {
	try {
		if(!this["a1-job-file"].match(/purge/))
		emit(this["a1-job-file"], this);
	} catch (e) {
		print ("exception " + e);
	}
}

function reduce(key, rows) {
	var data = {'A1-job-key':key, 'A1-job-size':rows.length};
	try { 
		var keys = {};
		for(var r = 0; r < rows.length; r++) {
			var row = rows[r];
			for(var c in row) {
				if(row[c] != null)
				if(!isNaN(parseFloat(row[c])) ) {
					keys[c] = c;
				}
				else {
				}
			}
		}
		for (var c in keys) {
			var s = 0;
			var i = 0;
			for (var r = 0; r < rows.length; r++) {
				var row   = rows[r];
				var value = parseFloat(row[c]);
				if(!isNaN(value)) {
					s+= value;
					i++;
				}
			}
			if(i != 0) {
				data['Avg-' + c] = Math.ceil(s/i);
			}
		}
	} catch(exception) {
		print ("exception " + exception);
	}
	return data;
}

function finalize(key, value) {
}
