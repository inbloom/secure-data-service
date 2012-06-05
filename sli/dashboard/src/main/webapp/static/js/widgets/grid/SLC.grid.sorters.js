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
				i = 0;
				
	        return function(value, rowObject) {
				ret = rowObject;
				// find the field in the rowobject by its path "field.subfield.subsub" and return the value
				while(i < length && (ret = ret[fieldArray[i ++]])) {
					return parseInt(ret, 10);
				}
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
	
	             i = grid.teardrop.GRADE_TREND_CODES[semesterGrades[0].letterGrade]; 
	             
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
	
	             i = grid.teardrop.GRADE_TREND_CODES[gradeDate.gradeEarned]; 
	             
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