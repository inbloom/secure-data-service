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
 * SLC CutPoints
 * Handles all the methods related to grid cutpoints
 */
/*global SLC $ jQuery*/

SLC.namespace('SLC.grid.cutPoints', (function () {
	
		var PERF_LEVEL_TO_COLOR = {
			0: "#eeeeee",
			1: "#b40610",
			2: "#e58829",
			3: "#dfc836",
			4: "#7fc124",
			5: "#438746"
		};
			
		/*
		 * Get CutPoints array
		 * @param cutPoints
		 * @return cutPointsArray
		 */
		function toArray(cutPoints) {
			
			var cutPointsArray = [],
				count = 0,
				i;
			
			if (cutPoints === null || cutPoints === undefined) {
				return undefined;
			}
			
			for(i=0; i < cutPoints.length; i++) {
				if (cutPoints[i].minimumScore !== null && cutPoints[i].minimumScore !== undefined) {
					cutPointsArray[count++] = cutPoints[i].minimumScore;
				}
				if (i === cutPoints.length - 1) {
					if (cutPoints[i].maximumScore !== null && cutPoints[i].maximumScore !== undefined) {
						cutPointsArray[count] = cutPoints[i].maximumScore ;
					}
				}
			}
			
			return cutPointsArray;
		}
		
		/*
		 * Get level for CutPoints array
		 * @param cutPointsArray
		 * @param score
		 * @return level or -1
		 */
		function getLevelFromArray(cutPointsArray, score) {
			
			if (cutPointsArray === null || cutPointsArray === undefined || score === null || score === undefined) {
				return -1;
			}
			
			for (var i = 0; i < cutPointsArray.length - 1; i++) {
				if (cutPointsArray[i] <= score &&
					(cutPointsArray[i+1] > score || (cutPointsArray.length - 1 == i+1 && cutPointsArray[i+1] >= score))) {
					return i+1;
				}
			}
			return -1;
		}
		
		/*
		 * Get level for CutPoints
		 * @param cutPoints
		 * @param score
		 * @return level or -1
		 */
		function getLevelFromcutPoints(cutPoints, score) {
			
			if (cutPoints === null || cutPoints === undefined || score === null || score === undefined) {
				return -1;
			}
			
			for( var i=0;i < cutPoints.length; i++) {
				if (score !== null && score !== undefined &&
					cutPoints[i].minimumScore !== null && cutPoints[i].minimumScore !== undefined && 
					cutPoints[i].maximumScore !== null && cutPoints[i].maximumScore !== undefined &&
					cutPoints[i].minimumScore <= score && cutPoints[i].maximumScore >= score) {
						return i+1;
				}
			}
			return -1;
		}
	
		/*
		 * Convert cutPoints Array to string
		 * @param cutPointsArray
		 * @return cutPoints string
		 */
		function getArrayToString(cutPointsArray) {
			var i;
			
			if (cutPointsArray === null || cutPointsArray === undefined) {
				return "";
			}
			var returnString = "";
			for (i = 0; i < cutPointsArray.length; i++) {
				if (i !== cutPointsArray.length - 1) {
					returnString += cutPointsArray[i] + ", ";
				} else {
					returnString += cutPointsArray[i];
				}
			}
			
			return returnString;
		}
		
		return {
			toArray: toArray,
			getLevelFromArray: getLevelFromArray,
			getLevelFromcutPoints: getLevelFromcutPoints,
			getArrayToString: getArrayToString,
			PERF_LEVEL_TO_COLOR: PERF_LEVEL_TO_COLOR
		};
	}())
);
