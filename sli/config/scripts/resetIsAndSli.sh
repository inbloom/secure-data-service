#!/bin/bash

mongo sli --eval 'db.dropDatabase()'
mongo sli ../indexes/sli_indexes.js

mongo is --eval 'db.dropDatabase()'
mongo is ../indexes/is_indexes.js
