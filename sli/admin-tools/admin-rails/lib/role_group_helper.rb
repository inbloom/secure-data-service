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


module RoleGroupHelper

  OPERATOR = "Operator"
  SANDBOX_ADMINISTRATOR = "Sandbox Administrator"
  APPLICATION_DEVELOPER = "Application Developer"
  APPLICATION_DEVELOPER_DOWNCASE = "application_developer"
  INGESTION_USER = "Ingestion User"
  INGESTION_USER_DOWNCASE = "ingestion_user"
  INBLOOM_OPERATOR = "SLC Operator" #TODO: inBloom Operator
  SANDBOX_INBLOOM_OPERATOR = "Sandbox SLC Operator" #TODO: Sandbox inBloom Operator
  SEA_ADMINISTRATOR = "SEA Administrator"
  LEA_ADMINISTRATOR = "LEA Administrator"
  REALM_ADMINISTRATOR = "Realm Administrator"
  IT_ADMINISTRATOR = "IT Administrator"

  # Check for user groups
  def includes_sandbox_admin_group?(groups)
    groups.include? SANDBOX_ADMINISTRATOR
  end

  def includes_app_developer_group?(groups)
    groups.include? APPLICATION_DEVELOPER or groups.include? APPLICATION_DEVELOPER_DOWNCASE
  end

  def includes_ingestion_user_group?(groups)
    groups.include? INGESTION_USER or groups.include? INGESTION_USER_DOWNCASE
  end

  def includes_operator_group?(groups)
    groups.each do |group|
      return true if group.include? OPERATOR
    end
    return false
  end

  def includes_sea_admin_group?(groups)
    groups.include? SEA_ADMINISTRATOR
  end

  def includes_lea_admin_group?(groups)
    groups.include? LEA_ADMINISTRATOR
  end

  def includes_realm_admin_group?(groups)
    groups.include? REALM_ADMINISTRATOR
  end


end