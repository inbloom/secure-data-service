lastRow = -1
editRowIndex = -1

jQuery ->
  $("#custom_roles tr:gt(0)").mouseenter -> rowMouseEnter($(@))

  $("#custom_roles tr").mouseleave ->
    if (editRowIndex < 0)
      $(".rowEditTool").hide()

  $(".rowEditTool").mouseenter ->
    $(".rowEditTool").show()

  $("#rowEditToolEditButton").click ->
    if (editRowIndex < 0)
      editRow(lastRow)

  $("#rowEditToolSaveButton").click ->
    if (editRowIndex > -1)
      editRowStop()      

  $("#addRightUi").change ->
    text = $('#addRightUi option:selected').text()
    $("#addRightUi").before(createRightButton(text))

  #Wire up Add Role button
  $("#addRoleButton").click ->
    newRow = $("<tr><td></td><td></td></tr>")
    $("#custom_roles tbody").append(newRow)
    newRow.mouseenter -> rowMouseEnter(newRow)
    lastRow = $("#custom_roles tbody").children().index(newRow) + 1
    editRow(lastRow)

  $("#addRoleUi button").click ->
    td = $("#custom_roles tr:eq(" + editRowIndex + ") td:first")
    roleName = $("#custom_roles input").val()
    div = createRightButton(roleName)
    div = wrapInputWithDeleteButton(div)
    td.append(div)
  
rowMouseEnter = (row) ->   
    lastRow = row.parent().children().index(row) + 1 #track the index of the currently highlighted row
    xPos = row.position().left + row.width()
    yPos = row.position().top
    $(".rowEditTool").show()
    $(".rowEditTool").height(row.height())
    $(".rowEditTool").offset({top: yPos, left: xPos})

createRightButton = (name) ->
  button = $('#buttonUi').clone();
  button.find("input").attr('value', name)
  return button.children()


createRightLabel = (name) ->
   label = $('#labelUi').clone();
   label.removeAttr('id')
   label.text(name)
   return label

editRow = (rowNum) ->
    $(".editButtons").hide()
    $(".saveButtons").show()
    editRowIndex = rowNum

    #turn spans into buttons
    $("#custom_roles tr:eq(" + rowNum + ") td span").each ->
      $(@).replaceWith(createRightButton($(@).text()))

    #Add right combobox
    $("#custom_roles tr:eq(" + rowNum + ") td:last").append($("#addRightUi"))
    $("#addRightUi").fadeIn()

    #Add role name field
    td = $("#custom_roles tr:eq(" + rowNum + ") td:first")
    addRoleUi = $("#addRoleUi")
    td.prepend(addRoleUi)
    addRoleUi.fadeIn()

    #Add delete button to each role name
    td.find(".roleLabel").each -> wrapInputWithDeleteButton($(@))

wrapInputWithDeleteButton = (input) ->
      input.wrap('<div class="input-append"/>') 
      input.parent().wrap('<div class="controls"/>') 
      input.parent().parent().wrap('<div class="control-group"/>') 
      button = $("<button class='btn'>&times;</button>")
      button.click ->
        button.parent().parent().remove()
      input.parent().append(button)
      return input.parent().parent().parent()

editRowStop = () ->
    console.log('yo!!!!!!!!!!')
    $(".editButtons").show()
    $(".saveButtons").hide()

    #Move the components back to their original location
    $("#addRoleUi").hide()
    $("#components").append($("#addRoleUi"))
    $("#addRightUi").hide()
    $("#components").append($("#addRightUi"))
    td = $("#custom_roles tr:eq(" + editRowIndex + ") td")
    td.find("control-group").each ->
      $(@).remove()
      #replaceWith(createRightLabel($(@).find("input").val()))
    editRowIndex = -1
