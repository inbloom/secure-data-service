class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  schema do 
    string "client_secret", "redirect_uri", "description", "image_url"
    string "name", "client_id", "application_url", "administration_url"
    string "vendor", "version", "behavior"
    boolean "is_admin", "license_acceptance", "installed", "bootstrap"
    time "created", "updated"
    string "organization", "developer_info"
    string "authorized_ed_orgs"

  end
  
  class DeveloperInfo < SessionResource
    schema do
      string "organization"
    end
  end

  class Registration < SessionResource
    schema do
      string "status"
    end
  end
end

