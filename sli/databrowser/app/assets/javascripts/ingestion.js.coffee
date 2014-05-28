# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

jQuery ->
  $("#list_ingestion_table").dataTable(
    "sDom": '<"top"i><"right"l>rt<"bottom"f><"left"p><"clear">'
    "bFilter": false,
    "bRetrieve": true,
    "bPaginate": false,
    "bLengthChange": false,
    "bInfo" : true,
    "sPaginationType": "full_numbers",
    "aoColumnDefs": [ 
       { "bSortable": false, "aTargets": [ 0 ] }
    ],
    "aaSorting": [[2, "desc"]]
  )