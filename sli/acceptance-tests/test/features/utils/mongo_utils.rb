require 'mongo'
require_relative "rakefile_common.rb"

DATABASE_HOST = Property['ingestion_db']
DATABASE_PORT = Property['ingestion_db_port']

#############################################################################################
# Mongo Steps
#############################################################################################

#The following three methods (update_mongo, remove_from_mongo, add_to_mongo) are singleton operators.
#They also keep a record of the changes they made so that they can be reverted after the scenario is done
#Note: query is a query that'd uniquely identify an entity (Generally, a query like {'_id' => id} is enough)

def update_mongo(db_name, collection, query, field, remove, value = nil)
#Note: value is ignored if remove is true (It doesn't matter what the field contains if you're removing it)

  entry = update_mongo_operation(db_name, collection, query, field, remove, value)

  (@mongo_changes ||= []) << entry if entry
end

def update_mongo_operation(db_name, collection, query, field, remove, value = nil)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[db_name]
  coll = db.collection(collection)
  entity = coll.find_one(query)
  entity_iter = entity
  field_list = field.split('.')
  found = true
  field_list.each do |field_entry|
    if entity_iter.is_a? Array
      field_entry = field_entry.to_i
    else
      unless entity_iter.has_key? field_entry
        found = false
        break
      end
    end
    entity_iter = entity_iter[field_entry]
  end
  if !(remove) || found
    entry = {:operation => 'update',
             :tenant => db_name,
             :collection => collection,
             :query => query,
             :field => field,
             :remove => remove,
             :found => found,
             :value => entity_iter,
             :new => value
    }
    if remove
      coll.update(query, {'$unset' => {field => 1}})
    else
      # Trap Mongo errors so we can log what happened
      begin
        coll.update(query, {'$set' => {field => value}})
      rescue Mongo::OperationFailure => mongo_err
        puts "*** \nCAUGHT Mongo::OperationFailure on updating: " + mongo_err.to_s()
        puts "Update operation done in database '" + db_name.to_s() + "' collection '" + collection.to_s() + "'"
        puts "Update filter query was:\n" + query.to_s()
        puts "Update operation was attempting to set field '" + field.to_s() + "' to value:\n'" + value.to_s() + "'\n"
        # Rethrow
        raise mongo_err
      end
    end
  end
  conn.close

  return entry

end



# add or remove the given right on the given role within the given tenant
Given /^I change the custom role of "([^"]*)" in tenant "([^"]*)" to (add|remove) the "([^"]*)" right$/ do |role, tenant, function, right|
  tenant = convertTenantIdToDbName tenant
  puts("tenant: " + tenant)
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  add = (function == 'add')
  role_coll = db.collection('customRole')
  custom_roles = role_coll.find_one()

  roles = custom_roles['body']['roles']
  index = roles.index {|entry| entry['names'].include?(role)}

puts roles[index]['rights'].to_s

  if add
    roles[index]['rights'] << right
  else
    roles[index]['rights'].delete_if {|entry| entry == right}
  end

puts roles[index]['rights'].to_s
puts custom_roles['_id'].to_s

  update_mongo(tenant,'customRole',{},"body.roles.#{index}.rights", false, roles[index]['rights'])

  enable_NOTABLESCAN()
  conn.close

end

#  if custom_roles.nil?
#    if add
#      role_coll.add
#  else
