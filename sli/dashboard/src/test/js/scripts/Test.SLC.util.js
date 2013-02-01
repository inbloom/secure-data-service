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

/*global module test ok define equal deepEqual notEqual SLC $*/
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
	
	test('Test setContextRootPath method', function () {
		ok(SLC.util.setContextRootPath !== undefined, 'SLC.util setContextRootPath method should be defined');
		ok(typeof SLC.util.setContextRootPath === 'function', 'SLC.util setContextRootPath method should be function');
		
		deepEqual(SLC.util.setContextRootPath("testPath"), true, 'SLC.util setContextRootPath method should accept string as a parameter');
		
		deepEqual(SLC.util.setContextRootPath(2121), false, 'SLC.util setContextRootPath method should not accept number as a parameter');
		
		deepEqual(SLC.util.setContextRootPath(2121), false, 'SLC.util setContextRootPath method should not accept object as a parameter');
	});
	
	test('Test getContextRootPath method', function () {
		ok(SLC.util.getContextRootPath !== undefined, 'SLC.util getContextRootPath method should be defined');
		ok(typeof SLC.util.getContextRootPath === 'function', 'SLC.util getContextRootPath method should be function');
		
		SLC.util.setContextRootPath("testPath");
		deepEqual(typeof SLC.util.getContextRootPath(), 'string', 'SLC.util getContextRootPath method should return a string');
		deepEqual(SLC.util.getContextRootPath(), 'testPath', 'SLC.util getContextRootPath method should return context root path');
	});
	
	test('Test numbersFirstComparator method', function () {
		ok(SLC.util.numbersFirstComparator !== undefined, 'SLC.util.numbersFirstComparator method should be defined');
		ok(typeof SLC.util.numbersFirstComparator === 'function', 'SLC.util numbersFirstComparator method should be function');
	});
	
	test('Test sortObject method', function () {
		var testObject, sortedObject;
		
		ok(SLC.util.sortObject !== undefined, 'SLC.util.sortObject method should be defined');
		ok(typeof SLC.util.sortObject === 'function', 'SLC.util sortObject method should be function');
		
		testObject = {0:{style: "color-widget-darkgreen"}, 19:{style: "color-widget-red"}, 11:{style: "color-widget-green"}};
		sortedObject = {0:{style: "color-widget-darkgreen"}, 11:{style: "color-widget-green"}, 19:{style: "color-widget-red"}};
		
		deepEqual(SLC.util.sortObject(testObject, SLC.util.compareInt), sortedObject, "Method should return sorted objects");
		deepEqual(SLC.util.sortObject(testObject), sortedObject, "Method should return sorted objects even if compare function is not passed as a parameter");
	});
	
	test('Test setTableId method', function () {
		ok(SLC.util.setTableId !== undefined, 'SLC.util setTableId method should be defined');
		ok(typeof SLC.util.setTableId === 'function', 'SLC.util setTableId method should be function');
		
		deepEqual(SLC.util.setTableId("testTableId"), true, 'SLC.util setTableId method should accept string as a parameter');
		
		deepEqual(SLC.util.setTableId(2121), false, 'SLC.util setTableId method should not accept number as a parameter');
		
		deepEqual(SLC.util.setTableId(2121), false, 'SLC.util setTableId method should not accept object as a parameter');
	});
	
	test('Test getTableId method', function () {
		ok(SLC.util.getTableId !== undefined, 'SLC.util getTableId method should be defined');
		ok(typeof SLC.util.getTableId === 'function', 'SLC.util getTableId method should be function');
		
		SLC.util.setTableId("testTableId");
		deepEqual(typeof SLC.util.getTableId(), 'string', 'SLC.util getTableId method should return a string');
		deepEqual(SLC.util.getTableId(), 'testTableId', 'SLC.util getTableId method should return a string');
	});
	
	test('Test checkAjaxError method', function () {
		ok(SLC.util.checkAjaxError !== undefined, 'SLC.util checkAjaxError method should be defined');
		ok(typeof SLC.util.checkAjaxError === 'function', 'SLC.util checkAjaxError method should be function');
	});
	
	test('Test goToLayout method', function () {
		ok(SLC.util.goToLayout !== undefined, 'SLC.util goToLayout method should be defined');
		ok(typeof SLC.util.goToLayout === 'function', 'SLC.util goToLayout method should be function');
	});
	
	test('Test getLayoutLink method', function () {
		ok(SLC.util.getLayoutLink !== undefined, 'SLC.util getLayoutLink method should be defined');
		ok(typeof SLC.util.getLayoutLink === 'function', 'SLC.util getLayoutLink method should be function');
	});
	
	test('Test checkCondition method', function () {
		ok(SLC.util.checkCondition !== undefined, 'SLC.util checkCondition method should be defined');
		ok(typeof SLC.util.checkCondition === 'function', 'SLC.util checkCondition method should be function');
	});

