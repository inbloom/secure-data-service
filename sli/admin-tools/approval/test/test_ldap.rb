=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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


require 'test/unit'
require "socket"

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'

class TestLdap < Test::Unit::TestCase

  # Note: Socket.gethostname is used to ensure uniqueness when testers are 
  # running this simultaneously on different machines
  Jd_email = "jdoe@example.com_#{Socket.gethostname}"
  Jd_emailtoken = "0102030405060708090A0B0C0D0E0F"
  User_info = {
      :first        => "John",
      :last         => "Doe",
      :email        => Jd_email,
      :password     => "secret",
      :vendor       => "Acme Inc.",
      :emailtoken   => Jd_emailtoken,
      :status       => "submitted",
      :homedir      => 'test',
      :uidnumber    => '456',
      :gidnumber    => '123',
      :tenant       => 'testtenant',
      :edorg        => 'testedorg',
      :emailAddress => Jd_email,
      :resetKey     => ""
  }

  # Note: Socket.gethostname is used to ensure uniqueness when testers are 
  # running this simultaneously on different machines
  Td_email = "tdoe@example.com_#{Socket.gethostname}"
  Td_user_info = User_info.clone
  Td_user_info[:first] = "Tom"
  Td_user_info[:email] = Td_email

  Ldap_generated_keys = [:created, :updated]
  All_keys = [:first, :last, :email, :password, :vendor, :emailtoken,
              :status, :homedir, :uidnumber, :gidnumber, :tenant, :edorg, :cn, :emailAddress, :resetKey]

  Email1 = "dog_cat_hamster_platypus_elephant"
  Email2 = "mouse_snake_lion_cat_horse"
  Email3 = "car_truck_horse_camel"
  Email4 = "python_mongoose_anteater_cow"
  Email5 = "pig_cat_cow_porcupine"

  def setup
    # set up a connection without 
    @ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=Local,ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{", false)
    @password_policy = false

    #@ldap = LDAPStorage.new("rcldap01.slidev.org", 636, "ou=DevTest,dc=slidev,dc=org", "cn=admin,dc=slidev,dc=org", "Y;Gtf@w{", true)
    #@password_policy = true 

    @ldap.delete_user(Jd_email)
    @ldap.delete_user(Td_email)

    @ldap.remove_user_group(Td_email, "testgroup")
    @ldap.remove_user_group(Jd_email, "testgroup")

    @ldap.delete_user(Email1)
    @ldap.delete_user(Email2)
    @ldap.delete_user(Email3)
    @ldap.delete_user(Email4)
    @ldap.delete_user(Email5)

    all_testusers = @ldap.search_users "*#{Socket.gethostname}*"
    all_testusers.each do |u|
      @ldap.delete_user u[:email]
    end
  end

  def assert_equal_user_info(expected, actual)
    All_keys.each do |x|
      if (x.to_s.downcase != "password") && (x.to_s.downcase != "cn")
        assert_equal expected[x], actual[x]
      end
    end
  end

  def test_keys
    # to make sure that new tests are added when a new key is added to user_info
    result = @ldap.create_user(User_info)
    found_user = @ldap.read_user(Jd_email)

    keys = [].concat(All_keys).concat(Ldap_generated_keys)
    unaccounted = found_user.keys.reject{|x| keys.include? x}
    assert unaccounted.empty?
    @ldap.delete_user(User_info[:email])
  end

  def test_create_read_delete
    result = @ldap.create_user(User_info)
    found_user = @ldap.read_user(Jd_email)
    assert_equal_user_info(User_info, found_user)

    found_user.each do |attribute, values|
      # values.each do |value|
      #   puts "      --->#{value}"
      # end
    end

    @ldap.create_user(Td_user_info)
    @ldap.add_user_group(Jd_email, "testgroup")
    @ldap.add_user_group(Td_email, "testgroup")
    @ldap.add_user_group(Jd_email, "abcgroup")
    @ldap.add_user_group(Td_email, "abcgroup")

    jd_user_groups = @ldap.get_user_groups(Jd_email)
    assert jd_user_groups.include?("testgroup")
    assert jd_user_groups.include?("abcgroup")

    @ldap.get_user_groups(Jd_email).each do |group_id|
      @ldap.remove_user_group(Jd_email, group_id)
    end

    @ldap.get_user_groups(Td_email).each do |group_id|
      @ldap.remove_user_group(Td_email, group_id)
    end

    @ldap.delete_user(Td_user_info[:email])
    @ldap.delete_user(User_info[:email])
  end

  def test_update_user_info
    result = @ldap.create_user(User_info)
    found_user = @ldap.read_user(User_info[:email])
    assert_equal_user_info(User_info, found_user)
    #update the fields except for email, which uniquely id a user
    to_update = {
        :first      => "#{found_user[:first]}UpdateTest",
        :last       => "#{found_user[:last]}UpdateTest",
        :email      => found_user[:email],
        :vendor     => "#{found_user[:vendor]}UpdateTest",
        :emailtoken => "#{found_user[:emailtoken]}UpdateTest",
        :status     => "#{found_user[:status]}UpdateTest",
        :homedir    => "#{found_user[:homedir]}/updateTest",
        :uidnumber  => "#{found_user[:uidnumber]}890",
        :gidnumber  => "#{found_user[:gidnumber]}890",
        :tenant     => "#{found_user[:tenant]}890",
        :edorg      => "#{found_user[:edorg]}890"
    }
    @ldap.update_user_info(to_update)
    updated_found_user = @ldap.read_user(User_info[:email])

    allow_updating = [
        :first,
        :last,
        :password,
        :vendor,
        :emailtoken,
        :homedir,
        :tenant,
        :edorg
    ]

    to_update.keys.each do |key|
      if allow_updating.include? key
        assert_equal to_update[key], updated_found_user[key]
      else
        assert_equal found_user[key], updated_found_user[key]
      end
    end
    @ldap.delete_user(User_info[:email])
  end

  def build_user_info(email)
    new_user_info = User_info.clone
    new_user_info[:email] = "#{email}_#{Socket.gethostname}"
    new_user_info
  end

  def test_search_users
    @ldap.create_user(build_user_info Email1)
    @ldap.create_user(build_user_info Email2)
    @ldap.create_user(build_user_info Email3)
    @ldap.create_user(build_user_info Email4)
    @ldap.create_user(build_user_info Email5)

    search_result = @ldap.search_users "*cat*"
    assert_equal 3, search_result.size

    search_result = @ldap.search_users "*cow*"
    assert_equal 2, search_result.size

    search_result = @ldap.search_users "*pig*"
    assert_equal 1, search_result.size

    search_result = @ldap.search_users "*blah blah blah*"
    assert_equal 0, search_result.size
  end

  def test_minimal_arguments
    @ldap.delete_user(Jd_email)
    test_user_info = {
        :first      => "John",
        :last       => "Doe",
        :email      => Jd_email,
        :password   => "secret",
        :emailtoken => "abc",
        :homedir    => "-",
        :status     => "submitted",
        :emailAddress => Jd_email
    }
    @ldap.create_user(test_user_info)

    # make sure the login shell is set correctly 
    raw_entry = @ldap.search_users_raw(Jd_email)
    assert_equal raw_entry[0][:loginShell][0], "/sbin/nologin"
    @ldap.delete_user(test_user_info[:email])
  end

  def test_packed_fields
    @ldap.delete_user(Jd_email)
    test_user_info = {
        :first      => "John",
        :last       => "Doe",
        :email      => Jd_email,
        :password   => "secret",
        :emailtoken => "abc",
        :homedir    => "-",
        :status     => "submitted",
        :emailAddress => Jd_email
    }

    @ldap.create_user(test_user_info)
    ldap_raw = @ldap.search_users_raw("*#{Jd_email}*")
    desc = ldap_raw[0]
    assert !!desc
    desc = desc[:description][0]
    assert !desc || (desc.strip == "")

    tu_update = {
        :email => test_user_info[:email],
        :tenant => "testtenant"
    }
    @ldap.update_user_info(tu_update)

    ldap_raw = @ldap.search_users_raw("*#{Jd_email}*")
    assert ldap_raw && (ldap_raw[0]) && ldap_raw[0][:description] && ldap_raw[0][:description][0]
    desc = ldap_raw[0][:description][0]
    assert desc.strip == "tenant=testtenant"

    tu_update = {
        :email => test_user_info[:email],
        :edorg => "testedorg"
    }
    @ldap.update_user_info(tu_update)
    ldap_raw = @ldap.search_users_raw("*#{Jd_email}*")
    assert ldap_raw && (ldap_raw[0]) && ldap_raw[0][:description] && ldap_raw[0][:description][0]
    desc = ldap_raw[0][:description][0]
    assert desc.strip == "tenant=testtenant\nedOrg=testedorg"

    @ldap.delete_user(test_user_info[:email])
  end


  def test_abitrary_uid
    # simulate to create a user by hand in ldap 
    uid = "jdoeadmin"
    @ldap.delete_user(uid)

    cn = "Jon Doo"
    ldap_conf = @ldap.get_ldap_config
    dn = "cn=#{cn},#{ldap_conf[:base]}"
    attributes =  {
        :cn                   => cn,
        :objectclass          => ["inetOrgPerson", "posixAccount", "top"],
        :givenname            => "Jon",
        :sn                   => "Doo",
        :uid                  => uid,
        :userPassword         => "secret",
        :o                    => "none",
        :displayName          => "abc",
        :destinationIndicator => "submitted",
        :homeDirectory        => "-",
        :uidNumber            => "500",
        :gidNumber            => "500",
        :description          => "tenant = IL\r\n\r\nedOrg=IL-DAYBREAK\r\n\r\n"
    }


    Net::LDAP.open(ldap_conf) do |ldap|
      if !(ldap.add(:dn => dn, :attributes => attributes))
        raise "Unable to create user in LDAP: #{attributes}."
      end
    end

    found = @ldap.read_user(uid)
    assert !!found
    assert found[:homedir] == "-"
    assert found[:tenant] == "IL"
    assert found[:edorg] == "IL-DAYBREAK"

    test_user_info = {
        :email      => uid,
        :homedir    => "/home/example",
        :tenant     => "IL-NEW",
        :edorg      => found[:edorg]
    }

    @ldap.update_user_info(test_user_info)
    found = @ldap.read_user(uid)
    assert !!found
    assert found[:homedir] == test_user_info[:homedir]
    assert found[:tenant] == test_user_info[:tenant]
    assert found[:edorg] == test_user_info[:edorg]

    # authenticate
    assert @ldap.authenticate(uid, "secret")

    @ldap.delete_user(uid)
  end

  def test_email_with_plus
    plus_email = "jdoe+test1@example.com"
    @ldap.delete_user(plus_email)
    test_user_info = {
        :first      => "Jon",
        :last       => "Do",
        :email      => plus_email,
        :password   => "mysecret",
        :emailtoken => "xyz",
        :homedir    => "-",
        :status     => "submitted",
        :emailAddress => plus_email
    }
    @ldap.create_user(test_user_info)

    found_user = @ldap.read_user(plus_email)
    assert found_user[:emailAddress] == test_user_info[:emailAddress]
    assert found_user[:email] == test_user_info[:email]

    @ldap.delete_user(test_user_info[:email])
  end

  def test_invalidpassword
    # create a user 
    @ldap.delete_user(Jd_email)
    test_user_info = {
        :first      => "John",
        :last       => "Doe",
        :email      => Jd_email,
        :password   => "b",
        :emailtoken => "abc",
        :homedir    => "-",
        :status     => "submitted",
        :emailAddress => Jd_email
    }

    if @password_policy
      assert_raise InvalidPasswordException do
        @ldap.create_user(test_user_info)
      end
    end

    assert_nothing_raised InvalidPasswordException do
      test_user_info[:password] = "secret"
      @ldap.create_user(test_user_info)
    end

    if @password_policy
      assert_raise InvalidPasswordException do
        test_user_info[:password] = "a"
        @ldap.update_user_info(test_user_info)
      end
    end

    assert_nothing_raised InvalidPasswordException do
      test_user_info[:password] = "anothersecret"
      @ldap.update_user_info(test_user_info)
    end

    @ldap.delete_user(test_user_info[:email])
  end

  def test_authenticate
    # create a user 
    @ldap.delete_user(Jd_email)
    test_user_info = {
        :first      => "John",
        :last       => "Doe",
        :email      => Jd_email,
        :password   => "secret",
        :emailtoken => "abc",
        :homedir    => "-",
        :status     => "submitted",
        :emailAddress => Jd_email
    }
    @ldap.create_user(test_user_info)

    # authenticate with that user 
    assert @ldap.authenticate(Jd_email, "secret")

    # and make sure that an incorrect password or user cannot authenticate
    assert !(@ldap.authenticate(Jd_email, "wrongpassword"))
    assert !(@ldap.authenticate("xyz" + Jd_email, "secret"))

    #@ldap.delete_user(test_user_info[:email])
  end

  def test_user_count
    assert_nothing_raised { @ldap.read_users().length }
  end

end

