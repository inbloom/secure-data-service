class OnBoarding < SessionResource

  self.collection_name = "provision"
  schema do
    string  "tenantId"
    string  "stateOrganizationId"
  end

end
