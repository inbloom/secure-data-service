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
 * SLC grid repeatHeaderGrid
 * Displays column header for each row in the grid.
 */
/*global SLC $ jQuery*/

SLC.namespace('SLC.grid.repeatHeaderGrid', (function () {
	
		var util = SLC.util;
		
		/*
		 * Creates SLC grid repeatHeadergrid
		 * @param tableId - The container id for grid
		 * @param columnItems
		 * @param panelData
		 * @param options
		 */
		function create(tableId, columnItems, panelData, options) {
			var gridOptions,
				repeatHeaderGridWrapper = "#repeatHeaderGrid" + tableId,
				tableID,
				repeatHeaderGridSection,
				i;
			
			if (columnItems.root && panelData !== null && panelData !== undefined) {
				panelData = panelData[columnItems.root];
			}
			
			gridOptions = { 
				datatype: 'local', 
		        height: 'auto',
		        viewrecords: true,
		        autoencode: true,
		        rowNum: 10000,
		        rownumbers:true
			};
		
	        if (panelData === null || panelData === undefined || panelData.length < 1) {
	            util.displayErrorMessage("There is no data available for your request. Please contact your IT administrator.");
	        } else {
			    if (options) {
					gridOptions = $.extend(gridOptions, options);
			    }
			    for (i = 0; i < panelData.length; i++) {
					tableID = tableId + "_" + i;
					repeatHeaderGridSection = "<div class='ui-widget-no-border repeatHeaderTable" + (i+1) + " p10'><table id=" + tableID + "></table></div>";
					$(repeatHeaderGridSection).appendTo(repeatHeaderGridWrapper);
					gridOptions.data = [panelData[i]];
					$("#" + tableID).slcGrid(columnItems, gridOptions); 
					
				}
				return true;
			}
		}
		
		return {
			create: create
		};
	}())
);
