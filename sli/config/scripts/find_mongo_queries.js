/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


// query the system.profile collection and attempt to build a map of distinct queries per namespace (collection)
// author: danny duran

disinctQueries = buildDistinctQueries();

printjson(disinctQueries);

function buildDistinctQueries() {
	// we will load this map up with distinct(ish) queries
	queryMap = {};

	// find all relevant queries from system.profile
	db.system.profile.find({"op":"query", "ns": { $nin: ["sli.system.profile", "sli.system.namespaces", "sli.system.js"]} }).forEach(
		function(doc) {
		
			// get array of queries for a collection if one exists in our map
			mapValues = getIfExists(doc.ns, queryMap);
		
			// create an array of all the keys used in a given query
			queryKeys = getQueryKeys(doc.query);
		
			// eliminate queries that query on a set of fields we've already covered
			newQuery = existsInArrayOfJson(queryKeys, mapValues);
			if (newQuery) {
				mapValues.push(queryKeys);
			}

			// update map
			queryMap[doc.ns] = mapValues;
		}
	);
	return queryMap;
}

function getQueryKeys(query) {
	queryKeys = [];
	for (query_item in query) {
		if (query[query_item] instanceof Object) {
			// if the query value is an object, load the whole thing in as this is a complex query
			query_to_add = {}
			query_to_add[query_item] = query[query_item];
			queryKeys.push(query_to_add);
		}
		else {
			// otherwise just add the key of the query (dont care about value)
			queryKeys.push(query_item);
		}
	}
	return queryKeys;
}

function existsInArrayOfJson(json, array) {
	exists = true;
	for (item in array) {
		if (tojson(array[item]) === tojson(json)) {
			exists=false;
			break;
		}
	}
	return exists;
}

function getIfExists(item, array) {
	subArray = [];
	if (array[item] != null) {
		subArray = array[item];
	}
	return subArray;
}
