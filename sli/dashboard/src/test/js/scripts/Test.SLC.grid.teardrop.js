/*global module test ok define equal deepEqual notEqual SLC $*/

module("SLC.grid.teardrop");

	test('Test SLC.grid.teardrop Namespace', function () {
		ok(SLC.grid.teardrop !== undefined, 'SLC.teardrop namespace should be defined');
		ok(typeof SLC.grid.teardrop === 'object', 'SLC.teardrop should be an object');
	});
	
	test('Test init method', function () {
		ok(SLC.grid.teardrop.init !== undefined, 'SLC.grid.teardrop init method should be defined');
		ok(typeof SLC.grid.teardrop.init === 'function', 'SLC.grid.teardrop init method should be function');
	});
	
	test('Test getGradeColorCode method', function () {
		ok(SLC.grid.teardrop.getGradeColorCode !== undefined, 'SLC.grid.teardrop getGradeColorCode method should be defined');
		ok(typeof SLC.grid.teardrop.getGradeColorCode === 'function', 'SLC.grid.teardrop getGradeColorCode method should be function');
		
		equal(SLC.grid.teardrop.getGradeColorCode("A+"), "darkgreen");
		equal(SLC.grid.teardrop.getGradeColorCode({}), false, "SLC.grid.teardrop getGradeColorCode method should return false if parameter passed as an object");
		equal(SLC.grid.teardrop.getGradeColorCode(12345), false, "SLC.grid.teardrop getGradeColorCode method should return false if parameter passed as a number");
	});
	
	test('Test getGradeTrendCodes method', function () {
		ok(SLC.grid.teardrop.getGradeTrendCodes !== undefined, 'SLC.grid.teardrop getGradeTrendCodes method should be defined');
		ok(typeof SLC.grid.teardrop.getGradeTrendCodes === 'function', 'SLC.grid.teardrop getGradeTrendCodes method should be function');
		
		equal(SLC.grid.teardrop.getGradeTrendCodes("A+"), 15);
		equal(SLC.grid.teardrop.getGradeTrendCodes({}), false, "SLC.grid.teardrop getGradeTrendCodes method should return false if parameter passed as an object");
		equal(SLC.grid.teardrop.getGradeTrendCodes(12345), false, "SLC.grid.teardrop getGradeTrendCodes method should return false if parameter passed as a number");
	});
	
	test('Test getStyle method', function () {
		ok(SLC.grid.teardrop.getStyle !== undefined, 'SLC.grid.teardrop getStyle method should be defined');
		ok(typeof SLC.grid.teardrop.getStyle === 'function', 'SLC.grid.teardrop getStyle method should be function');
		
		equal(SLC.grid.teardrop.getStyle("C", null), "teardrop teardrop-yellow-notrend");
		equal(SLC.grid.teardrop.getStyle("C", "A+"), "teardrop teardrop-yellow-downtrend");	
		equal(SLC.grid.teardrop.getStyle(), "teardrop teardrop-grey-flattrend");		
	});