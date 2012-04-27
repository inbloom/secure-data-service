class EducationOrganization < SessionResource
  self.collection_name = "educationOrganizations"
  schema do
    boolean "is_allowed"
  end

end