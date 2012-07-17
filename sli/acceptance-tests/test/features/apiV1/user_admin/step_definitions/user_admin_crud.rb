require_relative '../../../utils/sli_utils.rb'
require 'json'
require 'test/unit'
require "mongo"

Before do
  extend Test::Unit::Assertions
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
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
  if key == "fullName" && value!= ""
    @new_update_user.merge!({"firstName" => value.split(" ")[0], "lastName" => value.split(" ")[1]})
  elsif key!="role" && key!="additional_role"&&value!=""
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
  puts "\n\r"
  puts @new_update_user
  @append_host=true
  if action == "POST"
    restHttpDelete(link+"/"+@new_update_user["uid"])
    restHttpPost(link,@new_update_user.to_json)
  elsif action=="PUT"
    restHttpDelete(link+"/"+@new_update_user["uid"])
    restHttpPost(link,@new_update_user.to_json)
    restHttpPut(link,@new_update_user.to_json)
  end

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

Given /^I have a tenant "(.*?)" and edorg "(.*?)"$/ do |tenant, edorg|
  @tenant = tenant
  @edorg = edorg
end

When /^I navigate to DELETE  "(.*?)"$/ do |wanted_admin_role|
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  new_user = build_user("test_user", [wanted_admin_role], @tenant, @edorg)
  format = "application/json"
  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  restHttpPost("/users", new_user.to_json, format, sessionId)

  idpRealmLogin(@user, nil)
  sessionIdTestAdmin = @sessionId
  restHttpDelete("/users/#{new_user['uid']}", format, sessionIdTestAdmin)
  @response_code = @res.code

  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
end

Then /^I should receive a return code "(.*?)"$/ do |return_code|
  assert_equal(return_code.to_i, @response_code)
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
        out << "#{user['uid']},#{group},#{user['firstName']},#{user['lastName']},#{user['email']},#{user['tenant']},#{user['edorg']}\n"
      }
    end
  }
  out.sort! {|a,b| a <=> b}
  out.each { |entry|
    puts entry
  }
end

def append_hostname(user )
  oldUid = user["uid"]
  newUid = oldUid+"_"+Socket.gethostname
  user.merge!({"uid" => newUid})
  return user
end

def remove_user(user)
  restHttpDelete("/users/"+user["uid"])
end

def build_user(uid, groups, tenant, edorg)
  new_user = {
      "uid" => uid,
      "groups" => groups,
      "firstName" => "Test",
      "lastName" => "User",
      "password" => "#{uid}1234",
      "email" => "testuser@wgen.net",
      "tenant" => tenant,
      "edorg" => edorg,
      "homeDir" => "/dev/null"
  }
  append_hostname(new_user)
end

