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
 * SLC transcriptHistory
 * Handles all the methods related to transcript history table grid.
 */
/*global SLC $ */

SLC.namespace('SLC.transcriptHistory', (function () {
	
	var util = SLC.util;

    function expandAll() {
        var gridArray = $("#"+util.getTableId()).jqGrid("getDataIDs"),
			gridArrayLength = gridArray.length,
			i;
        
        for (i = 0; i < gridArrayLength; i++) {
			$("#"+util.getTableId()).expandSubGridRow(gridArray[i]);
        }
    }

    function collapseAll() {
		var gridArray = $("#"+util.getTableId()).jqGrid("getDataIDs"),
			gridArrayLength = gridArray.length,
			i;
        
        for (i = 0; i < gridArrayLength; i++) {
			$("#"+util.getTableId()).collapseSubGridRow(gridArray[i]);
        }

    }
    
	SLC.grid.tablegrid.create(util.getTableId(), SLC.dataProxy.getConfig("transcriptHistory"),
        SLC.dataProxy.getData("transcriptHistory"),
        {
            subGridRowExpanded: function(subgrid_id, row_id) {
					var subgrid_table_id,
						data,
						row,
						row_data,
						select_by_row = function(data, row) {
		                    var gradeLevel = row.gradeLevelCode,
								schoolYear = row.schoolYear,
								term = row.term,
								school = row.school,
								elem,
								i;
		
		                    for (i in data) {
								if (data.hasOwnProperty(i)) {
			                        elem = data[i];
			                        if (elem.gradeLevelCode === gradeLevel &&
			                                elem.school === school &&
			                                elem.term === term &&
			                                elem.schoolYear === schoolYear) {
												return elem.courses;
			                        }
		                        }
		                    }
		                    return null;
		                };

                subgrid_table_id = subgrid_id+"_t";
                $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table>");
                
				data = SLC.dataProxy.getData("transcriptHistory").transcriptHistory;
				row = $("#"+util.getTableId()).getRowData(row_id);
				row_data = select_by_row(data, row);
				
				$("#"+subgrid_table_id).slcGrid({}, {
                    data: row_data,
                    datatype: 'local',
                    root: "courses",
                    repeatitems: true,
                    viewrecords:true,
                    colNames: ['Subject','Course','Grade'],
                    colModel: [
                        {name:"subject",index:"subject",width:175,key:true},
                        {name:"course",index:"course",width:130},
                        {name:"grade",index:"grade",width:75}
                    ],
                    rowNum:1000,
                    sortname: 'num',
                    sortorder: "asc",
                    height: '100%'
                });
            },
            subGridRowColapsed: function(subgrid_id, row_id) {
                var subgrid_table_id;
                subgrid_table_id = subgrid_id + "_t";
                $("#"+subgrid_table_id).remove();
            },
            subGrid : true
        }
	);

	$(".transcript_expandAll").click(function () {
		expandAll();
	});
	
	
	}())
);
