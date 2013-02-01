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

module("SLC.grid.teardrop");

	test('Test SLC.grid.teardrop Namespace', function () {
		ok(SLC.grid.teardrop !== undefined, 'SLC.teardrop namespace should be defined');
		ok(typeof SLC.grid.teardrop === 'object', 'SLC.teardrop should be an object');
	});
	
	test('Test init method', function () {
		ok(SLC.grid.teardrop.init !== undefined, 'SLC.grid.teardrop init method should be defined');
		ok(typeof SLC.grid.teardrop.init === 'function', 'SLC.grid.teardrop init method should be function');
	});
	
	test('Test getGradeColorCode method', function () {
		ok(SLC.grid.teardrop.getGradeColorCode !== undefined, 'SLC.grid.teardrop getGradeColorCode method should be defined');
		ok(typeof SLC.grid.teardrop.getGradeColorCode === 'function', 'SLC.grid.teardrop getGradeColorCode method should be function');
		
		equal(SLC.grid.teardrop.getGradeColorCode("A+"), "darkgreen");
		equal(SLC.grid.teardrop.getGradeColorCode({}), false, "SLC.grid.teardrop getGradeColorCode method should return false if parameter passed as an object");
		equal(SLC.grid.teardrop.getGradeColorCode(12345), false, "SLC.grid.teardrop getGradeColorCode method should return false if parameter passed as a number");
	});
	
	test('Test getGradeTrendCodes method', function () {
		ok(SLC.grid.teardrop.getGradeTrendCodes !== undefined, 'SLC.grid.teardrop getGradeTrendCodes method should be defined');
		ok(typeof SLC.grid.teardrop.getGradeTrendCodes === 'function', 'SLC.grid.teardrop getGradeTrendCodes method should be function');
		
		equal(SLC.grid.teardrop.getGradeTrendCodes("A+"), 15);
		equal(SLC.grid.teardrop.getGradeTrendCodes({}), false, "SLC.grid.teardrop getGradeTrendCodes method should return false if parameter passed as an object");
		equal(SLC.grid.teardrop.getGradeTrendCodes(12345), false, "SLC.grid.teardrop getGradeTrendCodes method should return false if parameter passed as a number");
	});
	
	test('Test getStyle method', function () {
		ok(SLC.grid.teardrop.getStyle !== undefined, 'SLC.grid.teardrop getStyle method should be defined');
		ok(typeof SLC.grid.teardrop.getStyle === 'function', 'SLC.grid.teardrop getStyle method should be function');
		
		equal(SLC.grid.teardrop.getStyle("C", null), "teardrop teardrop-yellow-notrend");
		equal(SLC.grid.teardrop.getStyle("C", "A+"), "teardrop teardrop-yellow-downtrend");	
		equal(SLC.grid.teardrop.getStyle(), "teardrop teardrop-grey-flattrend");		
	});
