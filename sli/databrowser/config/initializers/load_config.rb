# Lets load the API configuration
APP_CONFIG = YAML::load_file("#{Rails.root}/config/config.yml")[Rails.env]

#Now the Simple view configurations
VIEW_CONFIG = YAML::load_file("#{Rails.root}/config/views.yml")
