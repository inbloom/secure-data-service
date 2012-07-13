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
  #print_administrator_comma_separated
  @user_with_wanted_admin_role = []
  @result.each { |user|
    user_contains_wanted_role = (user['groups'].index(wanted_admin_role) != nil)
    if user_contains_wanted_role
      if(!@logged_in_user['tenantId'].nil?)
        assert_equal(@logged_in_user['tenantId'], user['tenant'], "Logged in user should only be able to see users from same tenancy.\nLogged In User: #{@logged_in_user}\nUser shown: #{user}")
      end
      @user_with_wanted_admin_role << user
    end
  }
  assert( @user_with_wanted_admin_role.length>=number, "Users with group #{wanted_admin_role}: #{@user_with_wanted_admin_role.to_yaml}")
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
  if(!(fullName=="" && uid=="" && email==""))
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