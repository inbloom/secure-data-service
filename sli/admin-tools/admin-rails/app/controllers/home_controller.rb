=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

class HomeController < ApplicationController


  SANDBOX_ADMINISTRATOR = "Sandbox Administrator"
  SLC_OPERATOR = "SLC Operator"
  SEA_ADMINISTRATOR = "SEA Administrator"
  LEA_ADMINISTRATOR = "LEA Administrator"
  SANDBOX_ALLOWED_ROLES = [SANDBOX_ADMINISTRATOR]
  PRODUCTION_ALLOWED_ROLES = [SLC_OPERATOR, SEA_ADMINISTRATOR, LEA_ADMINISTRATOR]


  def index
    respond_to do |format|
      format.html # index.html.erb
    end
  end

  def has_users_access?
    if APP_CONFIG['is_sandbox']
      allowed_roles = SANDBOX_ALLOWED_ROLES
    else
      allowed_roles = PRODUCTION_ALLOWED_ROLES
    end
    overlap_roles = allowed_roles & session[:roles]
    if overlap_roles.length>0
      true
    else
      false
    end
  end


end
