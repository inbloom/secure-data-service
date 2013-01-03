#!/bin/sh

mongoimport --host pantomcat01.slidev.org:27017 -d sli -c application --drop application.json
mongoimport --host pantomcat01.slidev.org:27017 -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c applicationAuthorization --drop applicationAuthorization.json
mongoimport --host pantomcat01.slidev.org:27017 -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c customRole --drop customRole.json
mongoimport --host pantomcat01.slidev.org:27017 -d sli -c realm --drop realm.json
