module GeneralRealmHelper
  def get_realm_to_redirect_to(userRealm)
    realmToRedirectTo = nil
    realms = Realm.all
    realms.each do |realm|
        realmToRedirectTo = realm if realm.edOrg.eql? userRealm and realm.edOrg != nil
    end
    return realmToRedirectTo
  end
end