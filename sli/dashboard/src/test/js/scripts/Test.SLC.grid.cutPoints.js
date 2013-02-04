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

module("SLC.grid.cutPoints");

	test('Test SLC.grid.cutPoints Namespace', function () {
		ok(SLC.grid.cutPoints !== undefined, 'SLC.cutPoints namespace should be defined');
		ok(typeof SLC.grid.cutPoints === 'object', 'SLC.cutPoints should be an object');
	});
	
	test('Test toArray method', function () {
		var cutPoints, newCutPointsArray;
		
		ok(SLC.grid.cutPoints.toArray !== undefined, 'SLC.grid.cutPoints toArray method should be defined');
		ok(typeof SLC.grid.cutPoints.toArray === 'function', 'SLC.grid.cutPoints toArray method should be function');
		
		deepEqual(SLC.grid.cutPoints.toArray(), undefined, 'The toArray method should return undefined if no parameter passed to the function');
		
		cutPoints = [
			{ assessmentReportingMethod: "Scale score", maximumScore: 179, minimumScore: 120 },
			{ assessmentReportingMethod: "Scale score", maximumScore: 230, minimumScore: 180 },
			{ assessmentReportingMethod: "Scale score", maximumScore: 277, minimumScore: 231 }
		];
		
		newCutPointsArray = SLC.grid.cutPoints.toArray(cutPoints);
		deepEqual(newCutPointsArray.length, 4, "The method should return an array");
	});
	
	test('Test getLevelFromArray method', function () {
		ok(SLC.grid.cutPoints.getLevelFromArray !== undefined, 'SLC.grid.cutPoints getLevelFromArray method should be defined');
		ok(typeof SLC.grid.cutPoints.getLevelFromArray === 'function', 'SLC.grid.cutPoints getLevelFromArray method should be function');
		
		deepEqual(SLC.grid.cutPoints.getLevelFromArray(), -1, 'The method should return -1 if no parameters are passed');
		
		var cutPointsArray = [120, 180, 231, 277],
			score = "222";
		
		deepEqual(SLC.grid.cutPoints.getLevelFromArray([], score), -1, 'The method should return -1 if cutpointsArray parameter is empty');
		deepEqual(SLC.grid.cutPoints.getLevelFromArray(cutPointsArray), -1, 'The method should return -1 if score parameter not passed to the function');
		deepEqual(SLC.grid.cutPoints.getLevelFromArray(cutPointsArray, score), 2, 'The method should return level');
	});
	
	test('Test getLevelFromcutPoints method', function () {
		ok(SLC.grid.cutPoints.getLevelFromcutPoints !== undefined, 'SLC.grid.cutPoints getLevelFromcutPoints method should be defined');
		ok(typeof SLC.grid.cutPoints.getLevelFromcutPoints === 'function', 'SLC.grid.cutPoints getLevelFromcutPoints method should be function');
		
		deepEqual(SLC.grid.cutPoints.getLevelFromcutPoints(), -1, 'The method should return -1 if no parameters are passed');
		
		var cutPoints = [
				{ assessmentReportingMethod: "Scale score", maximumScore: 179, minimumScore: 120 },
				{ assessmentReportingMethod: "Scale score", maximumScore: 230, minimumScore: 180 },
				{ assessmentReportingMethod: "Scale score", maximumScore: 277, minimumScore: 231 }
			],
			score = "222";
		
		deepEqual(SLC.grid.cutPoints.getLevelFromcutPoints([], score), -1, 'The method should return -1 if cutPoints parameter is empty');
		deepEqual(SLC.grid.cutPoints.getLevelFromcutPoints(cutPoints), -1, 'The method should return -1 if score parameter not passed to the function');
		deepEqual(SLC.grid.cutPoints.getLevelFromcutPoints(cutPoints, score), 2, 'The method should return level');
	});
	
	test('Test getArrayToString method', function () {
		ok(SLC.grid.cutPoints.getArrayToString !== undefined, 'SLC.grid.cutPoints getArrayToString method should be defined');
		ok(typeof SLC.grid.cutPoints.getArrayToString === 'function', 'SLC.grid.cutPoints getArrayToString method should be function');
		
		deepEqual(SLC.grid.cutPoints.getArrayToString(), "", "The method should return empty string if no parameter are passed");
		
		var cutPointsArray = [120, 180, 231, 277];
		deepEqual(SLC.grid.cutPoints.getArrayToString(cutPointsArray), "120, 180, 231, 277", "The method should convert array into string");
	});
