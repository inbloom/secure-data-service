/*
 *  Draws a lozenge widget using raphaeljs on the element identified by id. 
 *  
 *  The element must have the following styles defined for it: 
 *    font-size
 *    color
 *    width
 */ 

// Constructor
/**
 * parameters: 
 *  id : the id of the DOM element to draw the fuel gauge widget on. 
 *  label: a 3-character string. 
 *  color: a string indicating the color. 
 *  style: a string describing how lozenge looks. Currently "solid" or "hollow"
 */
function LozengeWidget (id, label, color, style) { 

    // constants
    this.STROKEWIDTH = 1;
    this.WHITE = "#FFFFFF";

    this.id = id;
    this.label = label;
    this.color = color;
    this.style = style;

}

// Do the actual drawing. 
LozengeWidget.prototype.create = function()  
{  
    // Check we have all the information to draw a fuel gauge. 
    var element = document.getElementById(this.id);
    var fontSize = DashboardUtil.getElementFontSize(element);
    var height = DashboardUtil.getElementHeight(element);
    var width = DashboardUtil.getElementWidth(element);

    // missing info. Return an error. 
    if (!fontSize || !width || !height || isNaN(fontSize) || isNaN(width)) {
	throw ("Lozenge widget: font size, height, and width property must be defined for elementID: " + this.id);
	return; 
    }

    // Now call raphael.
    this.paper = Raphael(element, width, height);

    var rectOpacity = this.style == "solid" ? 1 : 0;
    var cornerRadius = Math.floor(width / 4);
    var textColor = this.style == "solid" ? this.WHITE : this.color;

    // draw the rectangle
    this.paper.rect(1,1, width-2, height-2, cornerRadius)
              .attr("fill", this.color)
              .attr("stroke", this.color)
              .attr("stroke-width", this.STROKEWIDTH)
              .attr("fill-opacity", rectOpacity);

    // draw the text
    var centerX = Math.floor(width / 2);
    var centerY = Math.floor(height / 2);
    this.paper.text(centerX,centerY, this.label)
              .attr("font-size", fontSize)
              .attr("stroke-width", this.STROKEWIDTH)
              .attr("stroke", textColor);

};  
