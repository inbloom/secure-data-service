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



class TransitionActionConfig

  def initialize(is_sandbox, emailer)
    @is_sandbox = is_sandbox
    @emailer = emailer
  end

  def transition(user)
    #if user[:status] == ApprovalEngine::STATE_APPROVED
    #  # TODO: Below should not be hardcoded and should be configurable by admin.
    #  # TODO: check that user[:emailAddress] is valid-ish email (regex?)
    #  email = {
    #      :email_addr => user[:emailAddress],
    #      :name       => "#{user[:first]} #{user[:last]}"
    #  }
    #  if @is_sandbox
    #    email[:subject] = "Welcome to the SLC Developer Sandbox"
    #    template=File.read("#{File.expand_path('../../template/welcome_email_sandbox_text.template',__FILE__)}")
    #  else
    #    template=File.read("#{File.expand_path('../../template/welcome_email_prod_text.template',__FILE__)}")
    #    email[:subject] = "Welcome to the SLC Developer Program"
    #  end
    #  email_content = ERB.new(template)
    #  template_data = {:firstName => user[:first],
    #                   :landingZoneLink => "__URI__/landing_zone",
    #                   :portalLink => "__PORTAL__" }
    #  email[:content] = email_content.result(ErbBinding.new(template_data).get_binding)
    #  @emailer.send_approval_email email
    #end
  end
end