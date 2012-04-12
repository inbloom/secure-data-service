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
          	    colModelItem.formatter = eval(item1.formatter);
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
				if (value - cutPoint <= 0) {
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

DashboardUtil.getLozenges = function(renderTo, student) {
	var config = DashboardUtil.getWidgetConfig("lozenge");
	var item, condition, configItem;
	for (var i in config.items) {
		configItem = config.items[i];
		condition = configItem.condition;
		item = student[condition.field];
		if (item) {
			for (var y in condition.value) {
				if (condition.value[y] == item) {
					var id = renderTo + "-" + counter();
					$("#" + renderTo).append('<span id="' + id + '" class="lozenge"/>');
					$("#" + id).sliLozenge({label: configItem.name, color: configItem.color, style: configItem.style});
				}
			}
		}
	}
};

(function( $ ) {
	  $.widget( "ui.sliLozenge", {
	    options: { 
	    },
	    // These options will be used as defaults if options is not provided
	    baseOptions: { 
	    	strokewidth: 1,
	        white: "#FFFFFF",
	        color: "#000000",
	        label: "N/A",
	        style: "solid",
	        fontSize: "9px"
	    },
	 
	    // Set up the widget
	    _create: function() {
	    	var options = {};
	    	jQuery.extend(options, this.baseOptions, this.options);
	    	// get width, height from the element
	    	options.width = $(this.element[0]).width();
	    	options.height = $(this.element[0]).height();
	    	
	    	// calculate and create rounded rectangle
	        var paper = Raphael(this.element[0], options.width, options.height);

	        // draw the rectangle
	        var rect = paper.rect(1, 1, options.width-2, options.height-2, Math.floor(options.width / 4))
	        rect.attr("fill", options.color);
	        rect.attr("stroke", options.color);
	        rect.attr("stroke-width", options.strokewidth);
	        rect.attr("fill-opacity", options.style == "solid" ? 1 : 0);

	        // draw the text
	        var text = paper.text(Math.floor(options.width / 2), Math.floor(options.height / 2), options.label)
	        text.attr("font-size", options.fontSize)
	        text.attr("stroke-width", options.strokewidth)
	        text.attr("stroke", options.style == "solid" ? options.white : options.color);
	    },
	 
	    // Use the destroy method to clean up any modifications your widget has made to the DOM
	    destroy: function() {
	      $.Widget.prototype.destroy.call( this );
	    }
	  });
	}( jQuery ) );


DashboardUtil.getData = function(componentId, queryString, callback) {
	$.ajax({
		  url: contextRootPath + '/service/data/' + componentId + '?' + queryString,
		  success: callback});
}

DashboardUtil.getPageUrl = function(componentId, queryString) {
	return contextRootPath + '/service/layout/' + componentId + ((queryString) ? ('?' + queryString) : '');
}