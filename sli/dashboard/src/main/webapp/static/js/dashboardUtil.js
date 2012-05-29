/*
 * dashboardUtil: contains javascript utility functions useful to dashbaord
 */


var counterInt = 1;

counter = function() {
	counterInt ++;
	return counterInt;
}

DashboardUtil = {
		
};

DashboardProxy = {
		data : {},
		config: {},
		widgetConfig: {},
		loadData : function(data) {
			jQuery.extend(this.data, data);
		},
		loadConfig : function(config) {
			jQuery.extend(this.config, config);
		},
		loadWidgetConfig : function(widgetConfigArray) {
			for (var i in widgetConfigArray) {
				this.widgetConfig[widgetConfigArray[i].id] = widgetConfigArray[i];
			}
		},
		loadAll : function(dataConfigObj) {
			jQuery.extend(this.data, dataConfigObj.data);
			jQuery.extend(this.config, dataConfigObj.config);
			this.loadWidgetConfig(dataConfigObj.widgetConfig);
		},
		load : function(componentId, id, callback) {
			var prx = this,
				w_studentListLoader = $("<div></div>").loader();
			
			w_studentListLoader.show();
						
			$.ajax({
				  async: false,
				  url: contextRootPath + '/service/component/' + componentId + '/' + (id ? id : ""),
				  scope: this,
				  success: function(panel){
					  jQuery.extend(prx.data, panel.data);
					  jQuery.extend(prx.config, panel.config);
					  w_studentListLoader.remove();
					  
					  if (jQuery.isFunction(callback))
					    callback(panel);
			      },
			      error: $("body").ajaxError( function(event, request, settings) {
			    	  w_studentListLoader.remove();
			    	  if (request.responseText == "") {
			    		  $(location).attr('href',$(location).attr('href'));
			    	  } else {
			    		  $(location).attr('href', contextRootPath + "/exception");
			    	  }
			      })
			});
		},
		getData: function(componentId) {
			var config = this.getConfig(componentId);
			if (config && config.data && config.data.cacheKey) {
				return this.data[config.data.cacheKey];
			}
				
			return {};
		},
		getConfig: function(componentId) {
			return this.config[componentId];
		},
		getWidgetConfig: function(widget) {
			return this.widgetConfig[widget];
		},
		
		// The function returns the layout name
		getLayoutName: function () {
			var configObj = this.config,
				key,
				obj;
			
			for (key in configObj) {
				obj = configObj[key];
				if(obj.type && obj.type === "LAYOUT" && obj.name) {
					return obj.name;
				}
			}
			
			return "SLI";
		}
};

DashboardUtil.getContextRootPath = function() {
	return contextRootPath;
}

DashboardUtil.getElementFontSize = function (element)
{
    var elemStyle = DashboardUtil.getStyleDeclaration(element);
    return parseInt(elemStyle.fontSize);
};

DashboardUtil.getElementColor = function (element)
{
    var elemStyle = DashboardUtil.getStyleDeclaration(element);
    return elemStyle.color;
};

DashboardUtil.getElementWidth = function (element)
{
    return $(element).width();
};

DashboardUtil.getElementHeight = function (element)
{
    return $(element).height();
};

DashboardUtil.makeTabs = function (element)
{
    $(element).tabs();
};

DashboardUtil.setDropDownOptions = function (name, defaultOptions, options, titleKey, valueKey, autoSelect, callback) {
	var select =  "";
	
	$("#"+name).find("dropdown-menu").html(select);
	var autoSelectOption = -1;
	
	
	if(options === null || options === undefined || options.length == 0) {
                DashboardUtil.displayErrorMessage("There is no data available for your request.  Please contact your IT administrator.");
	} else {
	    	if (options.length == 1 && autoSelect) {
			autoSelectOption = 0;
	    	}
		if (defaultOptions != undefined && defaultOptions != null) {
			jQuery.each(defaultOptions, function(val, displayText) {
				select += "    <li class=\"\"><a href=\"#\" onclick=\"DashboardUtil.hideErrorMessage()\">" + displayText + "</a>" +
				"<input type='hidden' value='"+ val + "' id ='selectionValue' /></li>";
			});
		}
		for(var index = 0; index < options.length; index++) {
			var selected = index == autoSelectOption ? "selected" : "";
			select += "    <li class=\"" + selected + "\"><a href=\"#\" onclick=\"DashboardUtil.hideErrorMessage()\">" +$.jgrid.htmlEncode(options[index][titleKey])+"</a>" +
	    				"<input type='hidden' value='"+ index + "' id ='selectionValue' /></li>";
		}
		
		$("#"+name + "SelectMenu").find(".optionText").html("Choose one");
	}
	
	$("#"+name + "SelectMenu .dropdown-menu").html(select);
	$("#"+name + "SelectMenu .disabled").removeClass("disabled");
	$("#"+name + "SelectMenu .dropdown-menu li").click( function() {
		$("#"+name + "SelectMenu .selected").removeClass("selected");
		$("#"+name + "SelectMenu").find(".optionText").html($(this).find("a").html());
		$("#"+name + "Select").val($(this).find("#selectionValue").val());
		$(this).addClass("selected");
		callback();
	});
  
	$("#"+name + "SelectMenu .selected").click();
	
};

