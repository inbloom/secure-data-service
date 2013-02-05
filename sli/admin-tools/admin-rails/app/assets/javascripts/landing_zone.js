/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

// !!! Commenting out RSA Key field UI temporarily !!!
// 
//  // rsa key field interaction
//  $("#addKeyBtn").unbind("click");
//  $("#addKeyBtn").click(function() {
//    $("#rsaKeyField").toggle(250);
//    $("#rsaKeyField").find(':input').val('');
//      $("#rsa_validation_error_text").text('');
//    $("#key_arrow").toggleClass("icon-chevron-left icon-chevron-down");
//  });
//
//  // tool tips
//  $("#key_tooltip").qtip({
//      content: 'You can provide a public key that will be used to securely SFTP to your landing zone. Public keys must be encrypted using RFC 4716 format. <a href="http://www.ietf.org/rfc/rfc4716.txt" class="tooltip_link">More Information</a>',
//      position: {
//	  corner: { 
//	      tooltip: 'bottomMiddle',
//	      target: 'topMiddle'
//	  }
//      },
//      hide: { 
//	  fixed: true,
//	  when: {
//	      event: 'unfocus'
//	  }
//      },
//      style: { 
//	  border: {
//              width: 1,
//              radius: 5
//          },
//	  padding: 5,
//	  tip: true
//      }
//  });
//
//  // put out spinner for provision button
// !!! END Commenting out RSA Key field UI temporarily !!!
  $("#provisionButton").click(function() {
      $("#spinner").show();
  });

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
	$("#provisionButton").removeAttr("disabled");
	$("#sample_data_select").removeAttr("disabled")
    });
    $("#custom").click(function() {
      $("#custom_ed_org").removeAttr("disabled");
      $("#sample_data_select").attr("disabled","disabled");
      buttonEnableForCustom()
    });
    $("#custom_ed_org").bind('input propertychange', buttonEnableForCustom); 
    if($("#custom").attr("type") == "radio"){
      $("#custom_ed_org").attr("disabled","disabled")
    }
    $("#provisionButton").attr("disabled", "disabled");
    $("#sample_data_select").attr("disabled","disabled")
  }

})
