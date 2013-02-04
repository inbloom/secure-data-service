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

/*global module test ok define equal deepEqual notEqual SLC $*/

module("SLC.grid.sorters");

	test('Test SLC.grid.sorters Namespace', function () {
		ok(SLC.grid.sorters !== undefined, 'SLC.sorters namespace should be defined');
		ok(typeof SLC.grid.sorters === 'object', 'SLC.sorters should be an object');
	});
	
	test('Test Enum method', function () {
		ok(SLC.grid.sorters.Enum !== undefined, 'SLC.grid.sorters Enum method should be defined');
		ok(typeof SLC.grid.sorters.Enum === 'function', 'SLC.grid.sorters Enum method should be function');
	});
	
	var params = {
		"fieldName": "StateTestWriting",
		"name": "StateTest Writing",
		"sortField": "assessments.StateTest Writing.Scale score",
		"valueField": "Scale score"
	};
	
	test('Test ProxyInt method', function () {
		ok(SLC.grid.sorters.ProxyInt !== undefined, 'SLC.grid.sorters ProxyInt method should be defined');
		ok(typeof SLC.grid.sorters.ProxyInt === 'function', 'SLC.grid.sorters ProxyInt method should be function');
		equal(typeof SLC.grid.sorters.ProxyInt(params), "function");
	});
	
	test('Test LetterGrade method', function () {
		ok(SLC.grid.sorters.LetterGrade !== undefined, 'SLC.grid.sorters LetterGrade method should be defined');
		ok(typeof SLC.grid.sorters.LetterGrade === 'function', 'SLC.grid.sorters LetterGrade method should be function');
	});
	
	test('Test LettersAndNumbers method', function () {
		ok(SLC.grid.sorters.LettersAndNumbers !== undefined, 'SLC.grid.sorters LettersAndNumbers method should be defined');
		ok(typeof SLC.grid.sorters.LettersAndNumbers === 'function', 'SLC.grid.sorters LettersAndNumbers method should be function');
	});
