/*
 *  Draws a fuel gauge widget using raphaeljs on the element identified by id. 
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
 *  score: a number. 
 *  cutpoints: an array of numbers. Contains N+1 elements, where N is the total number of levels. The ith level's lowest score is (cutpoints[i]+1) and highest score is cutpoints[i+1]
 */
function FuelGaugeWidget (id, score, cutpoints) { 

    // constants
    this.PADDING = 1; // paddings between levels, in pixel
    this.BACKGROUNDCOLOUR = "#eeeeee"; // background colour of rectangles

    this.id = id;
    this.score = score;
    this.cutpoints = cutpoints;

}

// Do the actual drawing. 
FuelGaugeWidget.prototype.create = function()  
{  
    // Check we have all the information to draw a fuel gauge. 
    var element = document.getElementById(this.id);
    var fontSize = DashboardUtil.getElementFontSize(element);
    var color = DashboardUtil.getElementColor(element);
    var width = DashboardUtil.getElementWidth(element);

    // missing info. Return an error. 
    if (!fontSize || !width || !color || isNaN(fontSize) || isNaN(width)) {
	alert("Fuel Gauge widget: font size, color, and width property must be defined for elementID: " + this.id);
	return; 
    }

    // calculate the widths of each level rectangle
    var fullLevelRectWidth = width / (this.cutpoints.length - 1)
    fullLevelRectWidth -= this.PADDING;
    var rects = new Array();
    for (var i = 0; i < this.cutpoints.length - 1; i++) {
	if (this.score > this.cutpoints[i+1]) {
	    // higher than the ith level
	    rects[i] = fullLevelRectWidth;
	} else if (this.score > this.cutpoints[i]) {
	    // in the ith level
	    rects[i] = (this.score - this.cutpoints[i]) / 
                       (this.cutpoints[i+1] - this.cutpoints[i]) *
		       fullLevelRectWidth;
	} else {
	    // lower than the ith level
	}
    }

    // Now call raphael.
    this.paper = Raphael(element, width, fontSize);
    // draw background first
    for (var i = 0; i < this.cutpoints.length - 1; i++) {
	this.paper.rect(i * (fullLevelRectWidth+this.PADDING),0, fullLevelRectWidth, fontSize, 0)
                  .attr("fill", this.BACKGROUNDCOLOUR)
                  .attr("stroke", "none");
    }
    // draw actual rectangle
    for (var i = 0; i < rects.length; i++) {
	this.paper.rect(i * (fullLevelRectWidth+this.PADDING),0, rects[i], fontSize, 1)
                  .attr("fill", color)
                  .attr("stroke", "none");
    }

};  