module("SLC.util displayErrorMessage & hideErrorMessage methods", {
	setup: function () {
		$("body").append("<div id=dsh_dv_error></div><div id=viewSelection></div>");
	},
	
	teardown: function () {
		$("#dsh_dv_error").remove();
		$("#viewSelection").remove();
	}
});
	
	test('Test displayErrorMessage method', function () {
		ok(SLC.util.displayErrorMessage !== undefined, 'SLC.util displayErrorMessage method should be defined');
		ok(typeof SLC.util.displayErrorMessage === 'function', 'SLC.util displayErrorMessage method should be function');
		
		SLC.util.displayErrorMessage("test error");
		
		equal($("#dsh_dv_error").css("display"), "block");
		equal($("#dsh_dv_error").html(), "test error");
		equal($("#viewSelection").css("display"), "none");
	});
	
	test('Test hideErrorMessage method', function () {
		ok(SLC.util.hideErrorMessage !== undefined, 'SLC.util hideErrorMessage method should be defined');
		ok(typeof SLC.util.hideErrorMessage === 'function', 'SLC.util hideErrorMessage method should be function');
		
		SLC.util.hideErrorMessage();
		equal($("#dsh_dv_error").css("display"), "none");
	});
	
module("SLC.util renderLozenges method", {
	setup: function () {
		var testData = {
			data: {},
			config: {
				listOfStudents: {
					type: "PANEL",
					data: {}
				},
				listOfStudentsPage: {
					type: "LAYOUT"
				},
				populationWidget: {
					type: "PANEL"
				}
			},
			widgetConfig: [{
               id: "lozenge",
               type: "WIDGET", 
               items: [{
	               description: "English Language Learner", style: "lozenge-widget-ell", name: "ELL", type: "FIELD",
	               condition: {
						field: "limitedEnglishProficiency",
						value: ["Limited"]
					}
               }]},
               {"id":"population","type":"WIDGET"}
			]
		};
		
		SLC.dataProxy.loadAll(testData);
	},
	teardown: function () {
	}
});
	
	test('Test renderLozenges method', function () {
		ok(SLC.util.renderLozenges !== undefined, 'SLC.util renderLozenges method should be defined');
		ok(typeof SLC.util.renderLozenges === 'function', 'SLC.util renderLozenges method should be function');
		
		var studentData = {
			entityType: "student",
			gradeLevel: "Eighth grade",
			limitedEnglishProficiency: "Limited"
		};
		var output = "<span class=\"lozenge-widget lozenge-widget-ell\">ELL</span>";
		deepEqual(typeof SLC.util.renderLozenges(studentData), "string");
		deepEqual(SLC.util.renderLozenges(studentData), output);
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
		$("body").append("<div id='test' style='font-size: 16px;'>Sample text</div>");
	},
	teardown: function () {
		$("#test").remove();
	}
});

	test('Test getStyleDeclaration method', function () {
		ok(SLC.util.getStyleDeclaration !== undefined, 'SLC.util getStyleDeclaration method should be defined');
		ok(typeof SLC.util.getStyleDeclaration === 'function', 'SLC.util getStyleDeclaration method should be function');
		deepEqual(typeof SLC.util.getStyleDeclaration(document.getElementById("test")), "object", "The method should return css style declaration object");
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


module("SLC.util setDropDownOptions & selectDropDownOption methods", {
	setup: function () {
		$("body").append("<div id='schoolSelectMenu' class='btn-group'><a href='#' data-toggle='dropdown' id='schoolSelectButton' class='btn dropdown-toggle'><span class='optionText'>Daybreak School District 4529</span><span class='caret'></span></a><ul class='dropdown-menu'></ul></div>");
	},
	teardown: function () {
		$("#schoolSelectMenu").remove();
	}
});

	test('Test setDropDownOptions & selectDropDownOption method', function () {
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
		deepEqual($(".dropdown-menu li").length, 2, "The dropdown options has been set up successfully!");
		
		ok(SLC.util.selectDropDownOption !== undefined, 'SLC.util selectDropDownOption method should be defined');
		ok(typeof SLC.util.selectDropDownOption === 'function', 'SLC.util selectDropDownOption method should be function');
		
		SLC.util.selectDropDownOption("school", 1, true);
		equal($("#schoolSelectMenu li.selected").length, 1);
	});
	
module("SLC.util placeholderFix method", {
	setup: function () {
		$("body").append("<input type='text' id='txt1' placeholder='Enter sample text' width='200'/>");
	},
	
	teardown: function () {
		$("#txt1").remove();
	}
});
	
	test('Test placeholderFix method', function () {
		ok(SLC.util.placeholderFix !== undefined, 'SLC.util placeholderFix method should be defined');
		ok(typeof SLC.util.placeholderFix === 'function', 'SLC.util placeholderFix method should be function');
		
		SLC.util.placeholderFix();
		equal($("#txt1").val(), "Enter sample text");
	});
