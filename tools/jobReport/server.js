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
var NewBatchJob = mongoose.model('NewBatchJob', schema, 'newBatchJob'); 
var BatchJobStage = mongoose.model('BatchJobStage', schema, 'batchJobStage'); 
var StagedEntities = mongoose.model('StagedEntities', schema, 'stagedEntities'); 

// map of our model vars for generic route lookup
var routeMap = {
  'job': NewBatchJob, 
  'stage': BatchJobStage,
  'stagedentities': StagedEntities
};

function genericFind(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*"); 
  res.header("Access-Control-Allow-Headers", "X-Requested-With");
  console.log('GET request: %s', req);
  console.log('Request params: %s %s %s', req.params[0], req.params[1], req.params[2]);

  if (routeMap[req.params[0]] != null) {

    if (req.params[1] == null) {
      // find all query
      routeMap[req.params[0]].find({}, function (err,docs) {
        console.log('docs: %s', docs);
        res.send(docs);
      });
    }  else {

      routeMap[req.params[0]].findOne({'_id':req.params[1]}, function (err,doc) {
        // find by id query
        console.log('doc: %s', doc);
        if (req.params[2] == null) {
          res.send(doc);
        } else if (routeMap[req.params[2]] != null) {
          // join with additional resource
          console.log('attempting to join with: $s using id $s', req.params[2], doc._id);
          routeMap[req.params[2]].find({'jobId':doc._id}, function (err,joinDoc) {
            console.log('joinDoc: %s', joinDoc);
            res.send(joinDoc);
          });
        } else {
          console.log('unregistered model requested in join: %s', req.params[2]);
          res.send(404);
        }
      });
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
