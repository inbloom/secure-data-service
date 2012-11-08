getEdorgs = ->
  edorgs = []
  $('input#app_authorized_ed_orgs').map ->
    edorgs.push $(@).val()
  edorgs

jQuery ->
    $("#state-menu select").change ->
        selected = $(@).find("option:selected")
        return false if selected.val() == ""
        $.get("/lea?state=" + selected.val(), (data) ->
            $("#lea-menu").html(data)
            $("#lea-menu table").trigger("change")
        )
    false
jQuery ->
    $("#lea-menu table").live 'change', ->
        #Populate the LI classes with enabled stuff
        edorgs = getEdorgs()
        jQuery.each(edorgs, (index, item) ->
            $("tr##{item} td label input").attr('checked', true)
        )

jQuery ->
  $("#lea-menu table tbody tr td label input").live 'change', ->
    id = $(@).parent().parent().parent().attr('id')
    edorgs = getEdorgs()
    if $(@).is(':checked')
      #Add the input
      $('div#ed_orgs').append("<input id=\"app_authorized_ed_orgs\" name=\"app[authorized_ed_orgs][]\" multiple=\"multiple\" type=\"hidden\" value=#{id}>")
    else
      index = edorgs.indexOf id
      if index != -1
        #Remove the input
        $("input#app_authorized_ed_orgs[value=#{id}]").remove()
jQuery ->
    $("div.enable-disable a#enable-all").live 'click', ->
        $("#lea-menu table").find("input:not(:checked)").click()
        false
    $("div.enable-disable a#disable-all").live 'click', ->
        $("#lea-menu table").find("input:checked").click()
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

