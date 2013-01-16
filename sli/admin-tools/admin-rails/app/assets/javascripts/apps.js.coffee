getEdorgs = ->
  edorgs = []
  $('input#app_authorized_ed_orgs').map ->
    edorgs.push $(@).val()
  edorgs

jQuery ->
  #Always focus the first text input on a page.
  $('input:text:visible:first').focus()
  
jQuery ->
    $("#state-menu select").change ->
        selected = $(@).find("option:selected")
        return false if selected.val() == ""
        $.get("/lea?state=" + selected.val(), (data) ->
            $("#lea-menu").html(data)
            $('a#enable-help').tooltip()            
            $("#lea-menu table").trigger("change")
        )

    false
jQuery ->
    $("#lea-menu table").live 'change', ->
        #Populate the LI classes with enabled stuff
        count = $(@).find('tr').size()
        edorgs = getEdorgs()
        jQuery.each(edorgs, (index, item) ->
            $("tr##{item} td label input").attr('checked', true)
        )
        $('#smartpager').smartpaginator({ datacontainer:'lea-table', dataelement:'tr', display:'single', totalrecords: count, recordsperpage: 25, initval:0 , next: 'Next', prev: 'Prev', first: 'First', last: 'Last', theme: 'bootstrap'})
    # 
    #     if(count <= items_per_page)
    #       $('div.pagination').hide()
    #     else
    #       pages = count/items_per_page
    #       if count % items_per_page !=0
    #         pages += 1
    #       $('div.pagination li:first').addClass('disabled')
    #       for page in [1..pages]
    #         $('div.pagination li:last').before('<li><a href="#">'+page+'</a></li>')
    #         if page == 1
    #           $('div.pagination li:first + li').addClass('active')
    #       $('#lea-table tr:gt('+(items_per_page-1)+')').hide()
    #     false
    # 
    # $('div.pagination li:gt(0):not(:last) a').live 'click', ->
    #   new_page = parseInt($(@).text())-1
    #   start_point = new_page * items_per_page
    #   stop_point = start_point + items_per_page
    #   #Unhide this page
    #   $('#lea-table tr').slice(start_point, stop_point).show()
    #   #Hide everything else
    #   $('#lea-table tr:lt('+start_point+')').hide()
    #   $('#lea-table tr:gt('+stop_point+')').hide()
    #   #Fix the class
    #   $('div.pagination li.active').removeClass('active')
    #   $(@).parent().addClass('active')
    #   false
    # $('div.pagination li:last a').live 'click', ->
    #   $(@).prev().find('a').click()
    # $('div.pagination li:first a').live 'click', ->
    #   $(@).next().find('a').click()
    
jQuery ->
  $("#lea-menu input").live 'change', ->
    id = $(@).parent().parent().parent().attr('id')
    edorgs = getEdorgs()
    if $(@).is(':checked')
      #Add the input
      $('div#ed_orgs').append("<input id=\"app_authorized_ed_orgs\" name=\"app[authorized_ed_orgs][]\" multiple=\"multiple\" type=\"hidden\" value=\"#{id}\">")
    else
      index = edorgs.indexOf id
      if index != -1
        #Remove the input
        $("input#app_authorized_ed_orgs[value=\"#{id}\"]").remove()
    false
  
jQuery ->
    $("div.enable-disable a#enable-all").live 'click', ->
        $("#lea-menu input:visible:not(:checked)").click()
        false
    $("div.enable-disable a#disable-all").live 'click', ->
        $("#lea-menu input:visible:checked").click()
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

