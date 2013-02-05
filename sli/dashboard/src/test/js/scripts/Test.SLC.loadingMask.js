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

module("SLC.loadingMask");
		
test('Test SLC.loadingMask namespace', function () {

	ok(SLC.loadingMask !== undefined, 'SLC.loadingMask namespace should be defined');
	ok(typeof SLC.loadingMask === 'object', 'SLC.loadingMask method should be function');
});

test('Test SLC.loadingMask.create method', function () {

	ok(SLC.loadingMask.create !== undefined, 'SLC.loadingMask create method should be defined');
	ok(typeof SLC.loadingMask.create === 'function', 'SLC.loadingMask create method should be function');
});

module("Loading Mask widget", {
	setup: function () {
		this.testLoader = SLC.loadingMask.create({context: "<div></div>", message: "testing..."});
		this.testLoader1 = SLC.loadingMask.create({context: "<div id='testing1'></div>"});
	},
	teardown: function () {
		this.testLoader.remove();
		this.testLoader1.remove();
	}
});

test("Test first loader", function () {
	equal(this.testLoader.length, 1, "First Loader instance is created.");
	
	this.testLoader.show();
	equal(this.testLoader.css("display"), "block");
	
	this.testLoader.hide();
	equal(this.testLoader.css("display"), "none");
});

test("Test second loader", function () {
	equal(this.testLoader1.length, 1, "Second Loader instance is created.");
	
	this.testLoader1.show();
	equal(this.testLoader1.css("display"), "block");
	
	this.testLoader1.hide();
	equal(this.testLoader1.css("display"), "none");

});
