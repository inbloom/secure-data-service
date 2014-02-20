# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/
table = null

jQuery ->
  table = $('#simple-table').dataTable(
    "sDom": '<"top"i><"right"l>rt<"bottom"fp><"clear">'
    "bFilter": false,
    "bRetrieve": true,
    "bPaginate": false,
    "bLengthChange": false,
    "bInfo" : false,
    "sPaginationType": "full_numbers",
    "aoColumnDefs": [ 
       { "bSortable": false, "aTargets": [ 0 ] }
    ]
    )
  $('#simple-table tbody tr').click ->
    details = $(@)
    console.log details
    if table.fnIsOpen(@)
      table.fnClose(@)
      $(@).find("td:first-child.expandable").toggleClass("expanded")
    else
      table.fnOpen(@, details.find('td.hidden').html(), "details")
      $(@).find("td:first-child.expandable").toggleClass("expanded")
