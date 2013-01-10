// mongo localhost:27017/ingestion_batch_job jobReport.js
var stages   = {};
var rpsStats = [];
var csv      = [];
var jCount   = 0;
db.newBatchJob.find().forEach(batchJob);

//poor man's Map
function get(name, param) {
	var stage  = stages[name];
	if(stage == null ) {
		stage = {}; stages[name] = stage;
	} 
	var data = stage[param];
	if(data == null)
		stage[param] = 0; return stage[param];
}

//poor man's Map
function put(name, param, value) {
	var stage  = stages[name];
	if(stage == null ) {
		stage = {}; stages[name] = stage;
	} 
	stage[param] = value;
	print (name + "," + param + "," + value);
	return stage[param];
}

//poor mans sprintf
function sprintf(x, y, boundaryPoint){
        x = x + '';
	var padding = boundaryPoint - x.length;
	if(padding < 0) padding = 0;
	var p = x + (new Array(padding)).join(" ") + y;
	return p;
}

function batchJob(job){
	stages                        = {};
	try{
		var sourceId          = job["sourceId"];
		var reportedTime      = 0;
		var tenant            = job["batchProperties"]["tenantId"];
		var jobStartTimestamp ;
		var jobStopTimestamp  ;
		try{
			var jobStartTimestamp = job["jobStartTimestamp"];
			var jobStopTimestamp  = job["jobStopTimestamp"];
			reportedTime          = (jobStopTimestamp.getTime() - jobStartTimestamp.getTime())/1000.0;
		}catch(e){ reportedTime = "Unknown"; }

		var totalRecords = 0;
		var totalErrors = 0;
		var mainFile     =  job["resourceEntries"][0]["externallyUploadedResourceId"];
		for(var i = 0; i < job["resourceEntries"].length; i++) {
		var entry                        = job["resourceEntries"][i];
			var errorCount                   = entry["errorCount"];
			var externallyUploadedResourceId = entry["externallyUploadedResourceId"];
			var recordCount                  = entry["recordCount"];
			totalRecords+= recordCount;
			totalErrors+=errorCount;
			print(sprintf ("   >" + externallyUploadedResourceId  , sprintf(recordCount  , " Records", 10), 70));
		}
		print ("                                                         Total:" + totalRecords + " Recs.");
		var totalTime = 0;
		for(var i = 0; i < job["stages"].length; i++) {
			var stage            = job["stages"][i];
			var name             = stage["stageName"];
			var start            = stage["startTimestamp"];
			var stop             = stage["stopTimestamp"];
			var time             = (stop.getTime() - start.getTime())/1000.0;
			var perRecord        = time/totalRecords;
			totalTime           += time;
			print(sprintf ("    >" + name, sprintf(time ,  " secs.", 10), 70));
		}
              print ("                                                         Total:" + totalTime + " secs.");
        	var rps = totalRecords/totalTime;
		var jobStats = [jobStartTimestamp, mainFile, tenant, Math.ceil(rps) + " RPS", 
		    jobStopTimestamp, totalErrors + " errors", jCount++].join(",  ");
		csv.push(jobStats);
	} catch ( exception ) {
	    for(var prop in exception){print (prop + ":" + exception[prop]);}
	}
}
print(csv.join("\n"));
