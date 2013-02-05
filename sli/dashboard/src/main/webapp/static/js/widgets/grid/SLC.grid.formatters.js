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
 * SLC grid formatters
 * Handles all the methods for displaying cutpoints, lozenge, fuelGauge, grade and teardrop in the grid
 */
/*global SLC $ */

SLC.namespace('SLC.grid.formatters', (function () {
	
		var grid = SLC.grid,
			util = SLC.util;
	    /* formatoptions : {1:{color:'green'}, 2:{color:'grey'},...} */
	    function CutPoint(value, options, rowObject) {
			var cutPoints = util.sortObject(options.colModel.formatoptions.cutPoints, util.compareInt),
				style,
				cutPoint;
				
	        if (!value && value !== 0) {
	            return '';
	        }
	        for (cutPoint in cutPoints) {
				if (cutPoints.hasOwnProperty(cutPoint)) {
		            style = cutPoints[cutPoint].style;
		            if (value - cutPoint <= 0) {
		                break;
		            }
	           }
	        }
	        
	        return "<span class='" + cutPoints[cutPoint].style + "'>" + value + "</span>";
	    }
	    
	    function CutPointReverse(value, options, rowObject) {
			var cutPoints = util.sortObject(options.colModel.formatoptions.cutPoints, util.compareInt),
				style;
				
	        if (!value && value !== 0) {
	            return '';
	        }
	        
	        for (var cutPoint in cutPoints) {
				if (cutPoints.hasOwnProperty(cutPoint)) {
		            if (value - cutPoint < 0) {
		                break;
		            }
		            style = cutPoints[cutPoint].style;
				}
	        }
	        return "<span class='" + style + "'>" + value + "</span>";
	    }
	    
	    function PercentBar(value, options, rowObject) {
			var color,
				colorValue = value,
				formatoptions = options.colModel.formatoptions,
				low = 30, 
				medium = 70;
	        
	        if (value === null || value === "") {
	          return "";
	        }
	
	        if (formatoptions && formatoptions.reverse === true) {
	            colorValue = 100 - value;
	        }
	        
	        if (formatoptions && formatoptions.low) {
	            low = formatoptions.low;
	        }
	        
	        if (formatoptions && formatoptions.medium) {
	            medium = formatoptions.medium;
	        }
	        
	        if (colorValue < low) {
	          color = "red";
	        } else if (colorValue < medium) {
	          color = "silver";
	        } else {
	          color = "green";
	        }
	
	        return "<span style='display: inline-block;height: 6px;-moz-border-radius: 3px;-webkit-border-radius: 3px;background:" + color + ";width:" + value * 0.9 + "%'></span>";
	    }
	      
	    function Lozenge(value, options, rowObject) {    
	        return util.renderLozenges(rowObject);
	    }
	    
	    function FuelGauge(value, options, rowObject) {
	        var name = options.colModel.formatoptions.name,
				valueField = options.colModel.formatoptions.valueField,
				assessments = (name) ? rowObject.assessments[name]: rowObject.assessments,
				score, fieldName, cutPoints, cutPointsArray, perfLevel, perfLevelClass, divId,
				returnValue, width, height;

	        if (!assessments || assessments === undefined) {
	            return "" ;
	        }
	        
	        score = (assessments[valueField]) ? assessments[valueField] : rowObject[valueField];
	        
	        if (!score || score === undefined || value === undefined || value === null) {
	            score = 0;
	        }
	        
			fieldName = options.colModel.formatoptions.fieldName;
			cutPoints = (assessments.assessments) ? assessments.assessments.assessmentPerformanceLevel : assessments.assessmentPerformanceLevel;
			cutPointsArray = options.colModel.formatoptions.cutPointsArray;
			
	        if (cutPointsArray === null || cutPointsArray === undefined) {
	            cutPointsArray = grid.cutPoints.toArray(cutPoints);
	        }
	        
	        perfLevel = options.colModel.formatoptions.perfLevel;
	        
	        if (perfLevel === null || perfLevel === undefined ) {
	            perfLevel = grid.cutPoints.getLevelFromArray(cutPointsArray, score);
	        }
	        
	        perfLevelClass = options.colModel.formatoptions.perfLevelClass;
	        
	        if (perfLevelClass === null || perfLevelClass === undefined) {
	            var cutPointLevel = options.colModel.formatoptions.cutPoints[perfLevel];
	            if (cutPointLevel !== null && cutPointLevel !== undefined) {
	                perfLevelClass = cutPointLevel.style;
	            } else {
	                perfLevelClass = "";
	            }
	        }
	        
	        divId = fieldName + util.counter();
			returnValue = "<div id='" + divId + "' class='fuelGauge " + perfLevelClass + "' >";
			
	        returnValue += "<script>";
	        returnValue += "var cutPoints = new Array(" + grid.cutPoints.getArrayToString(cutPointsArray) + ");";
	        returnValue += "$('#" + divId + "').parent().attr('title', '" + score + "');"; 
	        returnValue += "var fuelGauge = new SLC.grid.FuelGauge ('" + divId + "', " + score + ", cutPoints);";
	        
	        width = options.colModel.formatoptions.fuelGaugeWidth;
			
	        if (width === null || width === undefined) {
	            width = options.colModel.width;
	            if (width !== null && width !== undefined) {
	                width = Math.round(width * 9 / 100) * 10;
	            } else {
	                width = 0;
	            }
	        }
	        
	        height = Math.sqrt(width);
	        height -= height % 1;//removing the decimals.
	        
	        returnValue += "fuelGauge.setSize('" + width + "', '" + height + "');";
	        returnValue += "fuelGauge.create();";
	        returnValue += "</script>";
	        returnValue += "</div>";
	        
	        return  returnValue;
	    }
	    
	    function FuelGaugeWithScore(value, options, rowObject) {
	        var perfLevelClass = "",
				name = options.colModel.formatoptions.name,
				valueField = options.colModel.formatoptions.valueField,
				assessments = (name) ? rowObject.assessments[name]: rowObject.assessments,
				score, cutPoints, cutPointsArray, perfLevel, defaultCutPointsSettings,
				cutPointsSettings, cutPointLevel, width;
	        
	        if (value === undefined || value === null) {
	            return "<span class='fuelGauge-perfLevel'></span>";
	        }
	        
	        if (!assessments || assessments === undefined) {
	
	            if (value === undefined || value === null) {
	                value = "!";
	            }
	            return "<span class='fuelGauge-perfLevel'>" + value + "</span>" ;
	        }
	        
	        score = (assessments[valueField]) ? assessments[valueField] : rowObject[valueField];
	        
	        if (!score || score === undefined) {
	            return "<span class='fuelGauge-perfLevel'>" + value + "</span>" ;
	        }
	        
	        cutPoints = (assessments.assessments) ? assessments.assessments.assessmentPerformanceLevel : assessments.assessmentPerformanceLevel;
			cutPointsArray = grid.cutPoints.toArray(cutPoints);
			perfLevel = grid.cutPoints.getLevelFromArray(cutPointsArray, score);
			defaultCutPointsSettings = { 5:{style:'color-widget-darkgreen'}, 4:{style:'color-widget-green'}, 3:{style:'color-widget-yellow'}, 2:{style:'color-widget-orange'}, 1:{style:'color-widget-red'}};
			cutPointsSettings = (options.colModel.formatoptions.cutPoints) ? options.colModel.formatoptions.cutPoints : defaultCutPointsSettings;
			cutPointLevel = cutPointsSettings[perfLevel];
			
	        if (cutPointLevel !== null && cutPointLevel !== undefined) {
	            perfLevelClass = cutPointLevel.style;
	        }
	        
	        width = options.colModel.width;
	        
	        if (width !== null &&  width !== undefined) {
	            options.colModel.formatoptions.fuelGaugeWidth = Math.round(width * 3/400) * 100;
	        }
	        
	        options.colModel.formatoptions.cutPointsArray = cutPointsArray;
	        options.colModel.formatoptions.perfLevel = perfLevel;
	        options.colModel.formatoptions.perfLevelClass = perfLevelClass;
	        
	        return "<span class='" + perfLevelClass + " fuelGauge-perfLevel'>" + $.jgrid.htmlEncode(value) + "</span>" + FuelGauge(value, options, rowObject);
	    }
	
	    function Grade(value, options, rowobject) {
	        var div = "<div class=\"",
				closeDiv = "\">",
				endDiv = "</div>",
				styleClass = "",
				innerHtml = "";
				
	        if (value === undefined || value === null) {
	            return div + styleClass + closeDiv + innerHtml + endDiv;
	        }
	
	        if (value.gradeEarned !== null && value.gradeEarned !== undefined) {
	            innerHtml = value.gradeEarned;
	            if (isNaN(value.gradeEarned)) {
	                styleClass = grid.teardrop.getStyle(value.gradeEarned, null); 
	            } else {
	                styleClass = "numericGradeColumn"; 
	            }
	        } 
	        
	        return div + styleClass + closeDiv + $.jgrid.htmlEncode(innerHtml) + endDiv;
	    }
	
	    function TearDrop(value, options, rowObject) {
	        var div = "<div class=\"",
				closeDiv = "\">",
				endDiv = "</div>",
				styleClass = "",
				innerHtml = "",
				divs = "",
				courseIndex;
				
	        if (value === undefined || value === null) {
	            return divs;
	        }
	
	        for(courseIndex = 0;  courseIndex < value.length; courseIndex++){
	            innerHtml = "";
	            styleClass = "";
	            var course = value[courseIndex];
	            if(course.letterGrade !== null && course.letterGrade !== undefined) {
	                innerHtml = course.letterGrade;
	                styleClass = grid.teardrop.getStyle(course.letterGrade, null);
	                divs = divs + div + styleClass + closeDiv + $.jgrid.htmlEncode(innerHtml) + endDiv;
	            }
	        }
	        return divs;
	    }
	
	    function restLink(value, options, rowObject) {
			var link = options.colModel.formatoptions.link,
			    level = options.colModel.formatoptions.level,
				contextRootPath = util.getContextRootPath();
	      
			if (typeof link !== 'string') {
				return value;
			}
			
			if (level != null && level !== undefined && rowObject.level != null && rowObject.level !== undefined &&
			    level !== rowObject.level) {
				return value;
			}
				
			return '<a href="' + util.getLayoutLink(link, rowObject.id) +'">'+$.jgrid.htmlEncode(value)+'</a>';
		}
		
		function dataColorBox(value, options, rowObject) {
			var cssClass = options.colModel.formatoptions.style;
	      
			if (typeof cssClass === 'string') {
				return '<span class=' + cssClass + '>' + value + '</span>';
			} else {
				return value;
			}
		}

		function searchNameResults(value, options, rowObject) {

			var studentName = restLink(value, options, rowObject);
			
			if (rowObject.name.nickName) {
				return '<div>'+ studentName + '</div><div class="gray">(' + rowObject.name.nickName + ')</div>';
			}
			else {
				return studentName;
			}	
			
		}
		
		return {
			CutPoint: CutPoint,
			CutPointReverse: CutPointReverse,
			PercentBar: PercentBar,
			Lozenge: Lozenge,
			FuelGaugeWithScore: FuelGaugeWithScore,
			FuelGauge: FuelGauge,
			Grade: Grade,
			TearDrop: TearDrop,
			restLink: restLink,
			dataColorBox: dataColorBox,
			searchNameResults: searchNameResults
		};
	}())
);
