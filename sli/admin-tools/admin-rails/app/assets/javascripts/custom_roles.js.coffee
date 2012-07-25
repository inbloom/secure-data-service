jQuery ->
  $("#custom_roles tr:gt(0)").mouseenter ->
    
  #  $(@).find(".roleEditTool").show()
    xPos = $(@).position().left + $(@).width()
    yPos = $(@).position().top
   # $(".rowEditTool").width(100)
    $(".rowEditTool").show()
    $(".rowEditTool").height($(@).height())
    $(".rowEditTool").offset({top: yPos, left: xPos})
    $(".rowEditTool").show()
    console.log("done")

jQuery ->
  $("#custom_roles tr").mouseleave ->
    $(".rowEditTool").hide()

jQuery ->
  $(".rowEditTool").mouseenter ->
    $(".rowEditTool").show()
