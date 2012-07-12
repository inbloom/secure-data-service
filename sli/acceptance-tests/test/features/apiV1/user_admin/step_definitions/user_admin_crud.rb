require_relative '../../../utils/sli_utils.rb'
require 'json'
require 'test/unit'

Before do
  extend Test::Unit::Assertions
end

Then /^I have a uid "(.*?)" and role "(.*?)"$/ do |uid, admin_role|
 if uid != nil&& uid != ""
  @logged_in_user = get_user(uid)
  assert_not_nil(@logged_in_user, "Cannot find user with uid = <#{uid}> in \n#{@result}")
  assert_not_nil(@logged_in_user['groups'].index(admin_role), "The following user does not have role = #{admin_role}:\n#{@logged_in_user}")
end
end

Then /^I should receive a list of size "([^"]*)" of "([^"]*)"$/ do |number, wanted_admin_role|
  if number == "1 or more" 
  number =1
  elsif number == "0"
  number = 0
  end
  if number==1 or number ==0
  print_administrator_comma_separated
  @user_with_wanted_admin_role = []
  @result.each { |user|
    user_contains_wanted_role = (user['groups'].index(wanted_admin_role) != nil)
    if user_contains_wanted_role
      if(!@logged_in_user['tenant'].nil?)
        assert_equal(@logged_in_user['tenant'], user['tenant'], "Logged in user should only be able to see users from same tenancy.\nLogged In User: #{@logged_in_user}\nUser shown: #{user}")
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

Then /^one of the accounts has "(.*?)", "(.*?)", "(.*?)"$/ do |fullName, uid, email|
  if(!(fullName.empty? && uid.empty? && email.empty?))
    contains_specified_user = false
    @user_with_wanted_admin_role.each {|user|
      if(user['fullName'] == fullName && user['uid'] = uid && user['email'] = email)
        contains_specified_user = true
        break
      end
    }
    assert(contains_specified_user, "Cannot find user with fullName = #{fullName}, uid = #{uid}, and email = #{email} in the following: #{@user_with_wanted_admin_role.to_yaml}")
  end
end

def get_user(uid)
  @result.each { |user|
    return user if(user['uid'] == uid)
  }
  nil
end

def print_administrator_comma_separated
  administrators = ["SLC Operator", "LEA Administrator", "SEA Administrator", "Realm Administrator", "Ingestion Administrator"]
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