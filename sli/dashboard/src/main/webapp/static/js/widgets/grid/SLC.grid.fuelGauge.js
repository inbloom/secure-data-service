/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
 * SLC grid fuelGauge
 * Returns FuelGauge constructor and all its methods
 */
/*global SLC Raphael*/

SLC.namespace('SLC.grid.FuelGauge', (function (element, score, cutpoints) {
	
		return function(element, score, cutpoints) {
			// constants
			var slcCutPoints = SLC.grid.cutPoints,
				PADDING, // paddings between levels, in pixel
				WIDTH,
				HEIGHT,
				BACKGROUNDCOLOUR = slcCutPoints.PERF_LEVEL_TO_COLOR[0], // background colour of rectangles
			    ELEMENT = element,
				SCORE = score,
				CUTPOINTS = cutpoints,
				paper;
		    
			function setSize(width, height) {
				WIDTH = width;
				HEIGHT = height;
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
			    
			    if (WIDTH === null || WIDTH === undefined || HEIGHT === null || HEIGHT === null) {
					throw("Width and Height for the Fuel Gauge are not set");
			    }
			    // calculate the width of level
			    fullLevelRectWidth = WIDTH / (CUTPOINTS.length - 1);
			    PADDING =  fullLevelRectWidth / 10;
			    PADDING -= PADDING % 1;//remove decimal points
			    fullLevelRectWidth -= PADDING;
			    
			    if (SCORE !== null && SCORE !== undefined) {
					for (i = 0; i < CUTPOINTS.length - 1; i++) {
						backgroundWidth += fullLevelRectWidth;
						if ( i != (CUTPOINTS.length - 1)) {
							backgroundWidth += PADDING;
						}
						if (SCORE > CUTPOINTS[i+1]) {
							// higher than the ith level
							rects[i] = fullLevelRectWidth;
							scoreWidth += fullLevelRectWidth + PADDING;
							colorCode++;
						} else if (SCORE >= CUTPOINTS[i] && SCORE !== 0) {
									
							var cutPointsRange = (CUTPOINTS[i+1] - CUTPOINTS[i]);
							var scoreRange =  (SCORE - CUTPOINTS[i]) + 1;
							if (CUTPOINTS[i] === 0 ) {
								if (i+1 !== CUTPOINTS.length - 1) {
									cutPointsRange -= 1;
								}
								
								scoreRange -= 1;
							} else if (i+1 === CUTPOINTS.length - 1) {
								cutPointsRange += 1;
							}
							
							rects[i] = scoreRange / cutPointsRange * fullLevelRectWidth;
							scoreWidth += rects[i];
							colorCode++;
						}
					}
			    } else {
					scoreWidth = 0;
					colorCode = 0;
				}
			    
				color = slcCutPoints.PERF_LEVEL_TO_COLOR[colorCode];
				if (color === null || color === undefined) {
					color = BACKGROUNDCOLOUR;
			    }
			
			    // Now call raphael.
				paper = Raphael(ELEMENT, WIDTH, HEIGHT);
			    // draw background first
			    paper.rect(0, 0, backgroundWidth, HEIGHT, 5)
			                  .attr("fill", BACKGROUNDCOLOUR)
			                  .attr("stroke", "none");
			                  
			    paper.rect(0, 0, scoreWidth, HEIGHT, 5)
			                  .attr("fill", color)
			                  .attr("stroke", "none");
			                  
			    if ((5 - backgroundWidth + scoreWidth) > 0) {
					rightCorner = 5 - backgroundWidth + scoreWidth;
			    } 
			    
			    if ((5 - scoreWidth) < 0) {
					paper.rect(scoreWidth-5, 0, 5, HEIGHT, rightCorner)
							.attr("fill", color)
							.attr("stroke", "none");
				}
			    
				for (j = 0; j < CUTPOINTS.length - 2; j++) {
					gapPosition += fullLevelRectWidth;
					paper.rect(gapPosition, 0, PADDING, HEIGHT, 0)
							.attr("fill", gapColor)
							.attr("stroke", "none");
					gapPosition += PADDING;
			    }
			}
			
			return {
				setSize: setSize,
				create: create
			};
		};
	}())
);
