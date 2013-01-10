
var aggregates = [];
var keys = {};
var keyArray = [];

db.ingestionStatsResult.find({"value.A1-job-size":{$gt:1}}).forEach(aggregateProcessor);

function aggregateProcessor(aggregate) {
	aggregates.push(aggregate.value);
	for(var k in aggregate.value) {
		keys[k] = k;
	}
}

for(var k in keys){
	if(k != "Avg-_id"                       && 
			k != "Avg-a1-job-id"       && 
			k != "Avg-a1-job-file"     && 
			k.match("-ctl$") ==null && 
			k.match("-zip$") ==null) 
	keyArray.push(k);
}

aggregates.sort(function(a,b){return parseInt(a["A1-job-size"]) - parseInt(b["A1-job-size"] )});
keyArray.sort();
for(var i = 0; i < keyArray.length; i++) {
	var k = keyArray[i];
	var row = [];
	row.push(k);
	var last = "";
	var lastChange = 0;
	for(var j = 0; j < aggregates.length; j++){
		var v = aggregates[j][k];
		row.push(v);

		if(last != v) lastChange++;
		last = v;
	}
	if(lastChange > 1) row.unshift(1); else row.unshift(0);

	print (row.join(','));
}


