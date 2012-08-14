// setup restify
var restify = require('restify');  
var server = restify.createServer();
server.use(restify.bodyParser());

// setup default mongoose db connection
var mongoose = require('mongoose');

// will hold mongo connections <host:port, con>, 
var connectionMap = {};

function createConnection(host) {
  var uri = 'mongodb://' + host + '/ingestion_batch_job';

  var con = mongoose.createConnection(uri);

  // define _id in Schema otherwise mongoose does not return it
  var schema = new mongoose.Schema({
    _id: String
  });

  // create model object for newBatchJob
  // Mongoose#model(name, [schema], [collection], [skipInit])
  var Error = con.model('Error', schema, 'error'); 
  var NewBatchJob = con.model('NewBatchJob', schema, 'newBatchJob'); 
  var BatchJobStage = con.model('BatchJobStage', schema, 'batchJobStage'); 
  var StagedEntities = con.model('StagedEntities', schema, 'stagedEntities'); 

  // map of our model vars for generic route lookup
  var routeMap = {
    'error': { 'model': Error, 'allQuery': findAll, 'jobJoinQuery': findJoinBatchJobId },
    'job': { 'model': NewBatchJob, 'allQuery':  findAll },
    'jobids': { 'model': NewBatchJob, 'allQuery':  findAllIds },
    'stage': { 'model': BatchJobStage, 'allQuery':  findAll, 'jobJoinQuery': findJoinJobId  },
    'stagedentities': { 'model': StagedEntities, 'allQuery':  findAll, 'jobJoinQuery': findJoinJobId  }
  };

  var mapObj = { 'routeMap': routeMap };
  connectionMap[host] = mapObj;
}

function getOrCreateConnection(host) {
  var con = connectionMap[host];
  if (con == null) {
    createConnection(host);
    con = connectionMap[host];
  }
  return con;
}

function findAll(model, res) {
  model.find({}, function (err,docs) {
    //console.log('docs: %s', docs);
    res.send(docs);
  });
}

function findAllIds(model, res) {
  model.find(
    {},
    '_id jobStartTimestamp',
    { sort:{ jobStartTimestamp: -1 }},
    function (err,docs) {
      //console.log('docs: %s', docs);
      res.send(docs);
    }
  );
}

function findJoinJobId(model, docId, res) {
  model.find({ 'jobId': docId }, function (err,joinDoc) {
    //console.log('jobId(%s) join doc: %s', docId, joinDoc);
    res.send(joinDoc);
  });
}

function findJoinBatchJobId(model, docId, res) {
  model.find({ 'batchJobId': docId}, function (err,joinDoc) {
    //console.log('batchJobId(%s) join doc: %s', docId, joinDoc);
    res.send(joinDoc);
  });
}

function findById(model, docId, res) {
  model.findById(docId, function (err,doc) {
    //console.log('doc: %s', doc);
    res.send(doc);
  });
}

function genericFind(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*"); 
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  console.log('GET request: %s', req);

  // get mongo host:port from request headers, defaulting to localhost:27017
  // X-Api-Version because there is some problem with custom headers and jquery/restify
  var mongo = req.header('X-Api-Version', 'localhost:27017');
  console.log('Request: %s/%s/%s/%s', mongo, req.params[0], req.params[1], req.params[2]);

  var routeMap = getOrCreateConnection(mongo).routeMap;

  if (routeMap[req.params[0]] != null) {

    var firstModel = routeMap[req.params[0]].model;

    if (req.params[1] == null) {
      // find all query (only one param)
      routeMap[req.params[0]].allQuery(firstModel, res);

    } else {
      var reqId = req.params[1];

      if (req.params[2] == null) {
        // find by _id query (params: /resource/id)
        findById(firstModel, reqId, res);
        
      } else {
        var joinResource = req.params[2];

        // join first resource with second (params: /resource1/id/resource2)
        if (routeMap[joinResource] != null) {
          console.log('attempting to join with: %s using id %s', joinResource, reqId);

          var secondModel = routeMap[joinResource].model;

          // lookup a defined join function to use - can't provide the id field of the query as a variable for some reason
          routeMap[joinResource].jobJoinQuery(secondModel, reqId, res);

        } else {
          console.log('unregistered model requested in join: %s', joinResource);
          res.send(404);
        }
      }
    }
  } else {
    console.log('unregistered model requested: %s', req.params[2]);
    res.send(404);
  }
}

// Set up our routes and start the server
server.get(/^\/([a-zA-Z0-9_\.~-]+)\/(.*)\/(.*)/, genericFind);
server.get(/^\/([a-zA-Z0-9_\.~-]+)\/(.*)/, genericFind);
server.get(/^\/([a-zA-Z0-9_\.~-]+)/, genericFind);

server.listen(8080, function() {
  console.log('%s jobReport server started and listening at %s', server.name, server.url);
});

process.on('uncaughtException', function(err) {
  console.log('uncaught exception: ' + err);
});