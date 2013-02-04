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
 * SLC grid sorters
 * Handles all the methods related to grid column sorting
 */
/*global SLC $*/

SLC.namespace('SLC.grid.sorters', (function () {
	
		var grid = SLC.grid,
			util = SLC.util;
			
		function Enum(params) {
	        var enumHash = {},
				i = 0;
	        
	        params.sortEnum.sort(util.numbersFirstComparator);
	        
	        for (; i < params.sortEnum.length; i++) {
	            enumHash[params.sortEnum[i]] = i;
	        }
	        return function(value, rowObject) {
	            var i = enumHash[value];
	            return i ? parseInt(i, 10) : -1;
	        };
	    }
	    
	    /**
	     * Sort by sortField provided in the params. The field must be int.
	     */
	    function ProxyInt(params) {
	        var fieldArray = (params.sortField) ? params.sortField.split(".") : [],
				length = fieldArray.length,
				ret,
				i;
				
	        return function(value, rowObject) {
				ret = rowObject;
				// find the field in the rowobject by its path "field.subfield.subsub" and return the value
				for (i=0; i < length && (ret = ret[fieldArray[i ++]]););
				return parseInt(ret, 10);
	        };
	    }
	
	    function LetterGrade(params) {
			var i;
			
	        return function(semesterGrades, rowObject) {
	             
	             if (semesterGrades === null || semesterGrades === undefined) {
	                 return -1;
	             }
	             if (semesterGrades[0] === null || semesterGrades[0] === undefined) {
	                 return -1;
	             }
	             if (semesterGrades[0].letterGrade === null || semesterGrades[0].letterGrade === undefined) {
	                 return -1;
	             }
	
	             i = grid.teardrop.getGradeTrendCodes(semesterGrades[0].letterGrade); 
	             
	             if (i === null || i === undefined) {
	                 return -1;
	             }
	             return i ? i : -1;
	        };
	     }
	
	     function LettersAndNumbers(params) {
			var i;
			
			return function(gradeDate, rowObject) {
	             
	             if (gradeDate === null || gradeDate === undefined) {
	                 return -1;
	             }
	             if (gradeDate.gradeEarned === null || gradeDate.gradeEarned === undefined) {
	                 return -1;
	             }
	
	             i = grid.teardrop.getGradeTrendCodes(gradeDate.gradeEarned); 
	             
	             if (i === undefined || i === null) {
	                 i = gradeDate.gradeEarned;
	             }
	             return i ? i : -1;
	        };
	    }
	    return {
			Enum: Enum,
			ProxyInt: ProxyInt,
			LetterGrade: LetterGrade,
			LettersAndNumbers: LettersAndNumbers
	    };
	}())
);
