=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


module GeneralRealmHelper
  def get_realm_to_redirect_to(userRealm)
    realmToRedirectTo = []
    realms = Realm.all
    realms.each do |realm|
      realmToRedirectTo.push realm if realm.edOrg.eql? userRealm and realm.edOrg != nil
    end
    return realmToRedirectTo
  end
end