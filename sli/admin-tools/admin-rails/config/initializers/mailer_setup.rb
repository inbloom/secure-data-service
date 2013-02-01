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


smtp_settings = {
  :address              => APP_CONFIG['email_host'],
  :port                 => APP_CONFIG['email_port'],
  :enable_starttls_auto => !!APP_CONFIG['email_tls']
}

username = APP_CONFIG["email_username"]
password = APP_CONFIG["email_password"]
if username && password && (username.strip != "") && (password.strip != "")
    smtp_settings[:user_name] = username.strip
    smtp_settings[:password] = PropertyDecryptorHelper.decrypt(password.strip)
    smtp_settings[:authentication] = :plain
end 

ActionMailer::Base.smtp_settings = smtp_settings
ActionMailer::Base.default_url_options[:host] = APP_CONFIG["email_replace_uri"].gsub(/http[s]*:\/\//, '')
# Mail.register_interceptor(DevelopmentMailInterceptor) if Rails.env.development?
