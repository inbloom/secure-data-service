class Role < SessionResource
  self.site = "https://testapi1.slidev.org/api/rest/admin"
  attr_readonly :name, :rights
end
