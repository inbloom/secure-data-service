<#assign config = viewConfigs["enrollmentHist"]>
<#assign root = data[config.data.alias]>
<#assign id = config.id + "-" + random.nextInt(100)>

<div class="enrollmentHist">
<table id="${id}"></table>

<script type="text/javascript">

<#-- convert config from java to javascript vars, to pass into jq grid -->

var panelId = '${id}';
var panelName = '${config.id}';
var colNames = [<#list config.items as item>'${item.name}',</#list>];
var colModel = [<#list config.items as item>{name:'${item.id}',index:'${item.id}',width:${item.width}},</#list>];
var mydata = [ {col1:"2011-12",col2:"East Daybreak Junior High",col3:"8",col4:"2011-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2010-11",col2:"East Daybreak Junior High",col3:"7",col4:"2010-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2009-10",col2:"East Daybreak Junior High",col3:"6",col4:"2009-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2008-09",col2:"East Daybreak Junior High",col3:"5",col4:"2008-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2007-08",col2:"East Daybreak Junior High",col3:"4",col4:"2007-09-01",col5:"N",col6:"",col7:""} ]; 

<#-- make the grid -->

jQuery("#" + panelId).jqGrid({ 
datatype: "local", 
height: 250, 
colNames: colNames, 
colModel: colModel, 
multiselect: true, 
caption: panelName} ); 

<#-- populate the grid -->

for(var i=0;i<=mydata.length;i++) jQuery("#${id}").jqGrid('addRowData',i+1,mydata[i]); 

</script>

</div>
