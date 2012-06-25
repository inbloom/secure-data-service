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