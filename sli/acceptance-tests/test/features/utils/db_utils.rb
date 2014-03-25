require 'mongo'
require_relative 'db_client'

class DbUtils

  # The admin realm and sandbox realm IDs are dynamically created during bootstrap,
  # so here we swap out references to those realms in long-lived sessions with
  # the dynamic ID of the realm.
  def self.swap_realm_ids(tenant)
    realm = DbClient.new.for_sli.find_one('realm', 'body.uniqueIdentifier' => 'Shared Learning Collaborative')
    if realm
      realm_id = realm['_id']
      DbClient.new.for_tenant(tenant).open do |db|
        ['ADMIN_REALM_ID_PLACEHOLDER','SANDBOX_REALM_ID_PLACEHOLDER'].each do |placeholder|

          # User sessions
          puts "Updating #{db.count('userSession','body.principal.realm' => placeholder)} user sessions for tenant #{tenant}"
          db.update(
            'userSession',
            {'body.principal.realm' => placeholder},
            {'$set' => {'body.principal.realm' => realm_id}},
            :multi => true
          )

          # Custom roles
          puts "Updating #{db.count('customRole','body.realmId' => placeholder)} custom roles for tenant #{tenant}"
          db.update(
            'customRole',
            {'body.realmId' => placeholder},
            {'$set' => {'body.realmId' => realm_id}},
            :multi => true
          )
        end
      end
    end
  end

end