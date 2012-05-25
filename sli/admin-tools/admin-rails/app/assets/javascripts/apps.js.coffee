
jQuery ->
  if $('table').length != 0
    $(':input:enabled').not('.approve-button').not('.unregister-button').attr('disabled', true)
    $("a.edit-link").click ->
      set = $(@).closest("tr").next().find(':input:not(.read_only):disabled')
      if(set.length == 0)
        $(@).closest("tr").next().slideUp()
        $(@).closest("tr").next().find(':input:not(.read_only):enabled').attr('disabled', true)
      else 
        set.removeAttr('disabled')
        $(@).closest("tr").next().slideDown()
  
    $("#applications tr:odd").addClass("odd")
    $("#applications tr:not(.odd)").hide()
    $("#applications tr:first-child").show()
    $("#applications tr.odd td").click ->
      if $(@).attr("class") != "rowAction"
        $(@).parent().next("tr").slideToggle()

toggleAll = (isOn) ->
  if isOn
    $('div.edorgs:checkbox:not(:checked)').prop("checked", "checked")
  else
    $('div.edorgs:checkbox:chencked').removeProp("checked")