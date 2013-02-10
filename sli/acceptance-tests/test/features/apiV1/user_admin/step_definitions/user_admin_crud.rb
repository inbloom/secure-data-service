require_relative '../../../utils/sli_utils.rb'
require 'json'
require 'test/unit'
require "mongo"

Before do
  extend Test::Unit::Assertions
  @conn = Mongo::Connection.new
  @db = @conn.db(PropLoader.getProps['api_database_name'])
end

After do
  if @created_user 
    idpRealmLogin("operator", nil)
    restHttpDelete("/users/#{@created_user['uid']}")
  end
  @conn.close if @conn != nil
end

Given /^I have logged in to realm "(.*?)" using "(.*?)" "(.*?)"$/ do |realm, user, pass|
  @user = user
  step "I am logged in using \"#{user}\" \"#{pass}\" to realm \"#{realm}\""
end

Given /^I have a role "([^"]*)"$/ do |role|
  @logged_in_user = get_user(@user)
  assert_not_nil(@logged_in_user['roles'].index(role), "The user does not have role = #{role}")
end

Then /^I should receive a list of size "([^"]*)" of "([^"]*)"$/ do |number, wanted_admin_role|
  if number == "1 or more"
  number =1
  elsif number == "0"
  number = 0
  end
  if number==1 or number ==0
    @number =number
    #print_administrator_comma_separated
    @user_with_wanted_admin_role = []
    if @res.code == 200
      @result.each { |user|
        user_contains_wanted_role = (user['groups'].index(wanted_admin_role) != nil)
        if user_contains_wanted_role
          if(!@logged_in_user['tenantId'].nil?)
            assert_equal(@logged_in_user['tenantId'], user['tenant'], "Logged in user should only be able to see users from same tenancy.\nLogged In User: #{@logged_in_user}\nUser shown: #{user}")
          end
        @user_with_wanted_admin_role << user
        end
      }
      assert( (number==0&&@user_with_wanted_admin_role.length==number)||(number==1&&@user_with_wanted_admin_role.length>=number), "Users with group #{wanted_admin_role}: #{@user_with_wanted_admin_role.to_yaml}")
    end
  end
end

Given /^the new\/update user has$/ do
  @new_update_user=Hash.new
end

Given /^"(.*?)" is "(.*?)"$/ do |key, value|
  if key!="role" && key!="additional_role"&&value!=""
    @new_update_user.merge!({key => value})
  elsif key=="role" && value!=""
    @new_update_user.merge!({"groups" => [value]})
  elsif key=="additional_role" && value!=""
    roles = @new_update_user["groups"]
    roles<<value
    @new_update_user.merge!({"groups" => roles })
  end
  @new_update_user.merge!({"homeDir" => "/dev/null","password" => "test1234"})
end

Given /^the format is "(.*?)"$/ do |format|
  @format = format
end

Given /^I navigate to "(.*?)" "(.*?)"$/ do |action, link|
# puts @new_update_user
  @new_update_user = append_hostname(@new_update_user)
 # puts "\n\r"
 # puts @new_update_user
  @append_host=true
  if @new_update_user["uid"] != nil
     # restHttpDelete(link+"/"+@new_update_user["uid"])
     remove_user(@new_update_user)
     idpRealmLogin(@user, nil)
    end
  if action == "POST"
    
    restHttpPost(link,@new_update_user.to_json)
    sleep(1)
  elsif action=="PUT"
   # restHttpDelete(link+"/"+@new_update_user["uid"])
    restHttpPost(link,@new_update_user.to_json)
    sleep(1)
    restHttpPut(link,@new_update_user.to_json)
  end

end

Then /^I (should|should not) see "(.*?)"$/ do |should, uid|
  @res.code.should == 200
  should_see = should.downcase == "should"
  found = false
  @result.each do |user|
    if user["uid"] == uid
      assert(false, "User should not have been returned: #{uid}") unless should_see
      found = true
      break
    end
  end
  assert(found, "User was not returned: #{uid}") if should_see
end

Given /^I create a new "(.*?)" "(.*?)" with tenant "(.*?)" and edorg "(.*?)"$/ do |roles, uid, tenant, edorg|
  roles = roles.split(/,/).map { |r| r.strip }
  user = {}
  user["groups"] = roles
  user["fullName"] = "AT Generated User"
  user["uid"] = uid
  user["email"] = "at_email@doesnot.exist"
  user["tenant"] = tenant
  user["edorg"] = edorg
  user["homeDir"] = "/"
  user["password"] = "Mark Abernathy is my hero"
  user = append_hostname(user)
  puts "User: #{JSON.pretty_generate user}" if $SLI_DEBUG
  restHttpDelete("/users/#{user['uid']}")
  @format = "application/json"
  restHttpPost("/users", user.to_json)
  assert(@res.code == 201, "Could not create user: #{@res}")
  @created_user = user
end

And /^I verify this new user has home directory "(.*?)"$/ do |homeDir|
  idpRealmLogin(@user, nil)
  restHttpGet("/users")
  if @res.code == 200 
    all_users = JSON.parse(@res.body)
    @new_user_in_ldap = all_users.select{|user| user["uid"] == @created_user["uid"]}[0]
  end
  assert(@new_user_in_ldap != nil, "could not found recently created user #{@created_user}")
  assert(@new_user_in_ldap["homeDir"] == homeDir, "newly created user doesn't have homedir /dev/null")
end

Then /^I try to change his home directory to "(.*?)"$/  do |homeDir|
  @new_user_homeDir_in_ldap = @new_user_in_ldap["homeDir"]

  # attempt to update my homedir in ldap
  @new_user_in_ldap["homeDir"] = homeDir
  @new_user_in_ldap.delete "createTime"
  @new_user_in_ldap.delete "modifyTime"
  @new_user_in_ldap["password"] ="Mark Abernathy is still my hero" 
  restHttpPut("/users", @new_user_in_ldap.to_json)
  assert(@res.code == 204, "updating user has failed")
end

And /^It will not change$/ do
  @new_user_in_ldap = nil
  step "I verify this new user has home directory \"#{@new_user_homeDir_in_ldap}\"" 
end

Then /^I (should|should not) see user "(.*?)"$/ do |should, uid|
  should_find = should.downcase == "should"
  uid = append_hostname(uid)
  @format = "application/json"
  step "I navigate to GET \"/users\""
  users = @result
  found = false
  users.each do |user|
    if user["uid"] == uid
      found = true
      break
    end
  end
  assert(found == should_find, "User #{should} be visible: #{uid}")
end

Then /^each account has "(.*?)", "(.*?)", "(.*?)", "(.*?)" and "(.*?)"$/ do |fullName, uid, email, createTime, modifyTime|
  @user_with_wanted_admin_role.each {|user|
    assert_not_nil(user[fullName], "The following user has no #{fullName}: #{user}")
    assert_not_nil(user[uid], "The following user has no #{uid}: #{user}")
    assert_not_nil(user[email], "The following user has no #{email}: #{user}")
    assert_not_nil(user[createTime], "The following user has no #{createTime}: #{user}")
    assert_not_nil(user[modifyTime], "The following user has no #{modifyTime}: #{user}")
  }
end

Then /^one of the accounts has "([^"]*)", "([^"]*)", "([^"]*)"$/ do |fullName, uid, email|
  if(!((fullName=="" && uid=="" && email=="")||@number==0))
    #append host name to new user that will be created
    if @append_host==true && uid!=""
      uid=uid+"_"+Socket.gethostname
    end
    contains_specified_user = false
    @user_with_wanted_admin_role.each {|user|
      if((fullName==""||user['fullName'] == fullName) && (uid==""||user['uid'] == uid) && (email==""||user['email'] == email))
      # puts user["fullName"],user["uid"],user["email"]
      contains_specified_user = true
      break
      end
    }
    assert(contains_specified_user, "Cannot find user with fullName = #{fullName}, uid = #{uid}, and email = #{email} in the following: #{@user_with_wanted_admin_role.to_yaml}")
  end
  #remove the test user that is created for create and update test
  if @append_host==true
    remove_user(@new_update_user)
  end

