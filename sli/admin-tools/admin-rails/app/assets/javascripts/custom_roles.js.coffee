defaultRights = ["READ_GENERAL", "WRITE_GENERAL", "READ_RESTRICTED", "WRITE_RESTRICTED", "AGGREGATE_READ", "AGGREGATE_WRITE", "READ_PUBLIC"]
selfRights = ["READ_SELF_RESTRICTED", "READ_SELF_GENERAL"]
ROLE_COL = "td:eq(0)";
RIGHT_COL = "td:eq(1)";
SELF_RIGHT_COL = "td:eq(2)";
ADMIN_COL = "td:eq(3)";
EDIT_COL = "td:eq(4)";

jQuery ->
  unless initCustomRoleScripts?
    return

  populateTable(roles)

  $("#addSelfRightUi button").click ->
    console.log("Clicking the self button")
    option = $('#addSelfRightUi option:selected')
    if (option.val() == 'none')
      return
    text = option.text()
    right = createLabel('right', text)
    right = wrapInputWithDeleteButton(right, "span", text)
    $("#addSelfRightUi").parent().append(right)
    $("#addSelfRightUi").parent().append(" ")
    populateRightComboBox($(@).parents("tr"))
    enableSaveButtonIfPossible($(@).parents("tr"))

  $("#addRightUi button").click ->
    console.log("The regular button clicked")
    option = $('#addRightUi option:selected')
    if (option.val() == 'none')
      return
    text = option.text()
    right = createLabel('right', text)
    right = wrapInputWithDeleteButton(right, "span", text)
    $("#addRightUi").parent().append(right)
    $("#addRightUi").parent().append(" ")
    populateRightComboBox($(@).parents("tr"))
    enableSaveButtonIfPossible($(@).parents("tr"))


  #Wire up Add Role button
  $("#addGroupButton").click ->
    newRow = $("<tr><td><div class='groupTitle'></div></td><td></td><td></td><td><input type='checkbox' class='isAdmin'></td><td></td></tr>")
    $("#custom_roles tbody").append(newRow)

    newRow.find(EDIT_COL).append($("#rowEditTool").clone().children())

    # Disable the save button until they've added a role and right
    disableButton(newRow.find(".rowEditToolSaveButton"))
    editRow(newRow)

    #Add row edit tool
    wireEditButtons(newRow)
    

  #Wire up reset to defaults
  $("#resetToDefaultsButton").click ->
    if (confirm("Resetting to default roles will remove any existing role mapping and will restore the default roles.  This operation cannot be undone.  Are you sure you want to reset?"))
      populateTable(default_roles)
      saveData(getJsonData())

  $("#addRoleUi button").click ->
    tr = $(@).parents("tr")
    td = tr.find(ROLE_COL)
    roleName = $("#addRoleInput").val().trim()
    if (roleName == "")
      return

    #Check for duplicates
    if (getAllRoles().indexOf(roleName) > -1)
      return alert("The role name " + roleName + " is already used.")
    div = createLabel('role', roleName)
    div = wrapInputWithDeleteButton(div, "div", roleName)
    div.wrap("<div/>")
    td.append(div.parent())
    #$("#addRoleUi input").val("")
    $("#addRoleInput").val("")
    enableSaveButtonIfPossible(tr)
  
enableSaveButtonIfPossible = (tr)  ->
  roles = getRoles(tr)  
  rights = getRights(tr)  
  if roles.length > 0 && rights.length > 0
    tr.find(".rowEditToolSaveButton").removeClass("disabled")
    tr.find(".rowEditToolSaveButton").removeAttr('disabled')
    

createLabel = (type, name) ->
  label = $('#labelUi').clone()
  label.find("span").text(name)
  label.children().addClass(type)
  return label.children()

editRow = (tr) ->
  
  #hide all the other edit buttons
  curRowButtons = tr.find(".rowButtons")
  tr.parent().find(".rowButtons").each ->
    if $(@) != curRowButtons
      $(@).hide()
 
  disableButton($("#addGroupButton"))
  tr.find(".saveButtons").show()
  tr.find(".editButtons").hide()
  tr.find(".isAdmin").prop("disabled", false)

  populateRightComboBox(tr)
  tr.find(RIGHT_COL).prepend($("#addRightUi"))
  $("#addRightUi").fadeIn()
  tr.find(SELF_RIGHT_COL).prepend($("#addSelfRightUi"))
  $("#addSelfRightUi").fadeIn()

  #Give it a nice glow
  tr.find("td").addClass("highlight")

  #Add role name field
  td = tr.find(ROLE_COL)
  addRoleUi = $("#addRoleUi")
  td.prepend(addRoleUi)
  addRoleUi.find("input").val("")
  addRoleUi.fadeIn()

  #Turn group name into input
  groupName = tr.find(ROLE_COL).find(".groupTitle")
  groupName.hide()
  input = $('#groupNameInput').val(groupName.text().trim())

  if (groupName.text().trim() == "")
    input.attr("placeholder", "Enter group name")

  #Add delete button to each role name
  tr.find(ROLE_COL).find(".roleLabel").each -> wrapInputWithDeleteButton($(@), "div", groupName)
  tr.find(RIGHT_COL).find(".roleLabel").each -> wrapInputWithDeleteButton($(@), "span", groupName)

