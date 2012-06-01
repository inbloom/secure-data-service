/*global SLC $ jQuery*/

SLC.grid.teardrop = (function () {
    var GRADE_TREND_CODES = {},
		GRADE_COLOR_CODES = {};

    function initGradeTrendCodes() {
        this.GRADE_TREND_CODES['A+'] = 15;
        this.GRADE_TREND_CODES['A'] = 14;
        this.GRADE_TREND_CODES['A-'] = 13;
        this.GRADE_TREND_CODES['B+'] = 12;
        this.GRADE_TREND_CODES['B'] = 11;
        this.GRADE_TREND_CODES['B-'] = 10;
        this.GRADE_TREND_CODES['C+'] = 9;
        this.GRADE_TREND_CODES['C'] = 8;
        this.GRADE_TREND_CODES['C-'] = 7;
        this.GRADE_TREND_CODES['D+'] = 6;
        this.GRADE_TREND_CODES['D'] = 5;
        this.GRADE_TREND_CODES['D-'] = 4;
        this.GRADE_TREND_CODES['F+'] = 3;
        this.GRADE_TREND_CODES['F'] = 2;
        this.GRADE_TREND_CODES['F-'] = 1;
        
        this.GRADE_TREND_CODES['1'] = 5;
        this.GRADE_TREND_CODES['2'] = 4;
        this.GRADE_TREND_CODES['3'] = 3;
        this.GRADE_TREND_CODES['4'] = 2;
        this.GRADE_TREND_CODES['5'] = 1;
    }

    function initGradeColorCodes() {

        this.GRADE_COLOR_CODES['A+'] = "darkgreen";
        this.GRADE_COLOR_CODES['A'] = "darkgreen";
        this.GRADE_COLOR_CODES['A-'] = "darkgreen";
        this.GRADE_COLOR_CODES['B+'] = "lightgreen";
        this.GRADE_COLOR_CODES['B'] = "lightgreen";
        this.GRADE_COLOR_CODES['B-'] = "lightgreen";
        this.GRADE_COLOR_CODES['C+'] = "yellow";
        this.GRADE_COLOR_CODES['C'] = "yellow";
        this.GRADE_COLOR_CODES['C-'] = "yellow";
        this.GRADE_COLOR_CODES['D+'] = "orange";
        this.GRADE_COLOR_CODES['D'] = "orange";
        this.GRADE_COLOR_CODES['D-'] = "orange";
        this.GRADE_COLOR_CODES['F+'] = "red";
        this.GRADE_COLOR_CODES['F'] = "red";
        this.GRADE_COLOR_CODES['F-'] = "red";
        
        this.GRADE_COLOR_CODES['1'] = "darkgreen";
        this.GRADE_COLOR_CODES['2'] = "lightgreen";
        this.GRADE_COLOR_CODES['3'] = "yellow";
        this.GRADE_COLOR_CODES['4'] = "orange";
        this.GRADE_COLOR_CODES['5'] = "red";
    }

    //Determines the css class of a teardrop
    function getStyle(value, previousValue) {

        var color  = "grey",
			trend = "notrend",
			styleName;
        
        if (value !== null && this.GRADE_COLOR_CODES[value] !== undefined) {
           color = this.GRADE_COLOR_CODES[value];
        }
    
        if ((value !== null) && (previousValue !== null)) {

           var currentTrendCode = this.GRADE_TREND_CODES[value];
           var previousTrendCode = this.GRADE_TREND_CODES[previousValue];
           if ((currentTrendCode !== null) && (previousTrendCode !== null)) {
              var trendCode = currentTrendCode - previousTrendCode;
              if (trendCode > 0) {
                 trend = "uptrend";
              } else if (trendCode < 0) {
                 trend = "downtrend";
              } else {
                 trend = "flattrend";
              }
           }
        }
    
        styleName = "teardrop teardrop" + "-" + color + "-" + trend;
        
        return styleName;
    }

    function init() { 
       this.initGradeColorCodes();
       this.initGradeTrendCodes();
    }
    
    return {
		init: init
    };
}());

SLC.grid.teardrop.init();