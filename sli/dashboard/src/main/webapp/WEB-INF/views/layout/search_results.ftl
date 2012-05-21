
<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/searchResults.css" media="screen" />
<script type="text/javascript">
var dataModel = DashboardProxy.getData("studentSearch");
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
    if (dataModel.numResults == 0) {
        document.getElementById("noSearchResultsDiv").innerHTML = no_result_string;
        }
}

function setup() {
	if (dataModel.numResults == 0) {
		document.getElementById("dbrd_search_pagination").style.visibility = "hidden";
	} else {
		if (dataModel.searchPageNum <= 1) {
   			document.getElementById("dbrd_btn_search_prev").disabled = true;
		}
		if (dataModel.searchPageNum == dataModel.searchMaxPageNum) {
   			document.getElementById("dbrd_btn_search_next").disabled = true;
		}
		var psSelect = document.getElementById("pageSizeSelect");
		var i = 0;
		for (i = 0; i < psSelect.options.length; i++) {
			if (psSelect.options[i].value == dataModel.searchPageSize) {
				psSelect.selectedIndex = i;
				break;
			}
		}
	}
	noSearchResults();
}

function gotoURL(id) {
	var postPageNum = dataModel.searchPageNum;
	var postPageSize = dataModel.searchPageSize;
	if (id == "dbrd_btn_search_prev") {
		postPageNum -= 1;
	} else if (id == "dbrd_btn_search_next") {
		postPageNum += 1;
	} else if (id == "pageSizeSelect") {
		postPageNum = 1;
		var psSelect = document.getElementById("pageSizeSelect");
      	postPageSize = psSelect.options[psSelect.selectedIndex].value;
	}
	var params = 'firstName=' + dataModel.firstName + '&lastName=' + dataModel.lastSurname + '&pageNumber=' + postPageNum +
	'&pageSize=' + postPageSize;
  	DashboardUtil.goToUrl('studentSearchPage', params);
}

</script>

<div class="panel-container">
    <div class="panel-left">
    <div class="panel">
    <div class="panel-header">
    Search Results
    </div>
    <div class="panel-content">
        <#include "search_help_text.ftl">
    </div>
    </div>
    </div>

    <div class="panel-right">
        <div class="panel">
            <div class="panel-header">
            </div>
            <script>

            if (dataModel.numResults == 1){
                     var result_string = ' result.'
            } else {
                     var result_string = ' results.'
            }
            document.write('Your search for \"'+ dataModel.searchString +'\" \
                           returned ' + dataModel.numResults + result_string);


            </script>

                <div class="panel-content">
                    <div id="content">
                        <#-- create header panels -->
                        <#list layout as item>
                            <@includePanelContent panel=item/>
                        </#list>
                <br/>
                <div id="noSearchResultsDiv">
                </div>
<div id="dbrd_search_pagination">
<button id="dbrd_btn_search_prev" onclick="gotoURL(this.id)" class="btn" type="button">
<input type="image" src="${CONTEXT_ROOT_PATH}/static/images/glyphicons_210_left_arrow.png" height = "13px" alt="Prev" />
</button>
Page
<script type="text/javascript">
document.write(dataModel.searchPageNum);
</script>
of
<script type="text/javascript">
document.write(dataModel.searchMaxPageNum);
</script>
<button id="dbrd_btn_search_next" onclick="gotoURL(this.id)" class="btn" type="button">
<input type="image" src="${CONTEXT_ROOT_PATH}/static/images/glyphicons_211_right_arrow.png" height = "13px" alt="Next" />
</button>
<select id="pageSizeSelect" onChange="gotoURL(this.id)">
  <option value="50">50</option>
  <option value="100">100</option>
</select>
<script type="text/javascript">
var offset = (dataModel.searchPageNum - 1) * dataModel.searchPageSize;
var max = offset + dataModel.searchPageSize;
if (max > dataModel.numResults) {
	max = dataModel.numResults;
}
document.write('     Viewing ' +  (offset + 1) + '-' + max + ' of ' + dataModel.numResults);
setup();
</script>
</div>
                    </div>
                </div>
        </div>

    </div>

</div>
