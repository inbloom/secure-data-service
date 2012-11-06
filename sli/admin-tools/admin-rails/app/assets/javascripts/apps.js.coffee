getEdorgs = ->
    edorgs = jQuery.parseJSON($("input#app_authorized_ed_orgs").val())
    edorgs

jQuery ->
    $("#state-menu select").change ->
        selected = $(@).find("option:selected")
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
            $("#lea-menu ul").find("li##{item} input").attr('checked', true)
        )

jQuery ->
    $("#lea-menu ul li input").live 'change', ->
        id = $(@).parent().parent().attr('id')
        edorgs = getEdorgs()
        if $(@).is(':checked')
            edorgs.push id
        else
            #Remove the element from the array
            index = edorgs.indexOf id
            if index != -1
                edorgs.splice(index, 1)
        $("input#app_authorized_ed_orgs").attr("value", JSON.stringify(edorgs))
jQuery ->
    $("div.enable-disable a#enable-all").click ->
        $("#lea-menu ul li input:not(:checked)").click()
        false
    $("div.enable-disable a#disable-all").click ->
        $("#lea-menu ul li input:checked").click()
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