DashboardUtil.selectDropDownOption = function (name, optionValue, doClick) {
	$("#" + name + "SelectMenu .dropdown-menu li").each(function() {
		if (optionValue == $(this).find("#selectionValue").val()) {
			$(this).addClass("selected");
			if (doClick) {
				$(this).click();
			} else {
				$("#" + name + "Select").val(optionValue);
				$("#" + name + "SelectMenu .optionText").html($(this).find("a").html());
			}
		}
	});
};

jQuery.fn.sliGrid = function(panelConfig, options) {
	var colNames = [];
    var colModel = [];
    var items = [];
    var groupHeaders = [];
    var j;
    if (panelConfig.items) {
	    for (var i = 0; i < panelConfig.items.length; i++) {
	        var item = panelConfig.items[i]; 
	        if (item.items && item.items.length > 0) {
	        	items = item.items;
	        	groupHeaders.push({startColumnName: item.items[0].field, numberOfColumns: item.items.length, titleText: item.name});
	        } else {
	        	items = [item];
	        }
	        j = 0;
	        for (var j in items) {
	        	var item1 = items[j];
	        	colNames.push(item1.name); 
	            var colModelItem = {name:item1.field,index:item1.field,width:item1.width};
	            if (item1.formatter) {
	          	    colModelItem.formatter = (DashboardUtil.Grid.Formatters[item1.formatter]) ? DashboardUtil.Grid.Formatters[item1.formatter] : item1.formatter;
	            }
	            if (item1.sorter) {
	            	colModelItem.sorttype = (DashboardUtil.Grid.Sorters[item1.sorter]) ? DashboardUtil.Grid.Sorters[item1.sorter](item1.params) : item1.sorter;
	            }
	            if (item1.params) {
	        	  colModelItem.formatoptions = item1.params;
	            }
	            if (item1.align) {
	            	colModelItem.align = item1.align;
	            }
	            if(item1.style) {
	            	colModelItem.classes = item1.style;
	            }
	            
	            colModelItem.resizable = false; // prevent the user from manually resizing the columns
	            
	            colModel.push( colModelItem );
	        }     
	    }
        options = jQuery.extend(options, {colNames: colNames, colModel: colModel});
    }
    jQuery(this).jqGrid(options);
    if (groupHeaders.length > 0) {
    	jQuery(this).jqGrid('setGroupHeaders', {
      	  useColSpanStyle: true, 
      	  groupHeaders:groupHeaders,
          fixed: true
      	});
    	// not elegant, but couldn't figure out a better way to get to grouped headers
    	var groupRow = $(jQuery(this)[0].grid.hDiv).find('.jqg-second-row-header th:last-child');
    	groupRow.addClass('end');
    }
    jQuery(this).removeClass('.ui-widget-header');
    jQuery(this).addClass('.jqgrid-header');
    var headers = jQuery(this)[0].grid.headers;
    $(headers[headers.length - 1].el).addClass("end"); 
    // extra header added
    if (headers.length > colModel.length) {
    	$(headers[0].el).addClass("end"); 
    }
}

DashboardUtil.makeGrid = function (tableId, columnItems, panelData, options) {

	// for some reason root config doesn't work with local data, so manually extract
	if (columnItems.root && panelData != null && panelData != undefined) {
		panelData = panelData[columnItems.root];
	}
	gridOptions = { 
    	data: panelData,
        datatype: 'local', 
        height: 'auto',
        viewrecords: true,
        autoencode: true,
        rowNum: 10000};

        if(panelData === null || panelData === undefined || panelData.length < 1) {
            DashboardUtil.displayErrorMessage("There is no data available for your request. Please contact your IT administrator.");
        } else {
	    if (options) {
	    	gridOptions = jQuery.extend(gridOptions, options);
	    }
	    return jQuery("#" + tableId).sliGrid(columnItems, gridOptions); 
    }
};

