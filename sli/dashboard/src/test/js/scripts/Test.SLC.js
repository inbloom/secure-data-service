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

module("SLC namespace");
		
test('Test SLC namespace', function () {

	ok(SLC !== undefined, 'SLC namespace should be defined');
	ok(typeof SLC === 'object', 'SLC method should be an object');
});

test('Test SLC.namespace method', function () {

	ok(SLC.namespace !== undefined, 'SLC.namespace method should be defined');
	ok(typeof SLC.namespace === 'function', 'SLC.namespace method should be function');
	
	SLC.namespace("SLC.module1");
	SLC.namespace("SLC.module2", function () {});
	
	ok(SLC.module1 !== undefined, 'SLC.module1 method should be defined');
	ok(typeof SLC.module1 === 'object', 'SLC.module1 method should be an object');
	
	ok(SLC.module2 !== undefined, 'SLC.module2 method should be defined');
	ok(typeof SLC.module2 === 'function', 'SLC.module2 method should be function');
});
