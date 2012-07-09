module("SLC.util");

	test('Test SLC.util Namespace', function () {
		ok(SLC.util !== undefined, 'SLC.dataProxy namespace should be defined');
		ok(typeof SLC.util === 'object', 'SLC.dataProxy should be an object');
	});
	
	test('Test counter method', function () {
		ok(SLC.util.counter !== undefined, 'SLC.util counter method should be defined');
		ok(typeof SLC.util.counter === 'function', 'SLC.util counter method should be function');
		deepEqual(typeof SLC.util.counter(), 'number', 'SLC.util counter method should return a number');
	});
	
	test('Test compareInt method', function () {
		ok(SLC.util.compareInt !== undefined, 'SLC.util compareInt method should be defined');
		ok(typeof SLC.util.compareInt === 'function', 'SLC.util compareInt method should be function');
		deepEqual(typeof SLC.util.compareInt(3, 2), 'number', 'if numbers are passed to a function then it should return a number');
		deepEqual(typeof SLC.util.compareInt("3", "2"), 'number', 'if numbers are passes as a string then it should return a number');
		notEqual(typeof SLC.util.compareInt("test", "2"), 'number', 'if parameter not a number then it should return undefined');
	});
	
	test('Test compareIntReverse method', function () {
		ok(SLC.util.compareIntReverse !== undefined, 'SLC.util compareIntReverse method should be defined');
		ok(typeof SLC.util.compareIntReverse === 'function', 'SLC.util compareIntReverse method should be function');
		deepEqual(typeof SLC.util.compareIntReverse(3, 2), 'number', 'if numbers are passed to a function then it should return a number');
		deepEqual(typeof SLC.util.compareIntReverse("3", "2"), 'number', 'if numbers are passes as a string then it should return a number');
		notEqual(typeof SLC.util.compareIntReverse("test", "2"), 'number', 'if parameter not a number then it should return undefined');
	});
	
	test('Test getContextRootPath method', function () {
		ok(SLC.util.getContextRootPath !== undefined, 'SLC.util getContextRootPath method should be defined');
		ok(typeof SLC.util.getContextRootPath === 'function', 'SLC.util getContextRootPath method should be function');
		deepEqual(typeof SLC.util.getContextRootPath(), 'string', 'SLC.util getContextRootPath method should return a string');
	});

module("SLC.util makeTabs method", {
	setup: function () {
		$("body").append("<div id='tabs'><ul><li><a href='#tabs-1'>Nunc tincidunt</a></li><li><a href='#tabs-2'>Proin dolor</a></li><li><a href='#tabs-3'>Aenean lacinia</a></li></ul><div id='tabs-1'><p>text 1</p></div><div id='tabs-2'><p>text 2</p></div><div id='tabs-3'><p>text 3</p></div></div>");
	},
	teardown: function () {
		$("#tabs").remove();
	}
});
	test('Test makeTabs method', function () {
		ok(SLC.util.makeTabs !== undefined, 'SLC.util makeTabs method should be defined');
		ok(typeof SLC.util.makeTabs === 'function', 'SLC.util makeTabs method should be function');
		
		SLC.util.makeTabs("#tabs");
		equal($("#tabs").find("#tabs-1").length, 1);
		equal($("#tabs").find("#tabs-1").css("display"), "block");
		equal($("#tabs").find("#tabs-2").css("display"), "none");
		
		equal(SLC.util.makeTabs("#tabs1"), false);
	});


module("SLC.util get methods", {
	setup: function () {
		$("body").append("<div id='test'>Sample text</div>");
	},
	teardown: function () {
		$("#test").remove();
	}
});

	test('Test getElementFontSize method', function () {
		ok(SLC.util.getElementFontSize !== undefined, 'SLC.util getElementFontSize method should be defined');
		ok(typeof SLC.util.getElementFontSize === 'function', 'SLC.util getElementFontSize method should be function');
		deepEqual(typeof SLC.util.getElementFontSize(document.getElementById("test")), 'number', 'SLC.util getElementFontSize method should return a number');
	});
	
	test('Test getElementColor method', function () {
		ok(SLC.util.getElementColor !== undefined, 'SLC.util getElementColor method should be defined');
		ok(typeof SLC.util.getElementColor === 'function', 'SLC.util getElementColor method should be function');
		deepEqual(typeof SLC.util.getElementColor(document.getElementById("test")), 'string', 'SLC.util getElementColor method should return a string');
	});
	
	
	test('Test getElementWidth method', function () {
		ok(SLC.util.getElementWidth !== undefined, 'SLC.util getElementWidth method should be defined');
		ok(typeof SLC.util.getElementWidth === 'function', 'SLC.util getElementWidth method should be function');
		deepEqual(typeof SLC.util.getElementWidth("#test"), 'number', 'SLC.util getElementWidth method should return a number');
	});
	
	test('Test getElementHeight method', function () {
		ok(SLC.util.getElementHeight !== undefined, 'SLC.util getElementHeight method should be defined');
		ok(typeof SLC.util.getElementHeight === 'function', 'SLC.util getElementHeight method should be function');
		deepEqual(typeof SLC.util.getElementHeight("#test"), 'number', 'SLC.util getElementHeight method should return a number');
	});
	
	test('Test getData method', function () {
		ok(SLC.util.getData !== undefined, 'SLC.util getData method should be defined');
		ok(typeof SLC.util.getData === 'function', 'SLC.util getData method should be function');
	});
	

module("SLC.util setDropDownOptions method", {
	setup: function () {
		$("body").append("<div id='schoolSelectMenu' class='btn-group'><a href='#' data-toggle='dropdown' id='schoolSelectButton' class='btn dropdown-toggle'><span class='optionText'>Daybreak School District 4529</span><span class='caret'></span></a><ul class='dropdown-menu'></ul></div>");
	},
	teardown: function () {
		$("#schoolSelectMenu").remove();
	}
});

	test('Test setDropDownOptions method', function () {
		ok(SLC.util.setDropDownOptions !== undefined, 'SLC.util setDropDownOptions method should be defined');
		ok(typeof SLC.util.setDropDownOptions === 'function', 'SLC.util setDropDownOptions method should be function');
		
		var name = "school", 
			defaultOptions = null,
			options = [{nameOfInstitution: "East Daybreak Junior High"}, {nameOfInstitution: "DayBreak Central High"}], 
			titleKey = "nameOfInstitution", 
			valueKey = "", 
			autoSelect = true, 
			callback = function () {};
		
		SLC.util.setDropDownOptions(name, defaultOptions, options, titleKey, valueKey, autoSelect, callback); 
		deepEqual($(".dropdown-menu li").length, 2, "The dropdown options has been set up successfully!")
	});