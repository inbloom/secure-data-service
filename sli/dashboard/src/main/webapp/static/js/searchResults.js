var dataModel = SLC.dataProxy.getData("studentSearch");

var no_result_string =
        '<h4>I\'m sorry, we do not have results that match your search.</h4>\
        <p>There may be a quick fix:</p><ul>\
        <li>Are the names spelled correctly?</li>\
        <li>Are the names capitalized?</li>\
        <li>Is appropriate punctuation included?</li>\
        </ul>\
        <p>Please check these items and try again.</p>\
        <p>OR</p>\
        <p>Return to the <a href="#">previous page</a>.</p>';

function noSearchResults() {
	document.getElementById("searchPgnDiv").style.visibility = "hidden";
    document.getElementById("noSearchResultsDiv").innerHTML = no_result_string;
}

function setup() {
	if (dataModel.numResults != 0) {
		// disable previous button if we are on the first page
		if (dataModel.searchPageNum <= 1) {
   			document.getElementById("searchPrevBtn").disabled = true;
		}
		// disable next button if we are on the last page
		if (dataModel.searchPageNum == dataModel.searchMaxPageNum) {
   			document.getElementById("searchNextBtn").disabled = true;
		}
		// update the page size drop down box to reflect current page size
		var psSelect = document.getElementById("pageSizeSelect");
		if (!psSelect) {
			return;
		}
		var i = 0;
		for (i = 0; i < psSelect.options.length; i++) {
			if (psSelect.options[i].value == dataModel.searchPageSize) {
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
	var postPageNum = dataModel.searchPageNum;
	var postPageSize = dataModel.searchPageSize;
	if (id == "searchPrevBtn") {
		postPageNum -= 1;
	} else if (id == "searchNextBtn") {
		postPageNum += 1;
	} else if (id == "pageSizeSelect") {
		postPageNum = 1;
		var psSelect = document.getElementById("pageSizeSelect");
		if (psSelect) {
      		postPageSize = psSelect.options[psSelect.selectedIndex].value;
		}
	}
	var params = 'firstName=' + dataModel.firstName + '&lastName=' + dataModel.lastSurname + '&pageNumber=' + postPageNum +
	'&pageSize=' + postPageSize;
  	SLC.util.goToUrl('studentSearchPage', params);
}

