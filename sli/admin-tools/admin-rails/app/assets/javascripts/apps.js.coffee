
jQuery ->
  $("#applications tr:odd").addClass("odd")
  $("#applications tr:not(.odd)").hide()
  $("#applications tr:first-child").show()
  $("#applications tr.odd td").click ->
    if $(@).attr("class") != "rowAction"
      $(@).parent().next("tr").slideToggle()

jQuery ->
  $('div.edorgs > ul > li > :checkbox').click ->
    $('div.edorgs.yellow').removeClass('yellow')
    state = $(@).prop('checked')
    $(@).next().find(":checkbox").each (index) ->
      $(@).prop('checked', state)
  $('a#enable-all').click ->
    toggleAll true
    false
  $('a#disable-all').click ->
    toggleAll false
    false
  toggleAll = (isOn) ->
    $('div.edorgs :checkbox').each (index) ->
      if isOn
        $(@).prop('checked', true)
      else
        $(@).prop('checked', false)
    