DashboardUtil.Grid = {};
DashboardUtil.Grid.Formatters = {
        /* formatoptions : {1:{color:'green'}, 2:{color:'grey'},...} */
        CutPoint : function(value, options, rowObject) {
            if (!value && value != 0) {
                return '';
            }
            var cutPoints = DashboardUtil.sortObject(options.colModel.formatoptions.cutPoints, compareInt);
            var style;
            for (var cutPoint in cutPoints) {
                style = cutPoints[cutPoint].style;
                if (value - cutPoint <= 0) {
                    break;
                }
            }
            return "<span class='" + cutPoints[cutPoint].style + "'>" + value + "</span>";
        },
        CutPointReverse : function(value, options, rowObject) {
            if (!value && value != 0) {
                return '';
            }
            var cutPoints = DashboardUtil.sortObject(options.colModel.formatoptions.cutPoints, compareInt);
            var style;
            for (var cutPoint in cutPoints) {
                if (value - cutPoint < 0) {
                    break;
                }
                style = cutPoints[cutPoint].style;
            }
            return "<span class='" + style + "'>" + value + "</span>";
        },
        PercentBar: function (value, options, rowObject) {
            if (value == null || value === "") {
              return "";
            }

            var color;
            var colorValue = value;
            var formatoptions = options.colModel.formatoptions;
            if (formatoptions && formatoptions.reverse == true) {
                colorValue = 100 - value;
            }
            var low = 30, medium = 70;
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

            return "<span style='display: inline-block;height: 6px;-moz-border-radius: 3px;-webkit-border-radius: 3px;background:" + color + ";width:" + value * .9 + "%'></span>";
        },
          
        Lozenge: function(value, options, rowObject) {    
            return DashboardUtil.renderLozenges(rowObject);
        },
        
        FuelGaugeWithScore: function(value, options, rowObject) {
            var perfLevelClass = "";
            var name = options.colModel.formatoptions.name;
            var valueField = options.colModel.formatoptions.valueField;
            
            var assessments = (name) ? rowObject.assessments[name]: rowObject.assessments;
            
            if (value === undefined || value === null) {
                return "<span class='fuelGauge-perfLevel'></span>";
            }
            
            if (!assessments || assessments == undefined) {

                if (value == undefined || value == null) {
                    value = "!";
                }
                return "<span class='fuelGauge-perfLevel'>" + value + "</span>" ;
            }
            var score = (assessments[valueField]) ? assessments[valueField] : rowObject[valueField];
            
            if (!score || score == undefined) {
                return "<span class='fuelGauge-perfLevel'>" + value + "</span>" ;
            }
            
            var cutPoints = (assessments.assessments) ? assessments.assessments.assessmentPerformanceLevel : assessments.assessmentPerformanceLevel;
            var cutPointsArray = DashboardUtil.CutPoints.toArray(cutPoints);
            var perfLevel = DashboardUtil.CutPoints.getLevelFromArray(cutPointsArray, score);
            var defaultCutPointsSettings = { 5:{style:'color-widget-darkgreen'}, 4:{style:'color-widget-green'}, 3:{style:'color-widget-yellow'}, 2:{style:'color-widget-orange'}, 1:{style:'color-widget-red'}};
            var cutPointsSettings = (options.colModel.formatoptions.cutPoints) ? options.colModel.formatoptions.cutPoints : defaultCutPointsSettings;
                    
            var cutPointLevel = cutPointsSettings[perfLevel];
            if (cutPointLevel != null && cutPointLevel != undefined) {
                perfLevelClass = cutPointLevel.style;
            }
            
            var width = options.colModel.width;
            if (width != null &&  width != undefined) {
                options.colModel.formatoptions["fuelGaugeWidth"] = Math.round(width * 3/400) * 100;
            }
            options.colModel.formatoptions["cutPointsArray"] = cutPointsArray;
            options.colModel.formatoptions["perfLevel"] = perfLevel;
            options.colModel.formatoptions["perfLevelClass"] = perfLevelClass;
            
            return "<span class='" + perfLevelClass + " fuelGauge-perfLevel'>" + $.jgrid.htmlEncode(value) + "</span>" + DashboardUtil.Grid.Formatters.FuelGauge(value, options, rowObject);
        },
        
        FuelGauge: function(value, options, rowObject) {
            var name = options.colModel.formatoptions.name;
            var valueField = options.colModel.formatoptions.valueField;
            
            var assessments = (name) ? rowObject.assessments[name]: rowObject.assessments;
            if (!assessments || assessments == undefined) {
                return "" ;
            }
            var score = (assessments[valueField]) ? assessments[valueField] : rowObject[valueField];
            
            if (!score || score == undefined || value == undefined || value == null) {
                score = 0;
            }
            
            var fieldName = options.colModel.formatoptions.fieldName;
            var cutPoints = (assessments.assessments) ? assessments.assessments.assessmentPerformanceLevel : assessments.assessmentPerformanceLevel;

            var cutPointsArray = options.colModel.formatoptions["cutPointsArray"];
            if (cutPointsArray == null || cutPointsArray == undefined) {
                cutPointsArray = DashboardUtil.CutPoints.toArray(cutPoints);
            }
            
            var perfLevel = options.colModel.formatoptions["perfLevel"];
            if (perfLevel == null || perfLevel == undefined ) {
                perfLevel = DashboardUtil.CutPoints.getLevelFromArray(cutPointsArray, score);
            }
            
            var perfLevelClass = options.colModel.formatoptions["perfLevelClass"];
            if (perfLevelClass == null || perfLevelClass == undefined) {
                var cutPointLevel = options.colModel.formatoptions.cutPoints[perfLevel];
                if (cutPointLevel != null && cutPointLevel != undefined) {
                    perfLevelClass = cutPointLevel.style;
                } else {
                    perfLevelClass = "";
                }
            }
            
            var divId = fieldName + counter();
            var returnValue = "<div id='" + divId + "' class='fuelGauge " + perfLevelClass + "' >";
            returnValue += "<script>";
            returnValue += "var cutPoints = new Array(" + DashboardUtil.CutPoints.getArrayToString(cutPointsArray) + ");";
            returnValue += "$('#" + divId + "').parent().attr('title', '" + score + "');"; 
            returnValue += "var fuelGauge = new FuelGaugeWidget ('" + divId + "', " + score + ", cutPoints);";
            
            var width = options.colModel.formatoptions["fuelGaugeWidth"];
            if (width == null || width == undefined) {
                width = options.colModel.width;
                if (width != null && width != undefined) {
                    width = Math.round(width * 9 / 100) * 10;
                } else {
                    width = 0;
                }
            }
            
            var height = Math.sqrt(width);
            height -= height % 1;//removing the decimals.
            
            returnValue += "fuelGauge.setSize('" + width + "', '" + height + "');"
            returnValue += "fuelGauge.create();";
            returnValue += "</script>";
            returnValue += "</div>";
            return  returnValue;
        },

        Grade: function(value, options, rowobject) {
            var div = "<div class=\"";
            var closeDiv = "\">";
            var endDiv = "</div>";
            var styleClass = "";
            var innerHtml = "";
            if(value === undefined || value === null) {
                return div + styleClass + closeDiv + innerHtml + endDiv;
            }

            if(value.gradeEarned !== null && value.gradeEarned !== undefined) {
                innerHtml = value.gradeEarned;
                if(isNaN(value.gradeEarned)) {
                    styleClass = DashboardUtil.teardrop.getStyle(value.gradeEarned, null); 
                } else {
                    styleClass = "numericGradeColumn"; 
                }
            } 
            return div + styleClass + closeDiv + $.jgrid.htmlEncode(innerHtml) + endDiv;
        },

        TearDrop: function(value, options, rowObject) {
            var div = "<div class=\"";
            var closeDiv = "\">";
            var endDiv = "</div>";
            var styleClass = "";
            var innerHtml = "";
            var divs = "";
            if(value === undefined || value === null) {
                return divs;
            }

            for(var courseIndex in value){
                innerHtml = "";
                styleClass = "";
                var course = value[courseIndex];
                if(course.letterGrade !== null && course.letterGrade !== undefined) {
                    innerHtml = course.letterGrade;
                    styleClass = DashboardUtil.teardrop.getStyle(course.letterGrade, null)
                    divs = divs + div + styleClass + closeDiv + $.jgrid.htmlEncode(innerHtml) + endDiv;
                }
            }
            return divs;
        },

        restLink : function(value, options, rowObject)
        {
          var link = options.colModel.formatoptions.link;
          if(typeof link == 'string')
          {
            return '<a href="' + contextRootPath + '/' + link + rowObject.id+'">'+$.jgrid.htmlEncode(value)+'</a>';
          }else{
            return value;
          }
        }
};

