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

    DashboardUtil.makeGrid(getTableId(), DashboardProxy.getConfig("transcriptHistory"),
            DashboardProxy.getData("transcriptHistory"),
            {
                subGridRowExpanded: function(subgrid_id, row_id) {
                    var subgrid_table_id, pager_id;
                    subgrid_table_id = subgrid_id+"_t";
                    pager_id = "p_"+subgrid_table_id;
                    $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table><div id='"+pager_id+"' class='scroll'></div>");
                    jQuery("#"+subgrid_table_id).jqGrid({
                        //data: GET_ROW_DATA_BY_row_id,
                        datatype: "local",
                        root: "courses",
                        colNames: ['Subject','Course','Grade'],
                        colModel: [
                            {name:"Subject",index:"subject",width:80,key:true},
                            {name:"Course",index:"course",width:130},
                            {name:"Grade",index:"grade",width:70,align:"right"}
                        ],
                        rowNum:1000,
                        pager: pager_id,
                        sortname: 'num',
                        sortorder: "asc",
                        height: '100%'
                    });
                    jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false})
                },
                subGridRowColapsed: function(subgrid_id, row_id) {
                    var subgrid_table_id;
                    subgrid_table_id = subgrid_id + "_t";
                    jQuery("#"+subgrid_table_id).remove();
                },
                subGrid : true
            }
    );
</script>