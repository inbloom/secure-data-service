// mongo localhost:27017/ingestion_batch_job stressCollect.js
db.ingestionStats.remove({});
var csv      = [];
db.newBatchJob.find().forEach(batchJob);

//poor man's sprintf
function sprintf(x, y, boundaryPoint){
	x = x + '';
	var padding = boundaryPoint - x.length;
	if(padding < 0) padding = 0;
	var p = x + (new Array(padding)).join(" ") + y;
	return p;
}

function tDiff(s, e) {
	if(s != null && e != null ) 
		return  e.getTime() - s.getTime();
	else return 0;
}

function batchJob(job){
	var stats ={};
	try{
		with(job){
			stats                 = {};
			var reportedTime      = tDiff(jobStartTimestamp, jobStopTimestamp)/1000.0;
			var totalRecords      = 0;
			var totalErrors       = 0;
			var totalTime         = 0;
			stats["a1-job-start"]    = jobStartTimestamp;
			stats["a1-job-stop" ]    = jobStopTimestamp;
			stats["a1-job-id" ]      = _id;
			stats["a1-job-file"]     = resourceEntries[0]["externallyUploadedResourceId"];
			for(var i = 0; i < resourceEntries.length; i++) {
				with(resourceEntries[i]){
					totalRecords     +=recordCount;
					totalErrors      +=errorCount;
					var  externalId   = externallyUploadedResourceId.replace(/[\\\/\.]/, '-');
					stats["a2-resource-records-" + externalId] = totalRecords + '';
					stats["a2-resource-errors-"  + externalId] = totalErrors + '';
				}
			}
			stats["a2-job-totalRecords"] = totalRecords + '';
			stats["a2-job-totalErrors"]  = totalErrors  + '';
			for(var i = 0; i < stages.length; i++) {
				with(stages[i]){
					totalTime           += elapsedTime;
					stats["b1-stage-" + stageName + "-time"] = elapsedTime + ''; 
				}
			}
			var rps = totalRecords/totalTime;
			db.batchJobStage.find({jobId:_id}).forEach(
					function stageHandler(stage) {
						with(stage) {
							if(metrics != null && metrics.length > 0) {
								with(metrics[0]) {
									var  resource = resourceId.replace(/[\\\/\.]/, '-');
									if(recordCount != 0)
									stats["b2-stage-" + stageName + "-" + resource + "-recordCount"]    = recordCount + '' ;
									if(errorCount != 0)
									stats["b2-stage-" + stageName + "-" + resource + "-errorCount"]     = errorCount  + '' ;
			                                                if(elapsedTime != 0)
									stats["b2-stage-" + stageName + "-" + resource + "-elapsedTime"]    = elapsedTime + '' ;
								}
							}
						}
					});//end forEach		
			if(typeof executionStats != 'undefined')
                        for(var machineName in executionStats) {
				var machineStats = executionStats[machineName];
				for(var stat in machineStats){
					var statComps = stat.split('#');
					var dbase     = statComps[0], func = statComps[1], collection = statComps[2];
					var statValue = machineStats[stat];
					var op        = 'Operation';
					if( func != 'getCollection'){
						if(func == 'update' || func == 'insert') op = 'write'; else op = 'read';
						var count = statValue['left'];
						var time  = statValue['right'];
						var cParam = 'c1-mongo' + '-' +  dbase + '-' + collection + '-' + op + 'count' ;
						var tParam = 'c1-mongo' + '-' +  dbase + '-' + collection + '-' + op + 'time' ;
						if(stats[cParam]  != null) stats[cParam] = stats[cParam] + count + '' ;
						else stats[cParam] = count + '' ;
						if(stats[tParam]  != null) stats[tParam] = stats[tParam] + time  + '' ;
						else stats[tParam] = time  + '';
					}
				}
			}
			csv.push(stats);
			db.ingestionStats.insert(stats);
		}//end with job
	} catch ( exception ) {
		print(exception);
		for(var prop in exception){print (prop + ":" + exception[prop]);}
		quit(0);
	}
}