DashboardUtil.Grid.Sorters = {
        Enum: function(params) {
            var enumHash = {};
            params.sortEnum.sort(DashboardUtil.numbersFirstComparator);
            for (var i in params.sortEnum) {
                enumHash[params.sortEnum[i]] = i;
            }
            return function(value, rowObject) {
                var i = enumHash[value];
                return i ? parseInt(i) : -1;
            }
        },
        
        /**
         * Sort by sortField provided in the params. The field must be int.
         */
        ProxyInt: function(params) {
            var fieldArray = (params.sortField) ? params.sortField.split(".") : [];
            var length = fieldArray.length;
            return function(value, rowObject) {
            	var ret = rowObject, i = 0;
            	// find the field in the rowobject by its path "field.subfield.subsub" and return the value
                while(i < length && (ret = ret[fieldArray[i ++]]));
                return parseInt(ret);
            }
        },

        LetterGrade: function(params) {
            return function(semesterGrades, rowObject) {
                 if(semesterGrades === null || semesterGrades === undefined) {
                     return -1;
                 }
                 if(semesterGrades[0] === null || semesterGrades[0] === undefined) {
                     return -1;
                 }
                 if(semesterGrades[0].letterGrade === null || semesterGrades[0].letterGrade === undefined) {
                     return -1;
                 }

                 var i = DashboardUtil.teardrop.GRADE_TREND_CODES[semesterGrades[0].letterGrade]; 
                 if(i === null || i === undefined) {
                     return -1;
                 }
                 return i ? i : -1;
            }
         },

         LettersAndNumbers: function(params) {
             return function(gradeDate, rowObject) {
                 if(gradeDate === null || gradeDate === undefined) {
                     return -1;
                 }
                 if(gradeDate.gradeEarned === null || gradeDate.gradeEarned === undefined) {
                     return -1;
                 }

                 var i = DashboardUtil.teardrop.GRADE_TREND_CODES[gradeDate.gradeEarned]; 
                 if(i === undefined || i === null) {
                     i = gradeDate.gradeEarned;
                 }
                 return i ? i : -1;
            }
        }
}

