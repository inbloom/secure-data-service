lastRow = -1 #last highlighted row
editRowIndex = -1 #last row that was clicked on
defaultRights = ["READ_GENERAL", "WRITE_GENERAL", "READ_RESTRICTED", "WRITE_RESTRICTED", "AGGREGATE_READ", "AGGREGATE_WRITE"]

jQuery ->
  populateTable()

  $("#custom_roles tr:gt(0)").mouseenter -> rowMouseEnter($(@))

  $("#custom_roles tr").mouseleave ->
    if (editRowIndex < 0)
      $(".rowEditTool").hide()

  $(".rowEditTool").mouseenter ->
    $(".rowEditTool").show()

  $("#rowEditToolEditButton").click ->
    if (editRowIndex < 0)
      editRow(lastRow)

  $("#rowEditToolDeleteButton").click ->
    if (confirm("Do you really want to delete the role group"))
      $("#custom_roles tr:eq(" + lastRow + ")").remove()

  $("#rowEditToolSaveButton").click ->
    if (editRowIndex > -1)
      editRowStop()      

  $("#addRightUi button").click ->
    option = $('#addRightUi option:selected')
    if (option.val() == 'none')
      return
    text = option.text()
    right = createRightLabel(text)
    right = wrapInputWithDeleteButton(right, "span")
    $("#addRightUi").parent().append(right)
    $("#addRightUi").parent().append(" ")
    populateRightComboBox()

  #Wire up Add Role button
  $("#addGroupButton").click ->
    newRow = $("<tr><td><div></div></td><td></td><td></td></tr>")
    $("#custom_roles tbody").append(newRow)
    newRow.mouseenter -> rowMouseEnter(newRow)
    lastRow = $("#custom_roles tbody").children().index(newRow) + 1
    editRowIndex = lastRow
    drawEditBox(newRow)
    editRow(lastRow)

  $("#addRoleUi button").click ->
    td = $("#custom_roles tr:eq(" + editRowIndex + ") td:eq(1)")
    roleName = $("#addRoleUi input").val().trim()
    if (roleName == "")
      return

    #Check for duplicates
    if (getAllRoles().indexOf(roleName) > -1)
      return alert("The role name " + roleName + " is already used.")
    div = createRightLabel(roleName)
    div = wrapInputWithDeleteButton(div, "div")
    div.wrap("<div/>")
    td.append(div.parent())
    $("#addRoleUi input").val("")
  
rowMouseEnter = (row) ->   
  if (editRowIndex < 0)
    drawEditBox(row)

drawEditBox = (row) ->
  lastRow = row.parent().children().index(row) + 1
  xPos = row.position().left + row.width()
  yPos = row.position().top
  $(".rowEditTool").show()
  $(".rowEditTool").height(row.height())
  $(".rowEditTool").offset({top: yPos, left: xPos})

createRightLabel = (name) ->
  label = $('#labelUi').clone()
  label.find("span").text(name)
  return label.children()

editRow = (rowNum) ->
  $("#addGroupButton").addClass("disabled")
  $(".editButtons").hide()
  $(".saveButtons").show()
  editRowIndex = rowNum

  populateRightComboBox()
  $("#custom_roles tr:eq(" + rowNum + ") td:eq(2)").prepend($("#addRightUi"))
  $("#addRightUi").fadeIn()

  #Give it a nice glow
  $("#custom_roles tr:eq(" + rowNum + ") td").addClass("highlight")

  #Add role name field
  td = $("#custom_roles tr:eq(" + rowNum + ") td:eq(1)")
  addRoleUi = $("#addRoleUi")
  td.prepend(addRoleUi)
  addRoleUi.find("input").val("")
  addRoleUi.fadeIn()

  #Turn group name into input
  groupName = $("#custom_roles tr:eq(" + rowNum + ") td:eq(0) div")
  input = $("<input type='text'/>").val(groupName.text().trim())
  if (groupName.text().trim() == "")
    input.attr("placeholder", "Enter group name")
  groupName.replaceWith(input)
   
  #Add delete button to each role name
  $("#custom_roles tr:eq(" + rowNum + ") td:eq(1) .roleLabel").each -> wrapInputWithDeleteButton($(@), "div")
  $("#custom_roles tr:eq(" + rowNum + ") td:eq(2) .roleLabel").each -> wrapInputWithDeleteButton($(@), "span")

