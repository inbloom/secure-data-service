/*
* Copyright 2012 Shared Learning Collaborative, LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/ 

var express = require('express'),
    util = require('util'),
    SLC = require('../client/SLC');
    config = require('./config');

require('colors');

var app = express();

app.configure(function(){
  app.set('port', config.app.port);
  app.use(express.favicon());
  app.use(express.logger('dev'));
  app.use(express.bodyParser());
  app.use(express.cookieParser());
  app.use(express.session({secret: 'secret'}));
  app.use(express.methodOverride());
  app.use("/html", express.static('html'));
});

// Creating a instance of SLC
SLC_app = new SLC(config.api.base_url, 
                  config.api.client_id, 
                  config.api.client_secret, 
                  config.api.oauth_uri,
                  config.api.api_version);


// If the token is active, it will allow next route handler in line to handle the request,
// if the token is expired, then it will redirect to login page
function requireToken() {
    return function(req, res, next) {
        if (req.session.tokenId) {
          next();
        }
        else {
          res.redirect("/");
        }
    }
}

// ROUTES

//Redirect to SLC login page
app.get('/', function (req, res) {
  var loginURL = SLC_app.getLoginURL();
  res.redirect(loginURL);
});

app.get('/oauth', function (req, res) {
  var code = req.param('code', null);
  
  SLC_app.oauth({code: code}, function (token) {
      if (token !== null || token !== undefined) {
        req.session.tokenId = token;
        res.redirect('/dashboard');
      }
      else {
        res.redirect('html/error.html');
      }
  });

});

// Logout and redirect to user login page.
app.get('/logout', function (req, res) {
  req.session.destroy();
  SLC_app.logout(function (response) {
    util.puts("logout "+response.msg);
    res.redirect('/');
  });
  
});

app.get('/dashboard', requireToken(), function (req, res) {
  res.redirect('html/index.html');
});

app.get('/html', requireToken(), function (req, res) {
  res.redirect('html/index.html');
});

// Get student list
app.get('/students', requireToken(), function(req, res) {
  SLC_app.api("/students", "GET", req.session.tokenId, {}, {}, function (data) {
    res.json(data);
  })
});

// Get School list
app.get('/schools', requireToken(), function(req, res) {
  SLC_app.api("/schools", "GET", req.session.tokenId, {}, {}, function (data) {
    res.json(data);
  })
});

/*
  POST Method
  Following example shows how to post attendance event for student on SLC server. 
  Replace student_id, school_id, school_year and event_date with actual student data.
*/

/*app.get('/attendances', requireToken(), function(req, res) {
  SLC_app.api("/attendances", "POST", req.session.tokenId, {}, 
  {
    "studentId": student_id,
    "schoolId": school_id,
    "schoolYearAttendance":[{
        "schoolYear": school_year,
        "attendanceEvent":[{
          "date": event_date,
          "event":"In Attendance"
        }]
    }]
  }, function (data) {
    res.json(data);
  })
});*/

/*
  Delete Method
  Following example shows how to delete student from SLC server. 
  Replace ":id" with actual student id
*/

/*app.get('/deleteStudent', requireToken(), function(req, res) {
  SLC_app.api("/students/:id", "DELETE", req.session.tokenId, {}, {}, function (data) {
    res.json(data);
  })
});
*/

app.listen(app.get('port'), function(){
  util.puts("The app has started".green);
});

