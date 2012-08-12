// Setup mongoose and the database
var mongoose = require('mongoose/');
var config = require('./config-mongo'); // Local congig file to hide creds
db = mongoose.connect(config.creds.mongoose_auth),
Schema = mongoose.Schema;  

// require restify and bodyParser to read Backbone.js syncs
var restify = require('restify');  
var server = restify.createServer();
server.use(restify.bodyParser());

// Empty schema for now
var schema = new Schema({
  _id: String
});

// create model object for newBatchJob
// Mongoose#model(name, [schema], [collection], [skipInit])
var NewBatchJob = mongoose.model('NewBatchJob', schema, 'newBatchJob'); 

function getJob(req, res, next) {
  console.log('GET /job with request: %s', req);

  // Resitify currently has a bug which doesn't allow you to set default headers
  // This headers comply with CORS and allow us to server our response to any origin
  res.header("Access-Control-Allow-Origin", "*"); 
  res.header("Access-Control-Allow-Headers", "X-Requested-With");

  NewBatchJob.find({}, function (err,docs) {
    console.log('newBatchJob docs: %s', docs);
    res.send(docs);
  });
}

// Set up our routes and start the server
server.get('/job', getJob);

server.listen(8080, function() {
  console.log('%s jobReport server started and listening at %s', server.name, server.url);
});
