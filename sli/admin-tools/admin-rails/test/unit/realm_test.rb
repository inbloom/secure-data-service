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


require 'test_helper'

class RealmTest < ActiveSupport::TestCase
  test "get realms" do
    realms = Realm.all
    assert_not_nil(realms)
    assert_equal(realms.size, @realm_fixtures.size)
  end

  test "simple validation" do
    realm = Realm.new
    realm.idp = Realm::Idp.new
    realm.uniqueIdentifier = "Something"
    realm.name = nil
    assert(!realm.valid?, "Name validation should fail")
  end

  test "name specific validation" do
    realm = Realm.new
    realm.uniqueIdentifier = "Waffles" #valid
    realm.idp = Realm::Idp.new({:id => "Waffles", :redirectEndpoint => "Waffles"})
    realm.name = "Super awesome"
    assert realm.valid?, "Realm should be valid"
    realm.name = "REALM!"
    assert !realm.valid?, "Realm should not be valid (Special Character)"
    realm.name = "RE"
    assert !realm.valid?, "Realm should not be valid (Length)"
  end

  test "unique identifier validation" do
    realm = Realm.new
    realm.name = "Waffles" #valid
    realm.idp = Realm::Idp.new({:id => "Waffles", :redirectEndpoint => "Waffles"})
    realm.uniqueIdentifier = "Super awesome"
    assert realm.valid?, "Realm should be valid: #{realm.errors.to_json}"
    realm.uniqueIdentifier = "REALM!"
    assert !realm.valid?, "Realm should not be valid (Special Character)"
    realm.uniqueIdentifier = "RE"
    assert !realm.valid?, "Realm should not be valid (Length)"
  end

  test "idp validation" do
    realm = Realm.new
    realm.idp = Realm::Idp.new
    realm.name = "Simple"
    realm.uniqueIdentifier = "Something else"
    realm.idp.id = nil
    assert(!realm.idp.valid?, "Validation should fail")
  end
end
