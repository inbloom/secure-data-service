class EducationOrganization < SessionResource
  self.site = APP_CONFIG['api_base'] + "/v1" if APP_CONFIG['api_base'].index("v1") == nil

  self.collection_name = "educationOrganizations"
  schema do
    boolean "is_allowed"
    string  "organizationCategories"
  end

end