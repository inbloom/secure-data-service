/*
 * dashboardUtil: contains javascript utility functions useful to dashbaord
 */

DashboardUtil = new Object();

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
            colModel.push( colModelItem );
        }
        
    }
    options = jQuery.extend(options, {colNames: colNames, 
         colModel: colModel
    });
    jQuery(this).jqGrid(options);
    if (groupHeaders.length > 0) {
    	jQuery(this).jqGrid('setGroupHeaders', {
      	  useColSpanStyle: false, 
      	  groupHeaders:groupHeaders
      	});
    }
}

DashboardUtil.makeGrid = function (tableId, panelConfig, panelData)
{
	jQuery("#" + tableId).sliGrid(panelConfig, { 
    	data: panelData,
        datatype: "local", 
        height: 'auto',
        viewrecords: true,
        caption: panelConfig.name} ); 
};

var EnumSorter = function(params) {
	var enumHash = {};
	for (var i in params.sortEnum) {
		enumHash[params.sortEnum[i]] = i;
	}
	return function(value, rowObject) {
		var i = enumHash[value];
		return i ? i : -1;
	}
	
}

function PercentBarFormatter(value, options, rowObject) {
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

function PercentCompleteFormatter(value, options, rowObject) {
	var formatoptions = options.colModel.formatoptions;
	var colorValue = value;
	if (formatoptions && formatoptions.reverse == true) {
    	colorValue = 100 - value;
    }
	var low = 50;
    if (formatoptions && formatoptions.low) {
    	low = formatoptions.low;
    }
    if (value == null || value === "") {
      return "-";
    } else if (colorValue < low) {
      return "<span style='color:red;font-weight:bold;'>" + value + "%</span>";
    } else {
      return "<span style='color:green'>" + value + "%</span>";
    }
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

