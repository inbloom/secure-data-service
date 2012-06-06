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
  
  test "idp validation" do
    realm = Realm.new
    realm.idp = Realm::Idp.new
    realm.name = "Simple"
    realm.uniqueIdentifier = "Something else"
    realm.idp.id = nil
    assert(!realm.idp.valid?, "Validation should fail")
  end
end
