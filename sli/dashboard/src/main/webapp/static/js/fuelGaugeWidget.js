/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
    
    this.ELEMENT = element;
    this.SCORE = score;
    this.CUTPOINTS = cutpoints;

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
    var fullLevelRectWidth = this.WIDTH / (this.CUTPOINTS.length - 1);
    this.PADDING =  fullLevelRectWidth / 10;
    this.PADDING -= this.PADDING % 1;//remove decimal points
    fullLevelRectWidth -= this.PADDING;
    
    var scoreWidth = 0;
    var backgroundWidth = 0;
    var rects = new Array();
    var colorCode = 0;
    if (this.SCORE != null && this.SCORE != undefined) {
    	for (var i = 0; i < this.CUTPOINTS.length - 1; i++) {
    		backgroundWidth += fullLevelRectWidth;
    		if ( i != (this.CUTPOINTS.length - 1)) {
    			backgroundWidth += this.PADDING;
    		}
    		if (this.SCORE > this.CUTPOINTS[i+1]) {
    			// higher than the ith level
    			rects[i] = fullLevelRectWidth;
    			scoreWidth += fullLevelRectWidth + this.PADDING;
    			colorCode++;
    		} else if (this.SCORE >= this.CUTPOINTS[i] && this.SCORE != 0) {
    			
    			var cutPointsRange = (this.CUTPOINTS[i+1] - this.CUTPOINTS[i]);
    			var scoreRange =  (this.SCORE - this.CUTPOINTS[i]) + 1;
    			if (this.CUTPOINTS[i] == 0 ) {
    				if (i+1 != this.CUTPOINTS.length - 1) {
    					cutPointsRange -= 1;
    				}
    				
    				scoreRange -= 1;
    			} else if (i+1 == this.CUTPOINTS.length - 1) {
    				cutPointsRange += 1
    			}
    			// in the ith level
    			rects[i] = scoreRange / cutPointsRange * fullLevelRectWidth;
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
    this.paper = Raphael(this.ELEMENT, this.WIDTH, this.HEIGHT);
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
    for (var i = 0; i < this.CUTPOINTS.length - 2; i++) {
    	gapPosition += fullLevelRectWidth;
		this.paper.rect(gapPosition, 0, this.PADDING, this.HEIGHT, 0)
                  .attr("fill", gapColor)
                  .attr("stroke", "none");
        gapPosition += this.PADDING;
    }
};  
