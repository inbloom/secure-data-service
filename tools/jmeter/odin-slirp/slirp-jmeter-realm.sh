#!/bin/sh

mongoimport -d sli -c application --drop application.json
mongoimport -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c applicationAuthorization --drop applicationAuthorization.json
mongoimport -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c customRole --drop customRole.json
mongoimport -d sli -c realm --drop realm.json