DashboardUtil.numbersFirstComparator = function(a,b){
    var aIsNull = a === null;
    var bIsNull = b === null;
    
    if(aIsNull && bIsNull) {
        return 0;
    } else if (aIsNull) {
        return 1;
    } else if (bIsNull) {
        return -1;
    }
    
    var aIsNumber = a.match(/^\d+$/);
    var bIsNumber = b.match(/^\d+$/);
    
    //Need to do numerical comparison. With lexicographical compare,
    //the statement '15' < '2' evaluates to true.
    if(aIsNumber && bIsNumber) {    
        return parseInt(a) - parseInt(b);
    }
    
    //A lexicographical comparison is sufficient for two strings
    if(!aIsNumber && !bIsNumber) {
        return a < b ? -1 : (a == b ? 0 : 1);
    }
    
    //Since we know that one of the values is a number and one is not 
    //and we want numbers to precede strings.
    if(aIsNumber) {
        return -1;
    } else {
        return 1;
    }
}

compareInt = function compare(a,b) { return a-b; };
compareIntReverse = function compare(a,b) { return b-a; };

DashboardUtil.sortObject = function(o, compare) {
    var sorted = {},
    key, a = [];
    for (key in o) {
        if (o.hasOwnProperty(key)) 
        	a.push(key);
    }
    a.sort(compare);
    for (key = 0; key < a.length; key++) {
        sorted[a[key]] = o[a[key]];
    }
    return sorted;
};

/*
 * Check for ajax error response
 */
DashboardUtil.checkAjaxError = function(XMLHttpRequest, requestUrl)
{
    if(XMLHttpRequest.status != 200) {
        window.location = requestUrl;
    }
};

// --- static helper function --- 
// Gets the style object for the element where we're drawing the fuel gauge.
// Returns a CSSStyleDeclaration object 
DashboardUtil.getStyleDeclaration = function (element)
{
    if (window.getComputedStyle) {
        var compStyle = window.getComputedStyle (element, null);
    } else {
	var compStyle = element.currentStyle;
    }
    return compStyle;
};

