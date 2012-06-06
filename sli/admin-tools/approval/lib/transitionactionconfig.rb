
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