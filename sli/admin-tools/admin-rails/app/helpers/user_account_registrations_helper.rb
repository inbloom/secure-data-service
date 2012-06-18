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
