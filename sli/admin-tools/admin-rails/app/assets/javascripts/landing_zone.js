/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


$(document).ready(function() {
	if (is_sandbox) {
  var buttonEnableForCustom = function() {
    if($("#custom_ed_org").val().length == 0) {
      $("#provisionButton").attr("disabled","disabled")
    }
    else {
      $("#provisionButton").removeAttr("disabled")
    }
  }
  $("input[type=radio][id!=custom]").click(function() {
   $("#custom_ed_org").attr("disabled","disabled");
   $("#provisionButton").removeAttr("disabled")});
  $("#custom").click(function() {
   $("#custom_ed_org").removeAttr("disabled");
   buttonEnableForCustom()});
  $("#custom_ed_org").bind('input propertychange', buttonEnableForCustom); 
  if($("#custom").attr("type") == "radio"){
    $("#custom_ed_org").attr("disabled","disabled")
  }
  $("#provisionButton").attr("disabled", "disabled")
  }
})