DashboardUtil.renderLozenges = function(student) {
	var config = DashboardProxy.getWidgetConfig("lozenge");
	var item, condition, configItem;
	var lozenges = '';
	for (var i in config.items) {
		configItem = config.items[i];
		condition = configItem.condition;
		item = student[condition.field];
		if (item) {
			for (var y in condition.value) {
				if (condition.value[y] == item) {
					lozenges += '<span class="lozenge-widget ' + configItem.style + '">' + configItem.name + '</span>';
				}
			}
		}
	}
	return lozenges;
};

/*
 * cutPoints take a specific json structure, which is an array where each element is expected to have a minimum score and a maximum score 
 */
DashboardUtil.CutPoints = {
		
	toArray : function(cutPoints) {
		
		if (cutPoints == null || cutPoints == undefined) {
			return undefined;
		}
		
		var cutPointsArray = new Array();
		var count = 0;
		for( var i=0;i < cutPoints.length; i++) {
			if (cutPoints[i]["minimumScore"] != null && cutPoints[i]["minimumScore"] != undefined) {
				cutPointsArray[count++] = cutPoints[i]["minimumScore"];
			}
			if (i == cutPoints.length - 1) {
				if (cutPoints[i]["maximumScore"] != null && cutPoints[i]["maximumScore"] != undefined) {
					cutPointsArray[count] = cutPoints[i]["maximumScore"] ;
				}
			}
		}
		
		return cutPointsArray;
	},
	
	getLevelFromArray : function(cutPointsArray, score) {
		
		if (cutPointsArray == null || cutPointsArray == undefined || score == null || score == undefined) {
			return -1;
		}
		
		for (var i = 0; i < cutPointsArray.length - 1; i++) {
			if (cutPointsArray[i] <= score &&
				(cutPointsArray[i+1] > score || (cutPointsArray.length - 1 == i+1 && cutPointsArray[i+1] >= score))) {
				return i+1;
			}
		}
		return -1;
	},
	
	getLevelFromcutPoints : function(cutPoints, score) {
		
		if (cutPoints == null || cutPoints == undefined || score == null || score == undefined) {
			return -1;
		}
		
		for( var i=0;i < cutPoints.length; i++) {
			if (score != null && score != undefined &&
				cutPoints[i]["minimumScore"] != null && cutPoints[i]["minimumScore"] != undefined && 
				cutPoints[i]["maximumScore"] != null && cutPoints[i]["maximumScore"] != undefined &&
				cutPoints[i]["minimumScore"] <= score && cutPoints[i]["maximumScore"] >= score) {
					return i+1;
			}
		}
		return -1;
	},

	getArrayToString : function(cutPointsArray) {
		
		if (cutPointsArray == null || cutPointsArray == undefined) {
			return "";
		}
		var returnString = "";
		for (i = 0; i < cutPointsArray.length; i++) {
			if (i != cutPointsArray.length - 1) {
				returnString += cutPointsArray[i] + ", ";
			} else {
				returnString += cutPointsArray[i];
			}
		}
		
		return returnString;
	}
};

DashboardUtil.CutPoints.PERF_LEVEL_TO_COLOR = {
	0: "#eeeeee",
	1: "#b40610",
	2: "#e58829",
	3: "#dfc836",
	4: "#7fc124",
	5: "#438746"
};

DashboardUtil.getData = function(componentId, queryString, callback) {
	$.ajax({
		  url: contextRootPath + '/service/data/' + componentId + '?' + queryString,
		  success: function(panelData){DashboardProxy[componentId] = panelData; callback(panelData);}});
};

DashboardUtil.getPageUrl = function(componentId, queryString) {
	return contextRootPath + '/service/layout/' + componentId + ((queryString) ? ('?' + queryString) : '');
};

DashboardUtil.goToUrl = function(componentId, queryString) {
	window.location = DashboardUtil.getPageUrl(componentId, queryString);
};

DashboardUtil.checkCondition = function(data, condition) {
    if(condition == undefined) {
        return false;
    }
    var validValues = condition.value;
    var values = data[condition.field];
    if (values == undefined || validValues == undefined) {
    	return false;
    }
    for (var j=0; j < validValues.length; j++) {
        for (var k=0; k < values.length; k++) {
            if (validValues[j] == values[k])
                return true;
        }
    } 
    return false;
};

