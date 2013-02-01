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



var cleanupBodyAndId = function() {  
    //list aggregates needing cleanup
    db.aggregation.find({"value.raw":"true"}).forEach(
        function (data) {
            var old_id = data._id;
            var doc = data;
			
            doc._id = makeID(generateUUID());
            doc.type = "aggregation";
            doc.body = doc.value;

            delete doc.value;
            delete doc.body.raw;

            db.aggregation.insert(doc);
            db.aggregation.remove({ _id: old_id});
        }
    );
}

db.system.js.save({ "_id" : "cleanupBodyAndId", "value" : cleanupBodyAndId })
