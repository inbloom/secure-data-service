
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
  
    $("tr.odd").each (index) ->
      status = $(@).find('td')[5]
      edOrgs = $(@).next().find('div.edorgs :checkbox')
      $(edOrgs).each (index) ->
        console.log(isNaN(parseInt($(@).attr('value'))))
        if isNaN(parseInt($(@).attr('value')))
          #Yellow the approved field
          $(status).addClass("yellow")
          $(status).text("In Progress")
          return
    
  #   # alert($(status).text())

changeCheck = (checkboxName, checkVal) ->
  checkboxes = document.getElementsByName checkboxName
  for i in checkboxes.length by 1
    checkboxes[i].checked = checkVal
