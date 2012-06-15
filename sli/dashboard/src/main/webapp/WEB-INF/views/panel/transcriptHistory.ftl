<@includePanelModel panelId="transcriptHistory"/>
<#assign id = getDivId(panelConfig.id)>
</br>
<div class="ui-widget-no-border">
    <table id="${id}"></table>
</div>
<script type="text/javascript">

    function getTableId() {
        return '${id}';
    }

    SLC.grid.create(getTableId(), SLC.dataProxy.getConfig("transcriptHistory"),
            SLC.dataProxy.getData("transcriptHistory"),
            {
                subGridRowExpanded: function(subgrid_id, row_id) {
                    var select_by_row = function(data, row) {
                        var gradeLevel = row["gradeLevelCode"];
                        var schoolYear = row["schoolYear"];
                        var term = row["term"];
                        var school = row["school"];

                        for (var i in data) {
                            var elem = data[i];
                            if (elem["gradeLevelCode"] == gradeLevel &&
                                    elem["school"] == school &&
                                    elem["term"] == term &&
                                    elem["schoolYear"] == schoolYear) return elem["courses"];
                        }
                        return null;
                    }

                    var subgrid_table_id = subgrid_id+"_t";
                    $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table>");
                    var data = DashboardProxy.getData("transcriptHistory")["transcriptHistory"];
                    var row = $("#"+getTableId()).getRowData(row_id);
                    var row_data = select_by_row(data, row);
                    jQuery("#"+subgrid_table_id).sliGrid({}, {
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
                    jQuery("#"+subgrid_table_id).remove();
                },
                subGrid : true
            }
    );

    function expandAll() {
        $("#"+getTableId()).jqGrid("getDataIDs").forEach(function expandRow(rowId) {
            $("#"+getTableId()).expandSubGridRow(rowId);
        });
    }

    function collapseAll() {
        $("#"+getTableId()).jqGrid("getDataIDs").forEach(function expandRow(rowId) {
            $("#"+getTableId()).collapseSubGridRow(rowId);
        });
    }

</script>

<a href="#" onclick="expandAll()">Expand All</a>
<!-- <a href="#" onclick="collapseAll()">Collapse All</a> -->