populateRightComboBox = (tr) ->
  #Add right combobox - only add rights that haven't already been used
  curRights = getRights(tr)
  curSelfRights = getSelfRights(tr)
  $("#addRightUi option").each ->
    if ($(@).val() != "none")
      $(@).remove()

  $("#addSelfRightUi option").each ->
    if ($(@).val() != "none")
      $(@).remove()

  for right in defaultRights
    if (curRights.indexOf(right) < 0)
      $("#addRightUi select").append($("<option></option>").val(right).text(right))

  for right in selfRights
    if (curSelfRights.indexOf(right) < 0)
      $("#addSelfRightUi select").append($("<option></option>").val(right).text(right))

wrapInputWithDeleteButton = (input, type, name) ->
  div = $('<span>').addClass("input-append")
  button = $("<button class='btn' id='DELETE_" + input.text() + "' >&times;</button>")
  div.append(button)
  button.click ->
    label = button.parent().parent().find('.editable')
    if label.hasClass('right')
      rights = getRights(label.parents("tr"))
      if rights.length <= 1
        return alert("Role group must contain at least one right.")

    if label.hasClass('role')
      roles = getRoles(label.parents("tr"))
      if roles.length <= 1
        return alert("Role group must contain at least one role.")

    button.parent().parent().fadeOut 'fast', ->
      parentTr = $(this).parents('tr')
      $(this).remove()
      populateRightComboBox(parentTr)
      
  
  input.addClass("editable")
  input.wrap("<" + type + "/>").parent().css("white-space", "nowrap")
  input.parent().append(div)
  return input.parent()

editRowStop = (tr) ->
  disableButton($(".rowEditToolSaveButton"))
  disableButton($(".rowEditToolCancelButton"))
  saveData(getJsonData()) 
  editRowIndex = -1

saveData = (json) ->
  #Remove any errors from the last save
  $(".error").remove()
  $.ajax UPDATE_URL,
    type: 'PUT'
    contentType: 'application/json'
    data: JSON.stringify({json})
    dataType: 'json'
    success: (data, status, xhr) ->
      #$(".saveButtons").hide()
      window.location.reload(true);
    error: (data, status, xhr) ->
      console.log("error", data, status, xhr)
      window.location.reload(true);


# Should be able to scrape the data from each row regardless of whether the row is in edit mode
getJsonData = () ->
  data = []
  $("#custom_roles tr:gt(0)").each ->
    groupName = $(@).find(ROLE_COL).find("#groupNameInput").val()
    if !groupName
      groupName = $(@).find(ROLE_COL).find(".groupTitle").text()
    if !groupName
      groupName = $(@).find(ROLE_COL).find(".groupTitle").val()

    roles = []
    $(@).find(ROLE_COL).find(".customLabel").each ->
      roles.push($(@).text())
    rights = []
    $(@).find(RIGHT_COL).find(".customLabel").each ->
      rights.push($(@).text())
    isAdminRole = $(@).find(".isAdmin").prop("checked")
    data.push({"groupTitle": groupName, "names": roles, "rights": rights, "isAdminRole": isAdminRole})
  return data

getRights = (tr) ->
    rights = []
    tr.find(RIGHT_COL).find(".customLabel").each ->
      rights.push($(@).text())
    return rights

getSelfRights = (tr) ->
    rights = []
    tr.find(SELF_RIGHT_COL).find(".customLabel").each ->
      rights.push($(@).text())
    return rights

getRoles = (tr) ->
    roles = []
    tr.find(ROLE_COL).find(".customLabel").each ->
      roles.push($(@).text())
    return roles

getAllRoles = () ->
  roles = []
  $("#custom_roles tr:gt(0)").each ->
    $(@).find(ROLE_COL).find(".customLabel").each ->
      roles.push($(@).text())
  return roles

populateTable = (data) ->
  $("#custom_roles tbody").children().remove()
  for role in data
    newRow = $("<tr><td><div></div></td><td></td><td></td><td></td><td></td></tr>")
    $("#custom_roles tbody").append(newRow)

    newRow.find(ROLE_COL).append($("<div class='groupTitle'></div>").text(role.groupTitle))

    for name in role.names
      div = $('<div/>')
      div.append(createLabel('role', name))
      newRow.find(ROLE_COL).append(div)

    for right in role.rights
      newRow.find(RIGHT_COL).append(createLabel('right', right))
      newRow.find(RIGHT_COL).append(" ")


    newRow.find(ADMIN_COL).append("<input type='checkbox' class='isAdmin' disabled='true'>")
    if (role.isAdminRole)
      newRow.find(".isAdmin").prop("checked", true)

    newRow.find(EDIT_COL).append($("#rowEditTool").clone().children())
    wireEditButtons(newRow)

disableButton = (buttonElement) ->
  buttonElement.addClass("disabled")
  buttonElement.attr('disabled', 'disabled')

wireEditButtons = (tr) ->
  tr.find(".rowEditToolDeleteButton").click ->
    if (confirm("Do you really want to delete the role group"))
      $(@).parents("tr").remove()
      saveData(getJsonData())

  tr.find(".rowEditToolEditButton").click ->
    editRow($(@).parents("tr"))

  tr.find(".rowEditToolCancelButton").click ->
      window.location.reload(true);

  tr.find(".rowEditToolSaveButton").click ->
      editRowStop(tr)      


