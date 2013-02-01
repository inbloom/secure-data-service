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
 * SLC search results
 * Handles all the methods related search results
 */
/*global SLC $ */

SLC.namespace('SLC.searchResults', (function () {
	
		var dataModel = SLC.dataProxy.getData("studentSearchResults"),
			no_result_string,
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
		
		function setup() {
			var i;
			
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


		}
		
		function gotoURL(id) {
			var postPageNum = dataModel.searchPageNum,
				postPageSize = dataModel.searchPageSize,
				psSelect,
				params;
				
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

			params = 'name=' + getParameterByName("name") + '&pageNumber=' + postPageNum +
			'&pageSize=' + postPageSize;
			
			util.goToLayout('studentSearch', null, params);
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


		return {
			setup: setup
		};

		
	}())
);
