smtp_settings = {
  :address              => APP_CONFIG['email_host'],
  :port                 => APP_CONFIG['email_port'],
  :enable_starttls_auto => !!APP_CONFIG['email_tls']
}

username = APP_CONFIG["email_username"]
password = APP_CONFIG["email_password"]
if username && password && (username.strip != "") && (password.strip != "")
    smtp_settings[:user_name] = username.strip 
    smtp_settings[:password] = password.strip 
    smtp_settings[:authentication] = :plain
end 

ActionMailer::Base.smtp_settings = smtp_settings
ActionMailer::Base.default_url_options[:host] = APP_CONFIG["email_replace_uri"].gsub(/http[s]*:\/\//, '')
# Mail.register_interceptor(DevelopmentMailInterceptor) if Rails.env.development?
