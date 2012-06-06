# Load the rails application
require File.expand_path('../application', __FILE__)

# Put this here until we fix storing OAuth object directly in session
require "oauth_helper"

# Initialize the rails application
SLIAdmin::Application.initialize!

SLIAdmin::Application.configure do
  config.after_initialize do
    if APP_CONFIG['recaptcha_disable'] == true
      # disable the recaptcha validator
      module ReCaptcha::AppHelper
        def validate_recap( p, errors, options = {})
          Rails.logger.warn "ReCaptcha validator is disabled.  Response: #{p['recaptcha_response_field']}"
          return true
        end
      end
    end
  end
end