require 'test_helper'

class RealmTest < ActiveSupport::TestCase
  test "get realms" do
    realms = Realm.all
    assert_not_nil(realms)
    assert_equal(realms.size, @realm_fixtures.size)
  end
end