populateRightComboBox = () ->
  #Add right combobox - only add rights that haven't already been used
  curRights = getRights(editRowIndex)
  $("#addRightUi option").each ->
    if ($(@).val() != "none")
      $(@).remove()

  for right in defaultRights
    if (curRights.indexOf(right) < 0)
      $("#addRightUi select").append($("<option></option>").val(right).text(right))


wrapInputWithDeleteButton = (input, type) ->
  div = $('<span>').addClass("input-append")
  button = $("<button class='btn'>&times;</button>")
  div.append(button)
  button.click ->
    button.parent().parent().fadeOut -> 
      $(this).remove()
    populateRightComboBox()
  
  input.addClass("editable")
  input.wrap("<" + type + "/>").parent().css("white-space", "nowrap")
  input.parent().append(div)
  return input.parent()

editRowStop = () ->
  $("#addGroupButton").removeClass("disabled")
  $(".editButtons").show()
  $(".saveButtons").hide()

  #Move the components back to their original location
  $("#addRoleUi").hide()
  $("#components").append($("#addRoleUi"))
  $("#addRightUi").hide()
  $("#components").append($("#addRightUi"))
  td = $("#custom_roles tr:eq(" + editRowIndex + ") td")
  td.removeClass("highlight")
    
  #Replace input with delete buttons back to normal inputs
  td.find(".editable").each ->
    $(@).parent().replaceWith(createRightLabel($(@).text()))

  #Replace editable group name with normal div
  input = $("#custom_roles tr:eq(" + editRowIndex + ") td:eq(0) input")
  div = $("<div/>").text(input.val())
  input.replaceWith(div)
  saveData(getJsonData()) 
  editRowIndex = -1

saveData = (json) ->
  console.log("Saving", json)
  $.ajax '/',
    url: UPDATE_URL
    type: 'PUT'
    contentType: 'application/json'
    data: JSON.stringify({json})
    dataType: 'json'
    success: (data, status, xhr) ->
      console.log("success", data, status, xhr)
    error: (data, status, xhr) ->
      console.log("error", data, status, xhr)


getJsonData = () ->
  data = []
  $("#custom_roles tr:gt(0)").each ->
    groupName = $(@).find("td:eq(0) div").text()
    roles = []
    $(@).find("td:eq(1) span").each ->
      roles.push($(@).text())
    rights = []
    $(@).find("td:eq(2) span").each ->
      rights.push($(@).text())
    data.push({"groupName": groupName, "roles": roles, "rights": rights})
  return data

getRights = (row) ->
    rights = []
    $("#custom_roles tr:eq(" + row + ")").find("td:eq(2) .customLabel").each ->
      rights.push($(@).text())
    return rights

getAllRoles = () ->
  roles = []
  $("#custom_roles tr:gt(0)").each ->
    $(@).find("td:eq(1) .customLabel").each ->
      roles.push($(@).text())
  return roles

populateTable = () ->
  for role in roles
    newRow = $("<tr><td><div></div></td><td></td><td></td></tr>")
    newRow.mouseenter -> rowMouseEnter(newRow)
    $("#custom_roles tbody").append(newRow)

    newRow.find("td:eq(0)").append("<div>unknown</div>")

    for name in role.names
      div = $('<div/>')
      div.append(createRightLabel(name))
      newRow.find("td:eq(1)").append(div)

    for right in role.rights
      newRow.find("td:eq(2)").append(createRightLabel(right))
      newRow.find("td:eq(2)").append(" ")
