#!/bin/sh

mongo sli --quiet --eval "db.application.remove({}); assert.eq(null, db.getLastError());"
mongoimport -d sli -c application --file application.json
mongo 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a --quiet --eval "db.applicationAuthorization.json.remove({}); assert.eq(null, db.getLastError());"
mongoimport -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c applicationAuthorization --file applicationAuthorization.json
mongo 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a --quiet --eval "db.customRole.remove({}); assert.eq(null, db.getLastError());"
mongoimport -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c customRole --file customRole.json
mongo sli --quiet --eval "db.realm.remove({}); assert.eq(null, db.getLastError());"
mongoimport -d sli -c realm --file realm.json
# flush router config
mongo sli --quiet --eval "db.adminCommand('flushRouterConfig'); assert.eq(null, db.getLastError());"
mongo 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a --quiet --eval "db.adminCommand('flushRouterConfig'); assert.eq(null, db.getLastError());"
