smpt_settings = {
  :address              => APP_CONFIG['email_host'],
  :port                 => APP_CONFIG['email_port'],
  :enable_starttls_auto => APP_CONFIG['email_tls'] == "true"],
}

username = APP_CONFIG["email_username"]
password = APP_CONFIG["email_password"]
if username && password && (username.strip != "") && (password.strip != "")
    smtp_settings[:user_name] = username
    smtp_settings[:password] = password
    smtp_settings[:authentication] = :plain
end 

ActionMailer::Base.smtp_settings = smpt_settings
ActionMailer::Base.default_url_options[:host] = APP_CONFIG["email_replace_uri"].gsub(/http[s]*:\/\//, '')
# Mail.register_interceptor(DevelopmentMailInterceptor) if Rails.env.development?