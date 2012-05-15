<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#attempt>
<html>
<head>
<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/searchResults.css" media="screen" />
<script type="text/javascript">
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
    if (dataModel.studentSearch.numResults == 0) {
        document.getElementById("noSearchResultsDiv").innerHTML = no_result_string;
        }
}
</script>
</head>
<body  onLoad="noSearchResults()">


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
            if (dataModel.studentSearch.numResults == 1){
                     var result_string = ' result.' 
            } else {
                     var result_string = ' results.'
            }
            document.write('Your search for \"'+ dataModel.studentSearch.searchString +'\" \
                           returned ' + dataModel.studentSearch.numResults + result_string);
            
                            
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
                    </div>
                </div>
        </div>
        
    </div>
    
</div>
</body>
</html>
<#include "layout_footer.ftl">
<#recover>
${logger.error(.error)}
<#include "../error.ftl">
</#attempt>
