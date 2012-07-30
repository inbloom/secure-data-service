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
    
  };

})
