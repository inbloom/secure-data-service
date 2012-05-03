$(document).ready(function() {
  $("input[type=radio][id!=custom]").click(function() {
   $("#custom_ed_org").attr("disabled","disabled")});
  $("#custom").click(function() {
   $("#custom_ed_org").removeAttr("disabled")});
  }
)