end

Then /^an account with "(.*?)", "(.*?)", "(.*?)" should not exist$/ do |fullName, uid, email|
  @result.each do |user|
    if fullName == user["fullName"] and uid == user["uid"] and email == user["email"]
      assert(false, "User should not have existed: #{user}")
    end
  end
end

Given /^I have a tenant "(.*?)" and edorg "(.*?)"$/ do |tenant, edorg|
  @tenant = tenant
  @edorg = edorg
end

When /^I navigate to DELETE  "(.*?)" in environment "(.*?)"$/ do |wanted_admin_role, environment|
  new_user = build_user("test_user", [wanted_admin_role], @tenant, @edorg)
  remove_user(new_user)
  if (environment == "production")
    idpRealmLogin("operator", nil)
  elsif (environment == "sandbox")
    idpRealmLogin("sandboxoperator", nil)
  else
    assert(false) # environment must be production or sandbox
  end
  sessionId = @sessionId
  
  format = "application/json"
 # restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  restHttpPost("/users", new_user.to_json, format, sessionId)
  sleep(1)

  idpRealmLogin(@user, nil)
  sessionIdTestAdmin = @sessionId
  restHttpDelete("/users/#{new_user['uid']}", format, sessionIdTestAdmin)
  @response_code = @res.code

  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
