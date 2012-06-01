/*global SLC Raphael*/

SLC.FuelGauge = function (element, score, cutpoints) {

	return function(element, score, cutpoints) {
		// constants
	    this.PADDING = undefined; // paddings between levels, in pixel
	    this.WIDTH = undefined;
		this.HEIGHT = undefined;
		this.BACKGROUNDCOLOUR = SLC.cutPoints.PERF_LEVEL_TO_COLOR[0]; // background colour of rectangles
	    
	    this.ELEMENT = element;
	    this.SCORE = score;
	    this.CUTPOINTS = cutpoints;
	    
		function setSize(width, height) {
			this.WIDTH = width;
			this.HEIGHT = height;
		}
		
		// Do the actual drawing. 
		function create() {  
		    var color,
				fullLevelRectWidth,
				scoreWidth = 0,
				backgroundWidth = 0,
				rects = [],
				colorCode = 0,
				rightCorner = 0,
				gapPosition = 0,
				gapColor = "white",
				i, j; 
		    
		    if (this.WIDTH === null || this.WIDTH === undefined || this.HEIGHT === null || this.HEIGHT === null) {
				throw("Width and Height for the Fuel Gauge are not set");
		    }
		    // calculate the width of level
		    fullLevelRectWidth = this.WIDTH / (this.CUTPOINTS.length - 1);
		    this.PADDING =  fullLevelRectWidth / 10;
		    this.PADDING -= this.PADDING % 1;//remove decimal points
		    fullLevelRectWidth -= this.PADDING;
		    
		    if (this.SCORE !== null && this.SCORE !== undefined) {
				for (i = 0; i < this.CUTPOINTS.length - 1; i++) {
					backgroundWidth += fullLevelRectWidth;
					if ( i != (this.CUTPOINTS.length - 1)) {
						backgroundWidth += this.PADDING;
					}
					if (this.SCORE > this.CUTPOINTS[i+1]) {
						// higher than the ith level
						rects[i] = fullLevelRectWidth;
						scoreWidth += fullLevelRectWidth + this.PADDING;
						colorCode++;
					} else if (this.SCORE >= this.CUTPOINTS[i] && this.SCORE !== 0) {
								
						var cutPointsRange = (this.CUTPOINTS[i+1] - this.CUTPOINTS[i]);
						var scoreRange =  (this.SCORE - this.CUTPOINTS[i]) + 1;
						if (this.CUTPOINTS[i] === 0 ) {
							if (i+1 !== this.CUTPOINTS.length - 1) {
								cutPointsRange -= 1;
							}
							
							scoreRange -= 1;
						} else if (i+1 === this.CUTPOINTS.length - 1) {
							cutPointsRange += 1;
						}
						// in the ith level
						rects[i] = scoreRange / cutPointsRange * fullLevelRectWidth;
						scoreWidth += rects[i];
						colorCode++;
					}
				}
		    } else {
				scoreWidth = 0;
				colorCode = 0;
			}
		    
			color = SLC.cutPoints.PERF_LEVEL_TO_COLOR[colorCode];
			if (color === null || color === undefined) {
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
		                  
		    if ((5 - backgroundWidth + scoreWidth) > 0) {
				rightCorner = 5 - backgroundWidth + scoreWidth;
		    } 
		    
		    if ((5 - scoreWidth) < 0) {
				this.paper.rect(scoreWidth-5, 0, 5, this.HEIGHT, rightCorner)
						.attr("fill", color)
						.attr("stroke", "none");
			}
		    
			for (j = 0; j < this.CUTPOINTS.length - 2; j++) {
				gapPosition += fullLevelRectWidth;
				this.paper.rect(gapPosition, 0, this.PADDING, this.HEIGHT, 0)
						.attr("fill", gapColor)
						.attr("stroke", "none");
				gapPosition += this.PADDING;
		    }
		}
	};
};