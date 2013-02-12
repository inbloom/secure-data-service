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

  if (self_editing && is_operator) {
    $("#roles_edorg_block").hide()
  }

  if (self_editing && (is_lea || is_sea)) {
    $("#primary_role_block").hide()
  }

  if (is_sandbox) {
    $("#user_primary_role").change(function() {
        if($("#user_primary_role option:selected").attr("value")== "Sandbox Administrator") {
        	 $("#ingestion_user_role").removeAttr("disabled");
        	 $("#application_developer_role").removeAttr("disabled");
          }
          else if($("#user_primary_role option:selected").attr("value")== "Application Developer") {
        	  $("#application_developer_role").attr("disabled","disabled").attr("checked",false);
        	  $("#ingestion_user_role").removeAttr("disabled");
          } else {
        	  $("#ingestion_user_role").attr("disabled","disabled").attr("checked",false);
        	  $("#application_developer_role").removeAttr("disabled");
          }
        });
    
  } else {
	  $("#user_primary_role").change(function() {
	        if($("#user_primary_role option:selected").attr("value")== "SLC Operator") {
	        	 $("#ingestion_user_role").attr("disabled", "disabled").attr("checked",false);
	        	 $("#realm_administrator_role").attr("disabled", "disabled").attr("checked",false);
	        	 $("#user_tenant").attr("disabled", "disabled").val("");
	        	 $("#user_edorg").attr("disabled", "disabled").val("");
	          } else if($("#user_primary_role option:selected").attr("value")== "SEA Administrator" 
                                || $("#user_primary_role option:selected").attr("value")== "LEA Administrator") {
	        	     $("#ingestion_user_role").removeAttr("disabled")
		        	 $("#realm_administrator_role").removeAttr("disabled")
		        	 $("#user_tenant").removeAttr("disabled");
		        	 $("#user_edorg").removeAttr("disabled");
	          } else if($("#user_primary_role option:selected").attr("value")== "Ingestion User"){
	        	  $("#ingestion_user_role").attr("disabled","disabled").attr("checked",false);
	        	  $("#realm_administrator_role").removeAttr("disabled")
		          $("#user_tenant").removeAttr("disabled");
		          $("#user_edorg").removeAttr("disabled");
                  if(is_lea) { 
	        	    $("#user_edorg option[value=\"" +my_edorg+ "\"]").removeAttr("disabled").text(my_edorg);
                  }
	          } else if($("#user_primary_role option:selected").attr("value")== "Realm Administrator"){
	        	  $("#realm_administrator_role").attr("disabled","disabled").attr("checked",false);
	        	  $("#ingestion_user_role").removeAttr("disabled")
		          $("#user_tenant").removeAttr("disabled");
		          $("#user_edorg").removeAttr("disabled");
	          }
	  });
  };

})
