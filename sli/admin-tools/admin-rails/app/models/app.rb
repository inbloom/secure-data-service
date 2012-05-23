class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  validates_presence_of [:description, :application_url, :name, :redirect_uri, :vendor], :message => "must not be blank"
  validates_numericality_of :version, :message => "is not a number (eg 1.0)"
  # validate :organization_must_not_be_blank
  # 
  # def organization_must_not_be_blank
  #   if self.developer
  #     errors.add(:developer_info, "must not be blank")
  #   end
  # end
    
  schema do 
    string "client_secret", "redirect_uri", "description", "image_url"
    string "name", "client_id", "application_url", "administration_url"
    string "version", "behavior"
    boolean "is_admin", "license_acceptance", "installed", "bootstrap"
    time "created", "updated"
    string "authorized_ed_orgs", "vendor"

  end
  

  class Registration < SessionResource
    schema do
      string "status"
    end
  end
end


