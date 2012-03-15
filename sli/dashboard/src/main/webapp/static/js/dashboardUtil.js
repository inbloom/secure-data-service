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

DashboardUtil.makeGrid = function (tableId, panelConfig, mydata)
{
    // set up config data for grid
    var colNames = [];
    var colModel = [];

    for (var i = 0; i < panelConfig.items.length; i++) {
        var item = panelConfig.items[i]; 
        colNames.push(item.name); 
        colModel.push( {name:item.id,index:item.id,width:item.width} );
    }

    // make the grid
    jQuery("#" + tableId).jqGrid({ 
        datatype: "local", 
        height: 200, 
        colNames: colNames, 
        colModel: colModel, 
        multiselect: true, 
        caption: panelConfig.id} ); 

    // populate the grid
    for(var i=0;i<=mydata.length;i++) jQuery("#" + tableId).jqGrid('addRowData',i+1,mydata[i]); 
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

