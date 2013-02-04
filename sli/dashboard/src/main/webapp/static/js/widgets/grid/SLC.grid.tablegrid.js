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
 * SLC grid
 * The module contains SLC grid plugin and grid creation method
 */
/*global SLC $ jQuery*/

SLC.namespace('SLC.grid.tablegrid', (function () {
	
		var util = SLC.util;
		
		/*
		 *	SLC Grid plugin
		 *  @param panelConfig - Panel config data
		 *  @param options
		 *	Example: $("#table1").slcGrid(columnItems, gridOptions)
		 */
		(function ($) {
			$.fn.slcGrid = function (panelConfig, options) {
				var grid = SLC.grid,
					colNames = [],
					colModel = [],
					items = [],
					groupHeaders = [],
					item,
					item1,
					i, j;
					
			    if (panelConfig.items) {
				    for (i = 0; i < panelConfig.items.length; i++) {
				        item = panelConfig.items[i]; 
				        if (item.items && item.items.length > 0) {
							items = item.items;
							groupHeaders.push({startColumnName: item.items[0].field, numberOfColumns: item.items.length, titleText: item.name});
				        } else {
							items = [item];
				        }
						for (j=0; j < items.length; j++) {
							item1 = items[j];
							colNames.push(item1.name); 
				            var colModelItem = {name:item1.field,index:item1.field,width:item1.width};
				            if (item1.formatter) {
								colModelItem.formatter = (grid.formatters[item1.formatter]) ? grid.formatters[item1.formatter] : item1.formatter;
				            }
				            if (item1.sorter) {
								colModelItem.sorttype = (grid.sorters[item1.sorter]) ? grid.sorters[item1.sorter](item1.params) : item1.sorter;
				            }
				            if (item1.params) {
								colModelItem.formatoptions = item1.params;
				            }
				            if (item1.align) {
								colModelItem.align = item1.align;
				            }
				            if(item1.style) {
								colModelItem.classes = item1.style;
				            }
				            
				            colModelItem.resizable = false; // prevent the user from manually resizing the columns
				            
				            colModel.push( colModelItem );
				        }     
				    }
			        options = $.extend(options, {colNames: colNames, colModel: colModel});
			    }
			    $(this).jqGrid(options);
			    $(this).jqGrid('hideCol','rn');
			    // Add css class for column header
			    if (panelConfig.items) {
					for (i = 0; i < panelConfig.items.length; i++) {
						item = panelConfig.items[i];
						if (item.style) {
							$(this).jqGrid('setLabel',item.field, '', item.style);
						}
					}
				}
				
			    if (groupHeaders.length > 0) {
					$(this).jqGrid('setGroupHeaders', {
						useColSpanStyle: true, 
						groupHeaders:groupHeaders,
						fixed: true
					});

					var groupRow = $($(this)[0].grid.hDiv).find('.jqg-second-row-header th:last-child');
					groupRow.addClass('end');
			    }
			    $(this).removeClass('.ui-widget-header');
			    $(this).addClass('.jqgrid-header');
			    var headers = $(this)[0].grid.headers;
			    $(headers[headers.length - 1].el).addClass("end"); 

			    if (headers.length > colModel.length) {
					$(headers[0].el).addClass("end"); 
			    }
			};
		})(jQuery);
		
		/*
		 * Creates SLC grid
		 * @param tableId - The container id for grid
		 * @param columnItems
		 * @param panelData
		 * @param options
		 */
		function create(tableId, columnItems, panelData, options) {
			var gridOptions;
			
			if (columnItems.root && panelData !== null && panelData !== undefined) {
				panelData = panelData[columnItems.root];
			}
			
			gridOptions = { 
				data: panelData,
				datatype: 'local', 
		        height: 'auto',
		        viewrecords: true,
		        autoencode: true,
		        rowNum: 10000
			};
		
	        if (panelData === null || panelData === undefined || panelData.length < 1) {
	            util.displayErrorMessage("There is no data available for your request. Please contact your IT administrator.");
	        } else {
			    if (options) {
					gridOptions = $.extend(gridOptions, options);
			    }
				return $("#" + tableId).slcGrid(columnItems, gridOptions); 
			}
		}
		
		return {
			create: create
		};
	}())
);
