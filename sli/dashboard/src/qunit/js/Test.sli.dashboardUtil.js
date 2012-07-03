var contextRootPath = '/dashboard';
		
module("DashboardProxy");

	test('Test DashboardProxy Namespace', function () {
		ok(DashboardProxy !== undefined, 'DashboardProxy namespace should be defined');
		ok(typeof DashboardProxy === 'object', 'DashboardProxy should be an object');
	});

module("DashboardProxy getLayoutName method", {
	setup: function () {
		testConfigs = {
				listOfStudents: {
					type: "PANEL"
				},
				listOfStudentsPage: {
					type: "LAYOUT"
				},
				populationWidget: {
					type: "PANEL"
				}
		}
		
		DashboardProxy.loadConfig(testConfigs);
		$("<div id='testTitle'></div>").appendTo("body");
	},
	teardown: function () {
		DashboardProxy.config = null;
		$("#testTitle").remove();
	}
});

	test('Test getLayoutName method', function () {
		var pageTitle;
		
		ok(DashboardProxy.getLayoutName !== undefined, 'DashboardProxy getPageTitle method should be defined');
		ok(typeof DashboardProxy.getLayoutName === 'function', 'DashboardProxy getPageTitle method should be function');
		
		pageTitle = DashboardProxy.getLayoutName();
		$("#testTitle").html(pageTitle);
		
		equal($("#testTitle").html(), "SLC", "Page title should be 'SLC' if it is not mentioned in the configs");
		
		testConfigs.listOfStudentsPage.name = "List of Students Layout";
		
		pageTitle = DashboardProxy.getLayoutName();
		$("#testTitle").html(pageTitle);
		
		equal($("#testTitle").html(), "List of Students Layout", "Title should be same as defined in the configs");
	});

module("DashboardUtil");

	test('Test DashboardUtil Namespace', function () {
		ok(DashboardUtil !== undefined, 'DashboardUtil namespace should be defined');
		ok(typeof DashboardUtil === 'object', 'DashboardUtil should be an object');
	});
	
	test('Test DashboardUtil getContextRootPath method', function () {
		ok(DashboardUtil.getContextRootPath !== undefined, 'DashboardUtil getContextRootPath method should be defined');
		ok(typeof DashboardUtil.getContextRootPath === 'function', 'DashboardUtil getContextRootPath method should be function');
		deepEqual(typeof DashboardUtil.getContextRootPath(), 'string', 'DashboardUtil getContextRootPath method should return a string');
	});


module("DashboardUtil get methods", {
	setup: function () {
		$("body").append("<div id='test'>Sample text</div>");
	},
	teardown: function () {
		$("#test").remove();
	}
});

	test('Test getElementFontSize method', function () {
		ok(DashboardUtil.getElementFontSize !== undefined, 'DashboardUtil getElementFontSize method should be defined');
		ok(typeof DashboardUtil.getElementFontSize === 'function', 'DashboardUtil getElementFontSize method should be function');
		deepEqual(typeof DashboardUtil.getElementFontSize(document.getElementById("test")), 'number', 'DashboardUtil getElementFontSize method should return a number');
	});
	
	test('Test getElementColor method', function () {
		ok(DashboardUtil.getElementColor !== undefined, 'DashboardUtil getElementColor method should be defined');
		ok(typeof DashboardUtil.getElementColor === 'function', 'DashboardUtil getElementColor method should be function');
		deepEqual(typeof DashboardUtil.getElementColor(document.getElementById("test")), 'string', 'DashboardUtil getElementColor method should return a string');
	});
	
	
	test('Test getElementWidth method', function () {
		ok(DashboardUtil.getElementWidth !== undefined, 'DashboardUtil getElementWidth method should be defined');
		ok(typeof DashboardUtil.getElementWidth === 'function', 'DashboardUtil getElementWidth method should be function');
		deepEqual(typeof DashboardUtil.getElementWidth("#test"), 'number', 'DashboardUtil getElementWidth method should return a number');
	});
	
	test('Test getElementHeight method', function () {
		ok(DashboardUtil.getElementHeight !== undefined, 'DashboardUtil getElementHeight method should be defined');
		ok(typeof DashboardUtil.getElementHeight === 'function', 'DashboardUtil getElementHeight method should be function');
		deepEqual(typeof DashboardUtil.getElementHeight("#test"), 'number', 'DashboardUtil getElementHeight method should return a number');
	});
	

module("DashboardUtil setDropDownOptions method", {
	setup: function () {
		$("body").append("<div id='schoolSelectMenu' class='btn-group'><a href='#' data-toggle='dropdown' id='schoolSelectButton' class='btn dropdown-toggle'><span class='optionText'>Daybreak School District 4529</span><span class='caret'></span></a><ul class='dropdown-menu'></ul></div>");
	},
	teardown: function () {
		$("#schoolSelectMenu").remove();
	}
});

	test('Test setDropDownOptions method', function () {
		ok(DashboardUtil.setDropDownOptions !== undefined, 'DashboardUtil setDropDownOptions method should be defined');
		ok(typeof DashboardUtil.setDropDownOptions === 'function', 'DashboardUtil setDropDownOptions method should be function');
		
		var name = "school", 
			defaultOptions = null,
			options = [{nameOfInstitution: "East Daybreak Junior High"}, {nameOfInstitution: "DayBreak Central High"}], 
			titleKey = "nameOfInstitution", 
			valueKey = "", 
			autoSelect = true, 
			callback = function () {};
		
		DashboardUtil.setDropDownOptions(name, defaultOptions, options, titleKey, valueKey, autoSelect, callback); 
		deepEqual($(".dropdown-menu li").length, 2, "The dropdown options has been set up successfully!")
	});