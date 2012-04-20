/*
 *  Draws a fuel gauge widget using raphaeljs on the element identified by id. 
 *  
 *  
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
    this.PADDING = undefined; // paddings between levels, in pixel
    this.WIDTH = undefined;
	this.HEIGHT = undefined;
	this.BACKGROUNDCOLOUR = DashboardUtil.CutPoints.PERF_LEVEL_TO_COLOR[0]; // background colour of rectangles
    
    this.element = element;
    this.score = score;
    this.cutpoints = cutpoints;

}

FuelGaugeWidget.prototype.setSize = function(width, height) {
	this.WIDTH = width;
	this.HEIGHT = height;
}
// Do the actual drawing. 
FuelGaugeWidget.prototype.create = function()  
{  
    if (this.WIDTH == null || this.WIDTH == undefined || this.HEIGHT == null || this.HEIGHT == null) {
    	throw("Width and Height for the Fuel Gauge are not set");
    }
    // calculate the width of level
    var fullLevelRectWidth = this.WIDTH / (this.cutpoints.length - 1);
    this.PADDING =  fullLevelRectWidth / 10;
    this.PADDING -= this.PADDING % 1;//remove decimal points
    fullLevelRectWidth -= this.PADDING;
    
    var scoreWidth = 0;
    var backgroundWidth = 0;
    var rects = new Array();
    var colorCode = 0;
    if (this.score != null && this.score != undefined) {
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
    		} else if (this.score >= this.cutpoints[i]) {
    			// in the ith level
    			rects[i] = (this.score - this.cutpoints[i]) / 
                       (this.cutpoints[i+1] - this.cutpoints[i]) * fullLevelRectWidth;
    			scoreWidth += rects[i];
    			colorCode++;
    		} else {
    			// lower than the ith level
    		}
    	}
    } else {
		scoreWidth = 0;
		colorCode = 0;
	}
    
    color = DashboardUtil.CutPoints.PERF_LEVEL_TO_COLOR[colorCode];
    if (color == null || color == undefined) {
    	color = this.BACKGROUNDCOLOUR;
    }

    // Now call raphael.
    this.paper = Raphael(this.element, this.WIDTH, this.HEIGHT);
    // draw background first
    this.paper.rect(0, 0, backgroundWidth, this.HEIGHT, 5)
                  .attr("fill", this.BACKGROUNDCOLOUR)
                  .attr("stroke", "none");
                  
    this.paper.rect(0, 0, scoreWidth, this.HEIGHT, 5)
                  .attr("fill", color)
                  .attr("stroke", "none");
    var rightCorner = 0;
    if ((5 - backgroundWidth + scoreWidth) > 0) {
    	rightCorner = 5 - backgroundWidth + scoreWidth;
    } 
    
    if ((5 - scoreWidth) < 0) {
    	this.paper.rect(scoreWidth-5, 0, 5, this.HEIGHT, rightCorner)
    			  .attr("fill", color)
    			  .attr("stroke", "none");
    }
    
    var gapPosition = 0;
    var gapColor = "white";
    for (var i = 0; i < this.cutpoints.length - 2; i++) {
    	gapPosition += fullLevelRectWidth;
		this.paper.rect(gapPosition, 0, this.PADDING, this.HEIGHT, 0)
                  .attr("fill", gapColor)
                  .attr("stroke", "none");
        gapPosition += this.PADDING;
    }
};  
