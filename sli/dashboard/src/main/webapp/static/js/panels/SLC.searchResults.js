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
 * SLC search results
 * Handles all the methods related search results
 */
/*global SLC $ */

SLC.namespace('SLC.searchResults', (function () {
	
		var dataModel = SLC.dataProxy.getData("studentSearchResults"),
			no_result_string,
			schoolId = "",
			util = SLC.util;
			
		no_result_string = '<h4>I\'m sorry, we do not have results that match your search.</h4><p>There may be a quick fix:</p><ul><li>Are the names spelled correctly?</li><li>Are the names capitalized?</li><li>Is appropriate punctuation included?</li></ul><p>Please check these items and try again.</p><p>OR</p><p>Return to the <a href="#">previous page</a>.</p>';

		function getParameterByName(name)
		{
			name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
			var regexS = "[\\?&]" + name + "=([^&#]*)";
			var regex = new RegExp(regexS);
			var results = regex.exec(window.location.search);
			if(results == null)
				return "";
			else
				return decodeURIComponent(results[1].replace(/\+/g, " "));
		}

		function noSearchResults() {
			document.getElementById("searchPgnDiv").style.visibility = "hidden";
		    document.getElementById("noSearchResultsDiv").innerHTML = no_result_string;
		}

		function getSchoolList() {

			SLC.dataProxy.load('populationWidget', "", function(panel) {
				var instHierarchy = SLC.dataProxy.getData('populationWidget')['root'],
					select =  "",
					autoSelectOption = -1,
					titleKey = "nameOfInstitution",
					id = "id",
					options;

				for (var index = 0; index < instHierarchy.length; index++) {
					options = instHierarchy[index].schools;

					for (var i = 0; i < options.length; i++) {
						var selected = i === autoSelectOption ? "selected" : "";
						select += "    <li class=\"" + selected + "\"><a href=\"javascript:;\">" +$.jgrid.htmlEncode(options[i][titleKey])+"</a>" +
							"<input type='hidden' value='"+ options[i][id] + "' class ='selectionValue' /></li>";
					}
				}

				$("#schoolSelectMenu").find(".optionText").html("Choose one");

				$("#schoolSelectMenu .dropdown-menu").html(select);
				$("#schoolSelectMenu .disabled").removeClass("disabled");
				$("#schoolSelectMenu .dropdown-menu li").click( function() {
					$("#schoolSelectMenu .selected").removeClass("selected");
					$("#schoolSelectMenu").find(".optionText").html($(this).find("a").html());
					$("#schoolSelect").val($(this).find(".selectionValue").val());
					$(this).addClass("selected");
				});
			});


		}
		
		function setup() {
			var i;

			// get school id from query strings
			schoolId = getParameterByName("schoolId");

			// get school list for all available districts
			getSchoolList();
			
			if (dataModel.numResults !== 0) {
				// disable previous button if we are on the first page
				if (dataModel.searchPageNum <= 1) {
					document.getElementById("searchPrevBtn").disabled = true;
				}
				// disable next button if we are on the last page
				if (dataModel.searchPageNum === dataModel.searchMaxPageNum) {
					document.getElementById("searchNextBtn").disabled = true;
				}
				// update the page size drop down box to reflect current page size
				var psSelect = document.getElementById("pageSizeSelect");
				if (!psSelect) {
					return;
				}
				
				for (i = 0; i < psSelect.options.length; i++) {
					if (psSelect.options[i].value === dataModel.searchPageSize) {
						psSelect.selectedIndex = i;
						break;
					}
				}
			} else {
				// pagination interface should not be visible when no search results
				// also, special no results text should be displayed
				noSearchResults();
			}

			if (schoolId === "" || schoolId === undefined) {
				$("#searchResultsSection").hide();
			}
			else {
				$("#searchResultsSection").show();
			}
		}
		
		function gotoURL(id) {
			var postPageNum = dataModel.searchPageNum,
				postPageSize = dataModel.searchPageSize,
				psSelect,
				params,
				schoolIdParam;
				
			if (id === "searchPrevBtn") {
				postPageNum -= 1;
			} else if (id === "searchNextBtn") {
				postPageNum += 1;
			} else if (id === "pageSizeSelect") {
				postPageNum = 1;
				psSelect = document.getElementById("pageSizeSelect");
				if (psSelect) {
					postPageSize = psSelect.options[psSelect.selectedIndex].value;
				}
			}

			// If no dropdown option selected, then it will take school id from query string.
			schoolIdParam = $("#schoolSelect").val() || schoolId;

			params = 'firstName=' + dataModel.firstName + '&lastName=' + dataModel.lastSurname + '&schoolId=' + schoolIdParam + '&pageNumber=' + postPageNum +
			'&pageSize=' + postPageSize;
			
			SLC.util.goToLayout('studentSearch', null, params);
		}
		
		$("#searchPrevBtn").live("click", function () {
			gotoURL(this.id);
		});
		
		$("#searchNextBtn").live("click", function () {
			gotoURL(this.id);
		});
		
		$("#pageSizeSelect").live("change", function () {
			gotoURL(this.id);
		});

		$("#search_btn_go").live("click", function () {
			if ($("#schoolSelect").val() === "" || $("#schoolSelect").val() === undefined) {
				$("#schoolSelectionError").show();
				return false;
			}
			else {
				$("#schoolSelectionError").hide();
				gotoURL(this.id);
			}
		});


		return {
			setup: setup
		};

		
	}())
);