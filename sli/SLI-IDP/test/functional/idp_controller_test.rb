require 'test_helper'

class IdpControllerTest < ActionController::TestCase
  test "should get form" do
    get :index, {'SAMLRequest' => 'nZPBbtswDIZfxdDdsePZXSrEAbIEwwJsqxsbO%2Bym2nQrQJY0kW6zt5%2FkpJs3bDnsaPHnT%2FIjvUYxKMu3Iz3pI3wbASk6DUojnwIlG53mRqBErsUAyKnl9fbTR54tUm6dIdMaxaK9z5NakDS6ZE9EFnmSdPBsLPi05QKV9F8L4x75Kk1kZ7Okru%2BO0EkHLSUDkNgqKTCEWPTeuBamlkrWC4XAosO%2BZN4kzrI8z6Do44fizSrO39708Spd3cYizZd5ftumXSu8GiuBKJ%2FhVz7iCAeNJDSVLEuXWZzmcVo02ZIXS57li%2BKm%2BMqi6jLSO6k7qR%2Bvz%2F9wFiH%2F0DRVXN3VDYu%2BgMOJghewzTpQ5FNxN%2BN63da3Di6gZJsZSmHtnOM6mVmf61j%2B2Xsd9pVRsv0ebZUyLzsHgjwGciNMYAdB16uHF9nF%2FSTl5IRGCZpYVFfB%2Fn4USvYS3G97%2FqM5lrx2dLkp6KZ17owmOFG0M4MVTmLgBCfR0iupuWqnPIgj9P%2FD7aqs5W2w9s%2FhSl6M68LW%2FR1C14RxrXF0wfu3fjbn2D9m%2Bxmd%2F1KbHw%3D%3D'}
    assert_response :success
  end

  test "should get login" do
    post :login, {'selected_user' => 'ABC123', 'selected_roles' => ['A', 'B'], 'tenant' => 'IDP'}
    assert_response :success
  end
  
  test "should do logout" do
    post :logout
    assert_response :success
  end

end
