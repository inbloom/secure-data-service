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
        edorgs = getEdorgs()
        jQuery.each(edorgs, (index, item) ->
            $("#lea-menu ul").find("li##{item}").addClass("enabled")
        )

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
                edorgs.splice(index, 1)
        $("input#app_authorized_ed_orgs").attr("value", JSON.stringify(edorgs))
        false
jQuery ->
    $("div.enable-disable a#enable-all").click ->
        alert "Enable"
        $("#lea-menu ul li:not(.enabled)").trigger("click")
    $("div.enable-disable a#disable-all").click ->
        alert "Disable"
        $("#lea-menu ul li.enabled").trigger("click")
    false

jQuery ->
  $("#applications tr:odd").addClass("odd")
  $("#applications tr:not(.odd)").hide()
  $("#applications tr:first-child").show()
  $("#applications tr.odd td").click ->
    if $(@).attr("class") != "rowAction"
      $(@).parent().next("tr").slideToggle()

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

