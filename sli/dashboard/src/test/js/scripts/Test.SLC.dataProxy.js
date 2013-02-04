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

module("SLC.dataProxy");

	test('Test SLC.dataProxy Namespace', function () {
		ok(SLC.dataProxy !== undefined, 'SLC.dataProxy namespace should be defined');
		ok(typeof SLC.dataProxy === 'object', 'SLC.dataProxy should be an object');
	});

module("SLC.dataProxy load methods");
	test('Test loadData method', function () {
		ok(SLC.dataProxy.loadData !== undefined, 'SLC.dataProxy loadData method should be defined');
		ok(typeof SLC.dataProxy.loadData === 'function', 'SLC.dataProxy loadData method should be function');
	});
	
	test('Test loadConfig method', function () {
		ok(SLC.dataProxy.loadConfig !== undefined, 'SLC.dataProxy loadConfig method should be defined');
		ok(typeof SLC.dataProxy.loadConfig === 'function', 'SLC.dataProxy loadConfig method should be function');
	});
	
	test('Test loadWidgetConfig method', function () {
		ok(SLC.dataProxy.loadWidgetConfig !== undefined, 'SLC.dataProxy loadWidgetConfig method should be defined');
		ok(typeof SLC.dataProxy.loadWidgetConfig === 'function', 'SLC.dataProxy loadWidgetConfig method should be function');
	});
	
	test('Test loadAll method', function () {
		ok(SLC.dataProxy.loadAll !== undefined, 'SLC.dataProxy loadAll method should be defined');
		ok(typeof SLC.dataProxy.loadAll === 'function', 'SLC.dataProxy loadAll method should be function');
	});
	
	test('Test load method', function () {
		ok(SLC.dataProxy.load !== undefined, 'SLC.dataProxy load method should be defined');
		ok(typeof SLC.dataProxy.load === 'function', 'SLC.dataProxy load method should be function');
		
		$.fixture['app-loadData'] = function () { 
            ok(true, 'load method should make an ajax call.'); 
			start(); 
		};
		
		stop();
		SLC.dataProxy.load("data", "",function () {});
		
		expect(3);
	});

module("SLC.dataProxy get methods", {
	setup: function () {
		testData = {
			data: {},
			config: {
				listOfStudents: {
					type: "PANEL",
					data: {},
				},
				listOfStudentsPage: {
					type: "LAYOUT"
				},
				populationWidget: {
					type: "PANEL"
				},
				studentProfile: {
					type: "LAYOUT",
					items: [{type: "TAB"}]
				}
			},
			widgetConfig: [
               {"id":"lozenge","type":"WIDGET"},
               {"id":"population","type":"WIDGET"}
			]
		}
		
		SLC.dataProxy.loadAll(testData);
		$("<div id='testTitle'></div>").appendTo("body");
	},
	teardown: function () {
		testData = null;
		$("#testTitle").remove();
	}
});
	test('Test getData method', function () {	
		ok(SLC.dataProxy.getData !== undefined, 'SLC.dataProxy getData method should be defined');
		ok(typeof SLC.dataProxy.getData === 'function', 'SLC.dataProxy getData method should be function');
		
		equal(typeof SLC.dataProxy.getData("listOfStudentsPage"), "object", "if parameter is string then it should return an object");
		equal(SLC.dataProxy.getData({}), false, "if parameter is an object then it should return false");
		equal(SLC.dataProxy.getData(1234), false, "if parameter is number then it should return false");
		equal(typeof SLC.dataProxy.getData("listOfStudentsPage1"), "object", "if config id is not exist then it should return an object");
	});
	test('Test getConfig method', function () {	
		ok(SLC.dataProxy.getConfig !== undefined, 'SLC.dataProxy getConfig method should be defined');
		ok(typeof SLC.dataProxy.getConfig === 'function', 'SLC.dataProxy getConfig method should be function');
		
		equal(typeof SLC.dataProxy.getConfig("listOfStudentsPage"), "object", "if parameter is string then it should return an object");
		equal(SLC.dataProxy.getConfig({}), false, "if parameter is an object then it should return false");
		equal(SLC.dataProxy.getConfig(1234), false, "if parameter is number then it should return false");
		equal(SLC.dataProxy.getConfig("listOfStudentsPage1"), false, "if config id is not exist then it should return false");
	});
	
	test('Test getWidgetConfig method', function () {	
		ok(SLC.dataProxy.getWidgetConfig !== undefined, 'SLC.dataProxy getWidgetConfig method should be defined');
		ok(typeof SLC.dataProxy.getWidgetConfig === 'function', 'SLC.dataProxy getWidgetConfig method should be function');
		
		equal(typeof SLC.dataProxy.getWidgetConfig("lozenge"), "object", "if parameter is string then it should return an object");
		equal(SLC.dataProxy.getWidgetConfig({}), false, "if parameter is an object then it should return false");
		equal(SLC.dataProxy.getWidgetConfig(1234), false, "if parameter is number then it should return false");
		equal(SLC.dataProxy.getWidgetConfig("testWidget1"), false, "if config id is not exist then it should return false");
	});
	
	test('Test getLayoutName method', function () {
		var pageTitle;
		
		ok(SLC.dataProxy.getLayoutName !== undefined, 'SLC.dataProxy getPageTitle method should be defined');
		ok(typeof SLC.dataProxy.getLayoutName === 'function', 'SLC.dataProxy getPageTitle method should be function');
		
		pageTitle = SLC.dataProxy.getLayoutName();
		$("#testTitle").html(pageTitle);
		
		equal($("#testTitle").html(), "inBloom", "Page title should be 'inBloom' if it is not mentioned in the configs");
		
		testData.config.listOfStudentsPage.name = "List of Students Layout";
		
		pageTitle = SLC.dataProxy.getLayoutName();
		$("#testTitle").html(pageTitle);
		
		equal($("#testTitle").html(), "List of Students Layout", "Title should be same as defined in the configs");
	});
	
	test('Test checkTabPanel method', function () {
		ok(SLC.dataProxy.checkTabPanel !== undefined, 'SLC.dataProxy checkTabPanel method should be defined');
		ok(typeof SLC.dataProxy.checkTabPanel === 'function', 'SLC.dataProxy checkTabPanel method should be function');
		
		deepEqual(SLC.dataProxy.checkTabPanel(), true);
	});
