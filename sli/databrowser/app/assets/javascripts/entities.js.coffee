# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/
table = null
jQuery ->
  table = $('#simple-table').dataTable(
    "bLengthChange": false,
    "bPaginate": false)
  $('#simple-table tbody tr').click ->
    details = $(@)
    console.log details
    if table.fnIsOpen(@)
      table.fnClose(@)
    else
      table.fnOpen(@, details.find('td.hidden').html(), "details")

jQuery ->
  if $('.pagination').length
    $(window).scroll ->
      url = $('div.pagination #paginate-next').attr('href')
      if url && $(window).scrollTop() > $(document).height() - $(window).height() - 50
        $('div.pagination').text("Fetching more results...")
        $.getScript(url)
    $(window).scroll()
