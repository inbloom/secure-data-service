ActionMailer::Base.smtp_settings = {
  :address              => APP_CONFIG['email_host'],
  :port                 => APP_CONFIG['email_port'],
  :domain               => APP_CONFIG['replace_uri'],
  :enable_starttls_auto => true
}

# ActionMailer::Base.default_url_options[:host] = "localhost:3000"
# Mail.register_interceptor(DevelopmentMailInterceptor) if Rails.env.development?