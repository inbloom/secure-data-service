ActionMailer::Base.smtp_settings = {
  :address              => APP_CONFIG['email_host'],
  :port                 => APP_CONFIG['email_port'],
  :enable_starttls_auto => true
}

ActionMailer::Base.default_url_options[:host] = APP_CONFIG["email_replace_uri"].gsub(/http[s]*:\/\//, '')
# Mail.register_interceptor(DevelopmentMailInterceptor) if Rails.env.development?