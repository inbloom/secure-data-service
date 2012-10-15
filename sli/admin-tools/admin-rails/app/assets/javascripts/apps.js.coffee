getEdorgs = ->
    edorgs = jQuery.parseJSON($("input#app_authorized_ed_orgs").val())
    edorgs

jQuery ->
    $("#state-menu select").change ->
        selected = $(@).find("option:selected")
        console.log("Changed to: " +selected.val())
        $.get("/lea?state=" + selected.val(), (data) ->
            $("#lea-menu").html(data)
            $("#lea-menu ul").trigger("change")
        )
    false
jQuery ->
    $("#lea-menu ul").live 'change', ->
        #Populate the LI classes with enabled stuff
        alert "Change happened"

jQuery ->
    $("#lea-menu ul li").live 'click', ->
        id = $(@).attr('id')
        console.log id

        $(@).toggleClass 'enabled'
        edorgs = getEdorgs()
        if $(@).hasClass 'enabled'
            edorgs.push id
        else
            #Remove the element from the array
            index = edorgs.indexOf id
            if index != -1
                edorgs = edorgs.splice(edorgs.indexOf id, 1)

        $("input#app_authorized_ed_orgs").attr("value", JSON.stringify(edorgs))
        false

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

jQuery ->
  $('#installed > :checkbox').click ->
    state = $(@).prop('checked')
    if state
      $('#redirect_uri > :input').prop('disabled', true)
      $('#redirect_uri > :input').val('')
      $('#application_url > :input').prop('disabled', true)
      $('#application_url > :input').val('')
    else
      $('#redirect_uri > :input').prop('disabled', false)
      $('#application_url > :input').prop('disabled', false)

