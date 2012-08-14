// setup mongoose and the database
var mongoose = require('mongoose/');
var config = require('./config-mongo'); // Local congig file to hide creds
db = mongoose.connect(config.creds.mongoose_auth),
Schema = mongoose.Schema;  

// setup restify
var restify = require('restify');  
var server = restify.createServer();
server.use(restify.bodyParser());

// define _id in Schema otherwise mongoose does not return it
var schema = new Schema({
  _id: String
});

// create model object for newBatchJob
// Mongoose#model(name, [schema], [collection], [skipInit])
var Error = mongoose.model('Error', schema, 'error'); 
var NewBatchJob = mongoose.model('NewBatchJob', schema, 'newBatchJob'); 
var BatchJobStage = mongoose.model('BatchJobStage', schema, 'batchJobStage'); 
var StagedEntities = mongoose.model('StagedEntities', schema, 'stagedEntities'); 

// map of our model vars for generic route lookup
var routeMap = {
  'error': { 'model': Error, 'joinFunc': findJoinBatchJobId },
  'job': { 'model': NewBatchJob },
  'stage': { 'model': BatchJobStage, 'joinFunc': findJoinJobId  },
  'stagedentities': { 'model': StagedEntities, 'joinFunc': findJoinJobId  }
};

function findJoinJobId(model, docId, res) {
  model.find({ 'jobId': docId }, function (err,joinDoc) {
    console.log('jobId(%s) join doc: %s', docId, joinDoc);
    res.send(joinDoc);
  });
}

function findJoinBatchJobId(model, docId, res) {
  model.find({ 'batchJobId': docId}, function (err,joinDoc) {
    console.log('batchJobId(%s) join doc: %s', docId, joinDoc);
    res.send(joinDoc);
  });
}

function genericFind(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*"); 
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  console.log('GET request: %s', req);
  console.log('Request params: %s %s %s', req.params[0], req.params[1], req.params[2]);

  if (routeMap[req.params[0]] != null) {

    var firstModel = routeMap[req.params[0]].model;

    if (req.params[1] == null) {

      // find all query (only one param)
      firstModel.find({}, function (err,docs) {
        console.log('docs: %s', docs);
        res.send(docs);
      });
    } else {

      if (req.params[2] == null) {

        // find by _id query (params: /resource/id)
        firstModel.findOne({'_id':req.params[1]}, function (err,doc) {
          console.log('doc: %s', doc);
          res.send(doc);
        });
      } else {

        // join first resource with second (params: /resource1/id/resource2)
        if (routeMap[req.params[2]] != null) {
          console.log('attempting to join with: %s using id %s', req.params[2], req.params[1]);

          var secondModel = routeMap[req.params[2]].model;
          
          // lookup a defined join function to use - can't provide the id field of the query as a variable for some reason
          routeMap[req.params[2]].joinFunc(secondModel, req.params[1], res);

        } else {
          console.log('unregistered model requested in join: %s', req.params[2]);
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
