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

module("SLC.population");

	test('Test SLC.population Namespace', function () {
		ok(SLC.population !== undefined, 'SLC.population namespace should be defined');
		ok(typeof SLC.population === 'object', 'SLC.population should be an object');
	});
	
module("SLC.population", {
	setup: function () {
		var populationWidget = '<div id="populationSelect"><div class="menuBox" id="edorgDiv"><h4> District </h4><input type="hidden" id="edOrgSelect" value="0"><div class="btn-toolbar"><div name="edOrgSelectMenu" id="edOrgSelectMenu" class="btn-group"><a href="#" data-toggle="dropdown" id="edOrgSelectButton" class="btn dropdown-toggle"><span class="optionText">Daybreak School District 4529</span><span class="caret"></span></a><ul id="edOrgSelectOptions" class="dropdown-menu">    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">Choose One</a><input type="hidden" id="selectionValue" value="-1"></li>    <li class="selected"><a onclick="SLC.util.hideErrorMessage()" href="#">Daybreak School District 4529</a><input type="hidden" id="selectionValue" value="0"></li></ul></div></div></div><div class="menuBox" id="schoolDiv"><h4> School </h4><input type="hidden" id="schoolSelect" value="0"><div class="btn-toolbar"><div id="schoolSelectMenu" class="btn-group"><a href="#" data-toggle="dropdown" class="btn dropdown-toggle"><span class="optionText">East Daybreak Junior High</span><span class="caret"></span></a></div></div></div><div class="menuBox" id="courseDiv"><h4> Course </h4><input type="hidden" id="courseSelect" value="-1"><div class="btn-toolbar"><div id="courseSelectMenu" class="btn-group"><a href="#" data-toggle="dropdown" class="btn dropdown-toggle"><span class="optionText">8th Grade English</span><span class="caret"></span></a><ul class="dropdown-menu">    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">Choose One</a><input type="hidden" id="selectionValue" value="-1"></li>    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">6th Grade English</a><input type="hidden" id="selectionValue" value="0"></li>    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">6th Grade Math</a><input type="hidden" id="selectionValue" value="1"></li>    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">7th Grade English</a><input type="hidden" id="selectionValue" value="2"></li>    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">7th Grade Math</a><input type="hidden" id="selectionValue" value="3"></li>    <li class=""><a onclick="SLC.util.hideErrorMessage()" href="#">8th Grade English</a><input type="hidden" id="selectionValue" value="4"></li></ul></div></div></div><div class="menuBox" id="sectionDiv"><h4> Section </h4><input type="hidden" id="sectionSelect" value="-1"><div class="btn-toolbar"><div id="sectionSelectMenu" class="btn-group"><a href="#" data-toggle="dropdown" class="btn dropdown-toggle"><span class="optionText">8th Grade English - Sec 6</span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div class="menuBox" id="dbrd_div_pw_go_btn"><div class="btn-toolbar"><div id="sectionSelectMenu" class="btn-group"><button type="submit" class="btn" id="dbrd_btn_pw_go">Go</button></div></div></div></div>';
		//var pophtml = '<div id="populationSelect"><div id="edorgDiv" class="menuBox"><h4> District </h4><input type="hidden" value="" id ="edOrgSelect" /><div class="btn-toolbar"><div class="btn-group" id="edOrgSelectMenu" name="edOrgSelectMenu"><a class="btn dropdown-toggle" id="edOrgSelectButton" data-toggle="dropdown" href="#"><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu" id="edOrgSelectOptions"><li>Daybreak School District 4529</li></ul></div></div></div><div id="schoolDiv" class="menuBox"><h4> School </h4><input type="hidden" value="" id ="schoolSelect" /><div class="btn-toolbar"><div class="btn-group" id="schoolSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#" ><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div id="courseDiv" class="menuBox"><h4> Course </h4><input type="hidden" value="" id ="courseSelect" /><div class="btn-toolbar"><div class="btn-group" id="courseSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#" ><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div id="sectionDiv" class="menuBox"><h4> Section </h4><input type="hidden" value="" id ="sectionSelect" /><div class="btn-toolbar"><div class="btn-group" id="sectionSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#" ><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div id="dbrd_div_pw_go_btn" class="menuBox"><div class="btn-toolbar"><div class="btn-group" id="sectionSelectMenu"><button id="dbrd_btn_pw_go" class="btn" type="submit">Go</button></div></div></div></div>';
		$("body").append(populationWidget);
	},
	teardown: function () {
		$("#populationSelect").remove();
	}
});

test('Test dropdowns', function () {
		equal($("#dbrd_btn_pw_go").length, 1, "The 'Go' button is exist on the page.");
		
		$('#dbrd_btn_pw_go').trigger('click');
		equal($("#edOrgSelect").val(), "0");
		equal($("#schoolSelect").val(), "0");
	});
