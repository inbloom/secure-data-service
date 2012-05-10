require 'test_helper'

class EulaTest < ActiveSupport::TestCase

  test "eula accepted method" do
    fail_params = {:commit => "No!"}
    fail_params2 = {:some_other_key => "blah"}
    success_params = {:commit => "Accept", :another_param => "nothing"}
    assert Eula.accepted?(success_params)
    assert !Eula.accepted?(fail_params)
    assert !Eula.accepted?(fail_params2)
  end
end
