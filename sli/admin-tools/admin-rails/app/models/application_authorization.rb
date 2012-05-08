class ApplicationAuthorization < SessionResource
  self.collection_name = "applicationAuthorization"
  schema do
    string "authId", "authType"
  end


end