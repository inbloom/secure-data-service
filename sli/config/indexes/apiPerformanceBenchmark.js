/*
  For each resourse, sum up the response time , and figure out the average
  response time.

  Note that to be able to find the average api response time, we need to keep a
  total of the all the response time, and then divide at the end.

  We're also going to set this up so that we can repeat the process to get
  incremental results over time.
*/

function mapf()
{
    emit(this.body.resource,
         {resource:this.body.resource, total_time:this.body.responseTime, count:1, avg_time:0});
}

function reducef(key, values)
{
    var r =  {resource:key, total_time:0, count:1, avg_time:0};
    values.forEach(function(v)
		   {
		       r.total_time += v.total_time;
		       r.count += v.count;
		   });
    return r;
}

function finalizef(key, value)
{
    if (value.count > 0)
	value.avg_time = value.total_time / value.count;

    return value;
}

/*
  Here's the initial run.

  The query isn't technically necessary, but is included here to demonstrate
  how this is the same map-reduce command that will be issued later to do
  incremental adjustment of the computed values.  The query is assumed to run
  once a day at midnight.
 */
var mrcom1 = db.runCommand( { mapreduce:"apiResponse",
			      map:mapf,
			      reduce:reducef,
			      out: { reduce: "apiResponse_stat" },
			      finalize:finalizef,
            verbose: true
			    });

