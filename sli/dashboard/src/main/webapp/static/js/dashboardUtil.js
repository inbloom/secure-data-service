/*
 * dashboardUtil: contains javascript utility functions useful to dashbaord
 */


var counterInt = 1;

counter = function() {
	counterInt ++;
	return counterInt;
}

DashboardUtil = {
		widgetConfig: {}
};
if (typeof widgetConfigArray != 'undefined') {
for (var i in widgetConfigArray) {
	DashboardUtil.widgetConfig[widgetConfigArray[i].id] = widgetConfigArray[i];
}
}
DashboardUtil.getWidgetConfig = function(widgetName) {
	return DashboardUtil.widgetConfig[widgetName];
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
			for (var cutPoint in cutPoints) {
				color = cutPoints[cutPoint].color;
				if (value - cutPoint <= 0) {
					break;
			    }
		    }
			return "<span style='color:" + cutPoints[cutPoint].color + "'>" + value + "</span>";
		},
		CutPointReverse : function(value, options, rowObject) {
			if (!value && value != 0) {
				return '';
			}
			var cutPoints = DashboardUtil.sortObject(options.colModel.formatoptions.cutPoints, compareInt);
			var color = "#cccccc";
			for (var cutPoint in cutPoints) {
				if (value - cutPoint < 0) {
					break;
			    }
				color = cutPoints[cutPoint].color;
		    }
			return "<span style='color:" + color + "'>" + value + "</span>";
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
		}
};

DashboardUtil.Grid.Sorters = {
		Enum: function(params) {
			var enumHash = {};
			for (var i in params.sortEnum) {
				enumHash[params.sortEnum[i]] = i;
			}
			return function(value, rowObject) {
				var i = enumHash[value];
				return i ? i : -1;
			}
			
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
	var config = DashboardUtil.getWidgetConfig("lozenge");
	var item, condition, configItem;
	var lozenges = '';
	for (var i in config.items) {
		configItem = config.items[i];
		condition = configItem.condition;
		item = student[condition.field];
		if (item) {
			for (var y in condition.value) {
				if (condition.value[y] == item) {
					lozenges += '<span class="' + configItem.style + '">' + configItem.name + '</span>';
				}
			}
		}
	}
	return lozenges;
};

DashboardUtil.getData = function(componentId, queryString, callback) {
	$.ajax({
		  url: contextRootPath + '/service/data/' + componentId + '?' + queryString,
		  success: callback});
}

DashboardUtil.getPageUrl = function(componentId, queryString) {
	return contextRootPath + '/service/layout/' + componentId + ((queryString) ? ('?' + queryString) : '');
}