#!/bin/sh

mongoimport -d Midgar -c application --drop application.json
mongoimport -d Midgar -c applicationAuthorization --drop applicationAuthorization.json
mongoimport -d Midgar -c customRole --drop customRole.json
mongoimport -d sli -c realm --drop realm.json
