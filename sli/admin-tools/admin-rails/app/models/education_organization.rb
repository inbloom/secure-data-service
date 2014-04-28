class EducationOrganization < SessionResource
  self.site = APP_CONFIG['api_base'] + '/v1' if APP_CONFIG['api_base'].index('v1') == nil
  self.collection_name = 'educationOrganizations'

  schema do
    string 'id'
    boolean 'is_allowed'
    string 'stateOrganizationid'
    string 'nameOfInstituton'
    string 'organizationCategories' # Really: an array
    string 'parentEducationAgencyReference' # Really: an array
  end

  # Get list of EducationOrganization objects that are immediate children of edOrg with given ID
  def self.get_edorg_children(edOrg)
    findAllInChunks({'parentEducationAgencyReference' => edOrg})
  end

end
