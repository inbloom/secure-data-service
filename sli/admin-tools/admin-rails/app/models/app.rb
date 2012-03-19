class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  schema do 
    string "client_secret", "redirect_uri", "description", "image_url"
    string "name", "client_id", "application_url", "administration_url"
    string "vendor", "version", "method"
    boolean "is_admin", "license_acceptance"
    date "created_on", "updated_on"
    string "organization", "client_type", "scope"
  end
end