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

DashboardUtil.makeGrid = function (tableId, panelConfig, panelData)
{
    // set up config data for grid
    var colNames = [];
    var colModel = [];

    for (var i = 0; i < panelConfig.items.length; i++) {
        var item = panelConfig.items[i]; 
        colNames.push(item.name); 
        var colModelItem = {name:item.field,index:item.field,width:item.width};
        if (item.formatter) {
        	colModelItem.formatter = eval(item.formatter);
        }
        if (item.params) {
        	colModelItem.formatoptions = item.params;
        }
        colModel.push( colModelItem );
    }

    // make the grid
    jQuery("#" + tableId).jqGrid({ 
    	data: panelData,
        datatype: "local", 
        colNames: colNames, 
        colModel: colModel, 
        height: 'auto',
        viewrecords: true,
        caption: panelConfig.name} ); 
};

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

