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
 * SLC grid teardrop
 * Initialize & get data for grade color codes and grade trend codes
 */
/*global SLC $*/

SLC.namespace('SLC.grid.teardrop', (function () {
	    var GRADE_TREND_CODES = {},
			GRADE_COLOR_CODES = {};
	
	    function initGradeTrendCodes() {
	        GRADE_TREND_CODES['A+'] = 15;
	        GRADE_TREND_CODES['A'] = 14;
	        GRADE_TREND_CODES['A-'] = 13;
			GRADE_TREND_CODES['B+'] = 12;
			GRADE_TREND_CODES['B'] = 11;
	        GRADE_TREND_CODES['B-'] = 10;
	        GRADE_TREND_CODES['C+'] = 9;
	        GRADE_TREND_CODES['C'] = 8;
	        GRADE_TREND_CODES['C-'] = 7;
	        GRADE_TREND_CODES['D+'] = 6;
	        GRADE_TREND_CODES['D'] = 5;
	        GRADE_TREND_CODES['D-'] = 4;
	        GRADE_TREND_CODES['F+'] = 3;
	        GRADE_TREND_CODES['F'] = 2;
	        GRADE_TREND_CODES['F-'] = 1;
	        
	        GRADE_TREND_CODES['1'] = 5;
	        GRADE_TREND_CODES['2'] = 4;
	        GRADE_TREND_CODES['3'] = 3;
	        GRADE_TREND_CODES['4'] = 2;
	        GRADE_TREND_CODES['5'] = 1;
	    }
	
	    function initGradeColorCodes() {
	
	        GRADE_COLOR_CODES['A+'] = "darkgreen";
	        GRADE_COLOR_CODES['A'] = "darkgreen";
	        GRADE_COLOR_CODES['A-'] = "darkgreen";
	        GRADE_COLOR_CODES['B+'] = "lightgreen";
	        GRADE_COLOR_CODES['B'] = "lightgreen";
	        GRADE_COLOR_CODES['B-'] = "lightgreen";
	        GRADE_COLOR_CODES['C+'] = "yellow";
	        GRADE_COLOR_CODES['C'] = "yellow";
	        GRADE_COLOR_CODES['C-'] = "yellow";
	        GRADE_COLOR_CODES['D+'] = "orange";
	        GRADE_COLOR_CODES['D'] = "orange";
	        GRADE_COLOR_CODES['D-'] = "orange";
	        GRADE_COLOR_CODES['F+'] = "red";
	        GRADE_COLOR_CODES['F'] = "red";
	        GRADE_COLOR_CODES['F-'] = "red";
	        
	        GRADE_COLOR_CODES['1'] = "darkgreen";
	        GRADE_COLOR_CODES['2'] = "lightgreen";
	        GRADE_COLOR_CODES['3'] = "yellow";
	        GRADE_COLOR_CODES['4'] = "orange";
	        GRADE_COLOR_CODES['5'] = "red";
	    }
	
	    //Determines the css class of a teardrop
	    function getStyle(value, previousValue) {
	
	        var color  = "grey",
				trend = "notrend",
				styleName;
	        
	        if (value !== null && GRADE_COLOR_CODES[value] !== undefined) {
	           color = GRADE_COLOR_CODES[value];
	        }
	    
	        if ((value !== null) && (previousValue !== null)) {
	
	           var currentTrendCode = GRADE_TREND_CODES[value];
	           var previousTrendCode = GRADE_TREND_CODES[previousValue];
	           if ((currentTrendCode !== null) && (previousTrendCode !== null)) {
	              var trendCode = currentTrendCode - previousTrendCode;
	              if (trendCode > 0) {
	                 trend = "uptrend";
	              } else if (trendCode < 0) {
	                 trend = "downtrend";
	              } else {
	                 trend = "flattrend";
	              }
	           }
	        }
	    
	        styleName = "teardrop teardrop" + "-" + color + "-" + trend;
	        
	        return styleName;
	    }
	    
	    function getGradeColorCode(val) {
			if (typeof val !== "string") {
				return false;
			}
			
			return GRADE_COLOR_CODES[val];
		}
		
		function getGradeTrendCodes(val) {
			if (typeof val !== "string") {
				return false;
			}
			
			return GRADE_TREND_CODES[val];
		}
	
	    function init() { 
	       initGradeColorCodes();
	       initGradeTrendCodes();
	    }
	    
	    return {
			init: init,
			getStyle: getStyle,
			getGradeColorCode: getGradeColorCode,
			getGradeTrendCodes: getGradeTrendCodes
	    };
	}())
);
SLC.grid.teardrop.init();
