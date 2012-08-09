=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


module UserAccountRegistrationsHelper
  include ReCaptcha::ViewHelper

  # TODO:  Move me out of the helper, I belong in the model
  def self.register_user(user_account_registration)
    if user_account_registration.vendor.nil? or user_account_registration.vendor==""
      user_account_registration.vendor = "None"
    end
    
    ApplicationHelper.add_user(user_account_registration)
  end

  def increment_counter
    @counter = 0 if !@counter
    @counter = @counter + 1
  end
end
