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
 *	Creates 'SLC tree grid' 
 */
/*global $ SLC*/

SLC.namespace("SLC.grid.tree", (function () {
	
		var util = SLC.util;
		
		function create(tableId, columnItems, panelData, options) {
				
				var treeOptions;
			
				if (columnItems.root && panelData !== null && panelData !== undefined) {
					panelData = panelData[columnItems.root];
				}
				
				treeOptions = { 
					datastr: panelData,
					datatype: "jsonstring",
			        height: 'auto',
			        viewrecords: true,
			        autoencode: true,
			        rowNum: 10000,
			        treeGrid: true,
	                treeGridModel: 'adjacency',
	                treedatatype: "local",
	                ExpandColClick: true,
					jsonReader: {
						repeatitems: false,
	                    root: function (obj) { return obj; },
	                    page: function (obj) { return 1; },
	                    total: function (obj) { return 1; },
	                    records: function (obj) { return obj.length; }
					}
				};
			
		        if (panelData === null || panelData === undefined || panelData.length < 1) {
		            util.displayErrorMessage("There is no data available for your request. Please contact your IT administrator.");
		        } else {
				    if (options) {
						treeOptions = $.extend(treeOptions, options);
				    }
					return $("#" + tableId).slcGrid(columnItems, treeOptions); 
				}
					
			}
			
			return {
				create: create
			};
	}())
);
