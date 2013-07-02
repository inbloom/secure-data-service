require 'bundler'
require 'mongo'

###
### This is a quick util to setup a BE application.
### I created this to help with miniSLIRP testing,
### because it is not running API/Admin and setting up apps
### usnig just the mongo shell is annoying.
###
### It is totally unsupported/tested.  Use at your own risk.
###

raise "Usage: <host>:<port>" if (ARGV.size != 1)

host = ARGV[0].split(':')[0]
port = ARGV[0].split(':')[1]

@conn = Mongo::Connection.new(host, port)
@sli = @conn.db('sli')


def input
  STDOUT.print 'Choice: '
  STDIN.gets.chomp
end

def generate_app
  {
      '_id' => 'c6251365-bbbb-bbbb-bbbb-aaece6fd5136',
      'body' => {
          'isBulkExtract' => true,
          'name' => 'Generated Application',
          'client_id' => 'pavedz00ua',
          'authorized_ed_orgs' => [],
          'registration' => {'status' => 'APPROVED'}
      }
  }
end

puts
puts "-----------------------------------------------------------------"
puts "This utility authorizes a bulk extract application for an edorg."
puts "If you use it to generate an application, note the app is the bare"
puts "  minimum required to get BE working.  The app will not be usable"
puts "  through the API."
puts "The generated app uses one of existing app's client-id / cert."
puts "-----------------------------------------------------------------"
puts

puts "Select an application (N to generate new):"
apps = []
i = 0
@sli['application'].find().each do |app|
  apps << app
  puts "#{i}:\t#{app['body']['name']}\t\t#{app['_id']}}"
  i += 1
end
puts "N:\tCreate new application"

choice = nil
choice = input() until choice =~ /\d+|[nN]/
app = apps[choice.to_i] if choice =~ /\d+/
app = generate_app if choice =~ /[nN]/

tenants = []
puts "Choose a tenant:"
i = 0
@sli['tenant'].find().each do |tenant|
  tenants << tenant
  puts "#{i}:\t#{tenant['body']['tenantId']}\t\t#{tenant['body']['dbName']}"
  i += 1
end
choice = nil
choice = input() until choice =~ /\d+/
tenant = tenants[choice.to_i]



def print_edorg_hierarchy(edorg, edorg_map, edorg_list, i, depth)
  space = "\t" * depth
  if edorg['type'] != 'school'
    puts "#{i}: (#{edorg['classification']})#{space}#{edorg['body']['stateOrganizationId']}\t\t#{edorg['body']['nameOfInstitution']}"
    i += 1
    edorg_list << edorg
  else
    puts "   (#{edorg['classification']})#{space}#{edorg['body']['stateOrganizationId']}\t\t#{edorg['body']['nameOfInstitution']}"
  end
  if edorg['children']
    edorg['children'].each do |child_id|
      i = print_edorg_hierarchy(edorg_map[child_id], edorg_map, edorg_list, i, depth + 1)
    end
  end
  return i
end

puts "Choose a LEA:"
@db = @conn.db(tenant['body']['dbName'])
flat_edorgs = {}
roots = []
@db['educationOrganization'].find().each do | edorg |
  roots << edorg if edorg['body']['parentEducationAgencyReference'] == nil
  if edorg['type'] == 'school'
    edorg['classification'] = 'School'
  elsif edorg['body']['organizationCategories'].include? 'State Education Agency'
    edorg['classification'] = 'SEA'
  else
    edorg['classification'] = 'LEA'
  end
  flat_edorgs[edorg['_id']] = edorg
end
flat_edorgs.each do |id, edorg|
  parent = edorg['body']['parentEducationAgencyReference']
  if parent
    flat_edorgs[parent]['children'] ||= []
    flat_edorgs[parent]['children'] << id
  end
end
i = 0
edorg_list = []
roots.each do |edorg|
  print_edorg_hierarchy(edorg, flat_edorgs, edorg_list, i, 1)
  i += 1
end

choice = nil
choice = input() until choice =~ /\d+/
edorg = edorg_list[choice.to_i]

puts
puts "Application:  #{app['body']['name']}"
puts "Tenant:       #{tenant['body']['tenantId']}"
puts "EducationOrg: #{edorg['body']['stateOrganizationId']}"
puts "Proceed? (Y)"

choice = nil
choice = input() until choice =~ /[yY]/

app['body']['isBulkExtract'] = true
app['body']['authorized_ed_orgs'] << edorg['_id']
appAuth = @db['applicationAuthorization'].find_one({'body.applicationId' => app['_id']})
if appAuth == nil
  appAuth = { '_id' => 'e5a207ad-aaaa-aaaa-aaaa-b41c9511df24',
    'body' => {
        'applicationId' => app['_id'],
        'edorgs' => [ ]
    }}
end
appAuth['body']['edorgs'] << edorg['_id'] unless appAuth['body']['edorgs'].include? edorg['_id']

result = @sli['application'].update({'_id' => app['_id']}, app, {:upsert => true})
puts "Updating Application: #{result}"
result = @db['applicationAuthorization'].update({'_id' => appAuth['_id']}, appAuth, {:upsert => true})
puts "Updating AppAuth: #{result}"



