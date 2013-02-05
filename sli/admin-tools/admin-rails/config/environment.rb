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
