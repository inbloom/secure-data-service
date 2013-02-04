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

/*  
 * Creates SLC global namespace. 
 * All javascript files should be under SLC namespace
 */


var SLC = SLC || {};

/*
 * Description: This function creates namespaces under SLC.
 * @param ns_string - namespace string
 * @param assignee - the function which can be assigned to the namespace
 * @return parent object
 * Example: SLC.namespace('SLC.modules.module1');
 */
SLC.namespace = function (ns_string, assignee) {
	var parts = ns_string.split('.'),
		parent = SLC,
		i;
	
	if (parts[0] === "SLC") {
		parts = parts.slice(1);
	}
	
	for (i = 0; i < parts.length; i+=1) {
		if (typeof parent[parts[i]] === "undefined") {
			parent[parts[i]] = {};
			
			if (assignee && i === parts.length-1) {
				parent[parts[i]] = assignee;
			}
		}
		parent = parent[parts[i]];
	}
	
	return parent;
};