DashboardUtil.displayErrorMessage = function (error){
    $("#losError").show();
    $("#viewSelection").hide();
    $("#losError").html(error);
}

DashboardUtil.hideErrorMessage = function ( ){
    $("#losError").hide();
}

DashboardUtil.teardrop = {
    GRADE_TREND_CODES: {},
    GRADE_COLOR_CODES: {},

    initGradeTrendCodes:  function() {
        this.GRADE_TREND_CODES['A+'] = 15;
        this.GRADE_TREND_CODES['A'] = 14;
        this.GRADE_TREND_CODES['A-'] = 13;
        this.GRADE_TREND_CODES['B+'] = 12;
        this.GRADE_TREND_CODES['B'] = 11;
        this.GRADE_TREND_CODES['B-'] = 10;
        this.GRADE_TREND_CODES['C+'] = 9;
        this.GRADE_TREND_CODES['C'] = 8;
        this.GRADE_TREND_CODES['C-'] = 7;
        this.GRADE_TREND_CODES['D+'] = 6;
        this.GRADE_TREND_CODES['D'] = 5;
        this.GRADE_TREND_CODES['D-'] = 4;
        this.GRADE_TREND_CODES['F+'] = 3;
        this.GRADE_TREND_CODES['F'] = 2;
        this.GRADE_TREND_CODES['F-'] = 1;
        
        this.GRADE_TREND_CODES['1'] = 5;
        this.GRADE_TREND_CODES['2'] = 4;
        this.GRADE_TREND_CODES['3'] = 3;
        this.GRADE_TREND_CODES['4'] = 2;
        this.GRADE_TREND_CODES['5'] = 1;
    },

    initGradeColorCodes:  function() {

        this.GRADE_COLOR_CODES['A+'] = "darkgreen";
        this.GRADE_COLOR_CODES['A'] = "darkgreen";
        this.GRADE_COLOR_CODES['A-'] = "darkgreen";
        this.GRADE_COLOR_CODES['B+'] = "lightgreen";
        this.GRADE_COLOR_CODES['B'] = "lightgreen";
        this.GRADE_COLOR_CODES['B-'] = "lightgreen";
        this.GRADE_COLOR_CODES['C+'] = "yellow";
        this.GRADE_COLOR_CODES['C'] = "yellow";
        this.GRADE_COLOR_CODES['C-'] = "yellow";
        this.GRADE_COLOR_CODES['D+'] = "orange";
        this.GRADE_COLOR_CODES['D'] = "orange";
        this.GRADE_COLOR_CODES['D-'] = "orange";
        this.GRADE_COLOR_CODES['F+'] = "red";
        this.GRADE_COLOR_CODES['F'] = "red";
        this.GRADE_COLOR_CODES['F-'] = "red";
        
        this.GRADE_COLOR_CODES['1'] = "darkgreen";
        this.GRADE_COLOR_CODES['2'] = "lightgreen";
        this.GRADE_COLOR_CODES['3'] = "yellow";
        this.GRADE_COLOR_CODES['4'] = "orange";
        this.GRADE_COLOR_CODES['5'] = "red";
    },

    //Determines the css class of a teardrop
    getStyle: function(value, previousValue) {

        var color  = "grey";
        if (value !== null && this.GRADE_COLOR_CODES[value] !== undefined) {
           color = this.GRADE_COLOR_CODES[value];
        }
    
        var trend = "notrend";
        if ((value !== null) && (previousValue !== null)) {

           var currentTrendCode = this.GRADE_TREND_CODES[value];
           var previousTrendCode = this.GRADE_TREND_CODES[previousValue];
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
    
        var styleName = "teardrop teardrop" + "-" + color + "-" + trend;
        return styleName;
    },

    init: function() { 
       this.initGradeColorCodes();
       this.initGradeTrendCodes();
    }
};

DashboardUtil.teardrop.init();

// Loader widget
$.widget( "SLI.loader", {
	
	options: {
		message: "Loading..."
	},
	
    _create: function() {
        var message = this.options.message;
        this.element
            .addClass( "loader" )
            .html("<div class='message'>" + message + "</div>")
            .appendTo("body");
    },
    
    message: function( message ) {
        if ( message === undefined || typeof message !== "string" ) {
            return this.options.message;
        } else {
            this.options.message = message;
            this.element.find(".message").html(this.options.message);
        }
    },
});
