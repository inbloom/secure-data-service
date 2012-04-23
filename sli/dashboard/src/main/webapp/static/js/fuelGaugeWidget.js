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
function FuelGaugeWidget (element, score, cutpoints) { 

    // constants
    this.PADDING = 2; // paddings between levels, in pixel
    this.BACKGROUNDCOLOUR = "#eeeeee"; // background colour of rectangles

    this.element = element;
    this.score = score;
    this.cutpoints = cutpoints;

}

// Do the actual drawing. 
FuelGaugeWidget.prototype.create = function()  
{  
    // Check we have all the information to draw a fuel gauge. 
    var fontSize = 10;//DashboardUtil.getElementFontSize(element);
    var color = 'black'//DashboardUtil.getElementColor(element);
    var width = '100'//DashboardUtil.getElementWidth(element);

    // missing info. Return an error. 
    if (!fontSize || !width || !color || isNaN(fontSize) || isNaN(width)) {
	throw ("Fuel Gauge widget: font size, color, and width property must be defined for elementID: " + this.id);
	return; 
    }

    // calculate the width of level
    var fullLevelRectWidth = width / (this.cutpoints.length - 1)
    fullLevelRectWidth -= this.PADDING;
    
    var scoreWidth = 0;
    var backgroundWidth = 0;
    var rects = new Array();
    var colorCode = 1;
    for (var i = 0; i < this.cutpoints.length - 1; i++) {
    	backgroundWidth += fullLevelRectWidth;
    	if ( i != (this.cutpoints.length - 1)) {
    		backgroundWidth += this.PADDING;
    	}
		if (this.score > this.cutpoints[i+1]) {
	    	// higher than the ith level
	    	rects[i] = fullLevelRectWidth;
	    	scoreWidth += fullLevelRectWidth + this.PADDING;
	    	colorCode++;
		} else if (this.score > this.cutpoints[i]) {
	    	// in the ith level
	    	rects[i] = (this.score - this.cutpoints[i]) / 
                       (this.cutpoints[i+1] - this.cutpoints[i]) * fullLevelRectWidth;
		    scoreWidth += rects[i];
		} else {
	    // lower than the ith level
		}
    }
    
    switch (colorCode) {
    	case 1: color = "#b40610"; break;
    	case 2: color = "#e58829"; break;
    	case 3: color = "#dfc836"; break;
    	case 4: color = "#7fc124"; break;
    	case 5: color = "#438746"; break;
    }
    

    // Now call raphael.
    this.paper = Raphael(this.element, width, fontSize);
    // draw background first
    this.paper.rect(0, 0, backgroundWidth, fontSize, 5)
                  .attr("fill", this.BACKGROUNDCOLOUR)
                  .attr("stroke", "none");
                  
    this.paper.rect(0, 0, scoreWidth, fontSize, 5)
                  .attr("fill", color)
                  .attr("stroke", "none");
    var rightCorner = 0;
    if ((5 - backgroundWidth + scoreWidth) > 0) {
    	rightCorner = 5 - backgroundWidth + scoreWidth;
    } 
    
    if ((5 - scoreWidth) < 0) {
    	this.paper.rect(scoreWidth-5, 0, 5, fontSize, rightCorner)
    			  .attr("fill", color)
    			  .attr("stroke", "none");
    }
    
    var gapPosition = 0;
    var gapColor = "white";
    for (var i = 0; i < this.cutpoints.length - 2; i++) {
    	gapPosition += fullLevelRectWidth;
		this.paper.rect(gapPosition, 0, this.PADDING, fontSize, 0)
                  .attr("fill", gapColor)
                  .attr("stroke", "none");
        gapPosition += this.PADDING;
    }
};  
