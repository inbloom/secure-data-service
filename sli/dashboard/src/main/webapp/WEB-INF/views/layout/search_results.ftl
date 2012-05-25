
<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/searchResults.css" media="screen" />
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/searchResults.js"></script>
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
				<@includePanelModel panelId="studentSearch"/>
            	Your search for "${panelData.searchString}" returned ${panelData.numResults} result<#if panelData.numResults != 1>s</#if>.

                <div class="panel-content">
                    <div id="content">
                        <#-- create header panels -->
                        <#list layout as item>
                            <@includePanelContent panel=item/>
                        </#list>
                <br/>
                <div id="noSearchResultsDiv">
                </div>
				<div id="searchPgnDiv">
					<button id="searchPrevBtn" onclick="gotoURL(this.id)" class="btn" type="button">
						<input type="image" src="${CONTEXT_ROOT_PATH}/static/images/prevPage_icon.png" height = "13px" alt="Prev" />
					</button>
					Page ${panelData.searchPageNum} of ${panelData.searchMaxPageNum}
					<button id="searchNextBtn" onclick="gotoURL(this.id)" class="btn" type="button">
						<input type="image" src="${CONTEXT_ROOT_PATH}/static/images/nextPage_icon.png" height = "13px" alt="Next" />
					</button>
					<select id="pageSizeSelect" onChange="gotoURL(this.id)">
  						<option value="50">50</option>
  						<option value="100">100</option>
					</select>
					Viewing ${(panelData.searchPageNum - 1) * panelData.searchPageSize + 1} -
					<#if (panelData.searchPageNum * panelData.searchPageSize) lte panelData.numResults>${panelData.searchPageNum * panelData.searchPageSize}</#if>
					<#if (panelData.searchPageNum * panelData.searchPageSize) gt panelData.numResults>${panelData.numResults}</#if>
					of ${panelData.numResults}
					<script type="text/javascript">
					setup();
					</script>
				</div>
                    </div>
                </div>
        </div>

    </div>

</div>
