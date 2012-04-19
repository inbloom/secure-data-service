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
		load : function(componentId, id, callback) {
			var prx = this;
			$.ajax({
				  url: contextRootPath + '/service/component/' + componentId + '/' + id,
				  scope: this,
				  success: function(panel){
					  prx.data[componentId] = panel.data; 
					  prx.config[componentId] = panel.viewConfig; 
					  callback(panel);
			      }});
		},
		getData: function(componentId) {
			return this.data[componentId];
		},
		getConfig: function(componentId) {
			return this.config[componentId];
		},
		getWidgetConfig: function(widget) {
			return this.widgetConfig[widget];
		}
};


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

jQuery.fn.sliGrid = function(panelConfig, options) {
	var colNames = [];
    var colModel = [];
    var items = [];
    var groupHeaders = [];
    var j;
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
          	    colModelItem.formatter = (eval('typeof ' + item1.formatter) == 'function') ? eval(item1.formatter) : item1.formatter;
            }
            if (item1.sorter) {
            	colModelItem.sorttype = (eval('typeof ' + item1.sorter) == 'function') ? eval(item1.sorter)(item1.params) : item1.sorter;
            }
            if (item1.params) {
        	  colModelItem.formatoptions = item1.params;
            }
            if (item1.align) {
            	colModelItem.align = item1.align;
            }
            colModel.push( colModelItem );
        }
        
    }
    options = jQuery.extend(options, {colNames: colNames, colModel: colModel});
    jQuery(this).jqGrid(options);
    if (groupHeaders.length > 0) {
    	jQuery(this).jqGrid('setGroupHeaders', {
      	  useColSpanStyle: false, 
      	  groupHeaders:groupHeaders
      	});
    }
    jQuery(this).removeClass('.ui-widget-header');
    jQuery(this).addClass('.jqgrid-header');
}

DashboardUtil.makeGrid = function (tableId, panelConfig, panelData, options)
{
	// for some reason root config doesn't work with local data, so manually extract
	if (panelConfig.root) {
		panelData = panelData[panelConfig.root];
	}
	gridOptions = { 
	    	data: panelData,
	        datatype: 'local', 
	        height: 'auto',
	        viewrecords: true,
	        rowNum: 10000};
	if (options) {
		gridOptions = jQuery.extend(gridOptions, options);
	}
	return jQuery("#" + tableId).sliGrid(panelConfig, gridOptions); 
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
		
		FuelGauge: function(value, options, rowObject) {
			var name = options.colModel.formatoptions.name;
			var valueField = options.colModel.formatoptions.valueField;
			
			if (name == undefined || valueField == undefined ||  rowObject.assessments[name] == undefined || rowObject.assessments[name][valueField] == undefined ) {
				return "";
			}
			
			var score = rowObject.assessments[name][valueField];
			var fieldName = options.colModel.formatoptions.fieldName;
			var cutpoints = rowObject.assessments[name].assessments.assessmentPerformanceLevel;
			var divId = fieldName + counter();
			var returnValue = "<div id='" + divId + "' style='width: 100px; padding:5px;' align='left'>";
			returnValue += "<script>";
			returnValue += "var cutpoints = new Array(";
			//TODO: Cutpoints should be handled for All assessments.
			for( var i=0;i < cutpoints.length; i++) {
				if (cutpoints[i]["minimumScore"] != null && cutpoints[i]["minimumScore"] != undefined) {
					returnValue += cutpoints[i]["minimumScore"] + ",";
				}
				if (i == cutpoints.length - 1) {
					if (cutpoints[i]["maximumScore"] != null && cutpoints[i]["maximumScore"] != undefined) {
						returnValue += cutpoints[i]["maximumScore"] ;
					}
				}
			}
			returnValue += ");";
			returnValue += "var fuelGuage = new FuelGaugeWidget ('" + divId + "', " + score + ", cutpoints);";
			returnValue += "fuelGuage.create();";
			returnValue += "</script>";
			returnValue += "</div>";
			return  returnValue;
		},

		TearDrop: function(value, options, rowObject) {
			var style = DashboardUtil.tearDrop.getStyle(value, null);

			return "<div class=\"" + style +  "\">" + value + "</div>";
		},
		
		restLink : function(value, options, rowObject)
		{
		  var link = options.colModel.formatoptions.link;
		  if(typeof link == 'string')
		  {
		    return '<a href="'+link + rowObject.id+'">'+value+'</a>';
		  }else{
		    return cellvalue;
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

compareInt = function compare(a,b){return a-b;}
compareIntReverse = function compare(a,b){return b-a;}

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
}

/*
 * Check for ajax error response
 */
DashboardUtil.checkAjaxError = function(XMLHttpRequest, requestUrl)
{
    if(XMLHttpRequest.status != 200) {
        window.location = requestUrl;
    }
}

/*
 * Display generic dashboard error page
 */
DashboardUtil.displayErrorPage = function()
{
    window.location = "/dashboard/static/html/error.html";
}

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
					lozenges += '<div class="lozenge-widget ' + configItem.style + '">' + configItem.name + '</span>';
				}
			}
		}
	}
	return lozenges;
};

DashboardUtil.getPageUrl = function(componentId, queryString) {
	return contextRootPath + '/service/layout/' + componentId + ((queryString) ? ('?' + queryString) : '');
}

DashboardUtil.checkCondition = function(data, condition) {
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
}


DashboardUtil.tearDrop = {};
DashboardUtil.tearDrop.initGradeTrendCodes = function() {

    var GRADE_TREND_CODES = new Object();

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

    return GRADE_TREND_CODES;
}

DashboardUtil.tearDrop.getStyle = function(value, previousValue) {

    GRADE_COLOR_CODES = DashboardUtil.tearDrop.initGradeColorCodes();
    GRADE_TREND_CODES = DashboardUtil.tearDrop.initGradeTrendCodes();

    var color  = "grey";
    if (value != null) {
       color = GRADE_COLOR_CODES[value];
    }

    var trend = "notrend";
    if ((value != null) && (previousValue != null)) {
       var currentTrendCode = GRADE_TREND_CODES[value];
       var previousTrendCode = GRADE_TREND_CODES[previousValue];
       if ((currentTrendCode != null) && (previousTrendCode != null)) {
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

    var styleName = "teardrop" + "-" + color + "-" + trend;

    return styleName;
}

DashboardUtil.tearDrop.initGradeColorCodes = function() {

        var GRADE_COLOR_CODES = new Object();

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

        return GRADE_COLOR_CODES;
}