end

Then /^I should receive a return code "(.*?)"$/ do |return_code|
  assert_equal(return_code.to_i, @response_code)
end

Then /^I (should|should not) see "(.*?)" on my list of allowed apps$/ do |should, name|
  restHttpGet("/userapps?is_admin=true")
  @res.should_not == nil
  @res.code.should == 200
  @res.body.should_not == nil
  
  apps = JSON.parse(@res.body)
  admin = apps.find { |app| app["name"] == "Admin Apps" }
  if should == "should"
    admin.should_not == nil
    samt = admin["endpoints"].find { |ep| ep["name"] == name }
    samt.should_not == nil
  else
    if admin != nil
      samt = admin["endpoints"].find { |ep| ep["name"] == name }
      samt.should == nil
    end
  end
end

Given /^there is another LEA with "(.*?)" in my "(.*?)" and "(.*?)"$/ do |full_name, tenant, edorg|
  uid=full_name.gsub(" ", "_")
  groups = Array.new
  groups.push("LEA Administrator")
  @given_user=build_user(uid, groups, tenant, edorg)

  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"

  restHttpDelete("/users/#{@given_user['uid']}", format, sessionId)
  restHttpPost("/users", @given_user.to_json, format, sessionId)
end         
    
Then /^I think I am the only LEA in my EdOrg "(.*?)"$/ do |edorg| 
  @result.each do |other|
    if (other['groups'].index("LEA Administrator") != nil) 
      if (other['edorg'] == edorg) 
        assert(other['uid'] == @user, "@user is not the only LEA in EdOrg #{edorg}, #{other['uid']} is also LEA")
      end
    end 
  end

  #clean up 
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/#{@given_user['uid']}", format, sessionId)
end 

Then /^I try to update this new user as "(.*?)"$/ do |roles|
  user = @created_user
  roles = roles.split(/,/).map { |r| r.strip }
  user["groups"] = roles
  puts "User: #{JSON.pretty_generate user}" if $SLI_DEBUG
  @format = "application/json"
  restHttpPut("/users", user.to_json)
  @response_code = @res.code
end

Then /^I delete the test user "(.*?)"$/ do |user|

test_user = Hash.new
test_user.merge!({"uid" => user })
remove_user(append_hostname(test_user))
end

When /^I attempt to delete the user "(.*?)"$/ do |user|
  restHttpDelete("/users/#{user}", "application/json", @sessionId)
  @response_code = @res.code
end

def get_user(uid)
=begin
@result.each { |user|
return user if(user['uid'] == uid)
}
=end
  userSession_coll = @db['userSession']
  userSessions = userSession_coll.find({"body.principal.externalId" => uid})
  user=nil
  userSessions.each do |userSession|
    user=userSession["body"]["principal"]
  end

  return user
end

def print_administrator_comma_separated
  administrators = ["SLC Operator", "LEA Administrator", "SEA Administrator", "Realm Administrator", "Ingestion User", "Sandbox SLC Operator", "Sandbox Administrator","Application Developer"]
  out = []
  @result.each { |user|
    if((administrators & user['groups']).length > 0)
      user['groups'].each { |group|
        out << "#{user['uid']},#{group},#{user['fullName']},#{user['email']},#{user['tenant']},#{user['edorg']}\n"
      }
    end
  }
  out.sort! {|a,b| a <=> b}
  out.each { |entry|
    puts entry
  }
end

def append_hostname(user )
  if user.is_a? Hash
    oldUid = user["uid"]
    if (oldUid != nil)
      newUid = oldUid+"_"+Socket.gethostname
      user.merge!({"uid" => newUid})
    end
    return user
  else
    return user + "_" + Socket.gethostname
  end
end

def remove_user(user)
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+user["uid"],format,sessionId)
  
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+user["uid"],format,sessionId)
end

def build_user(uid, groups, tenant, edorg)
  new_user = {
      "uid" => uid,
      "groups" => groups,
      "fullName" => "Test User",
      "password" => "#{uid}1234",
      "email" => "testuser@wgen.net",
      "tenant" => tenant,
      "edorg" => edorg,
      "homeDir" => "/dev/null"
  }
  append_hostname(new_user)
end

