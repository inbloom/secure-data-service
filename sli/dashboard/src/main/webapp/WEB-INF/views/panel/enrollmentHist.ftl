<#assign root = data[config.data.alias]>
<#assign id = viewConfigs["enrollmentHist"].id + "-" + random.nextInt(100)>

<div class="enrollmentHist">
<table id="${id}"></table>

<script type="text/javascript">

<#-- get json config items -->

var tableId = '${id}';
var config = ${viewConfigsJson};
var panelConfig = config.enrollmentHist;

<#-- get data -->
var mydata = [ {col1:"2011-12",col2:"East Daybreak Junior High",col3:"8",col4:"2011-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2010-11",col2:"East Daybreak Junior High",col3:"7",col4:"2010-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2009-10",col2:"East Daybreak Junior High",col3:"6",col4:"2009-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2008-09",col2:"East Daybreak Junior High",col3:"5",col4:"2008-09-01",col5:"N",col6:"",col7:""}, 
               {col1:"2007-08",col2:"East Daybreak Junior High",col3:"4",col4:"2007-09-01",col5:"N",col6:"",col7:""} ]; 

<#-- make grid -->
DashboardUtil.makeGrid(tableId, panelConfig, mydata);

</script>

</div>
