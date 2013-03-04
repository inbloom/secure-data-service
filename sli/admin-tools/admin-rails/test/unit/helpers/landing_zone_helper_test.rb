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



require 'test_helper'

class LandingZoneHelperTest < ActionView::TestCase
  
  test "RSA key should pass validation" do
    assert_equal true, LandingZoneHelper.valid_rsa_key?(
    "---- BEGIN SSH2 PUBLIC KEY ----
Comment: \"2048-bit RSA, converted from OpenSSH by srichards@rclz01\"
AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjB
Li4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tp
a3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8
Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I
3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ip
Oxzd/1/ZKeKmQ5j5R63Q==
---- END SSH2 PUBLIC KEY ----")
  end

  test "RSA key with malformed header should fail validation" do
    assert_equal false, LandingZoneHelper.valid_rsa_key?(
    "--- BEGIN SSH2 PUBLIC KEY ----
Comment: \"2048-bit RSA, converted from OpenSSH by srichards@rclz01\"
AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjB
Li4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tp
a3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8
Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I
3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ip
Oxzd/1/ZKeKmQ5j5R63Q==
---- END SSH2 PUBLIC KEY ----")    
  end

  test "RSA key with malformed footer should fail validation" do
    assert_equal false, LandingZoneHelper.valid_rsa_key?(
    "---- BEGIN SSH2 PUBLIC KEY ----
Comment: \"2048-bit RSA, converted from OpenSSH by srichards@rclz01\"
AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjB
Li4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tp
a3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8
Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I
3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ip
Oxzd/1/ZKeKmQ5j5R63Q==
---- END SSH2 PUBLIC KEY ---")    
  end

  test "RSA key with malformed body should fail validation" do
    assert_equal false, LandingZoneHelper.valid_rsa_key?(
    "---- BEGIN SSH2 PUBLIC KEY ----
Comment: \"2048-bit RSA, converted from OpenSSH by srichards@rclz01\"
AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjB
Li4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tp
a3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8
Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I
3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ipOxzd/1/ZKeKmQ5j5R63Q==
---- END SSH2 PUBLIC KEY ----")    
  end

  test "OpenSSH style RSA key should fail validation" do
    assert_equal false, LandingZoneHelper.valid_rsa_key?(
      "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjBLi4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tpa3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ipOxzd/1/ZKeKmQ5j5R63Q== srichards@wgen.net\n"
    )
  end
  
  test "Key should be copied to specified directory" do
    keyFilePath = File.join(APP_CONFIG['rsa_key_dir'], "test_key")
    
      LandingZoneHelper.create_key("the_key", "test_key")
      assert_equal true, File.file?(keyFilePath)
      #check keyFile contents
      keyFile = File.open(keyFilePath, 'rb')
    contents = keyFile.read
      assert_equal "the_key\n", contents
  end

  test "OpenSSH style RSA key should be convertable to a valid RSA key" do
    newkey = LandingZoneHelper.convert_key("ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjBLi4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tpa3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ipOxzd/1/ZKeKmQ5j5R63Q== srichards@wgen.net", "test_key")
    assert_not_nil newkey
    assert_equal true, LandingZoneHelper.valid_rsa_key?(newkey)
  end

  test "Invalid OpenSSH style RSA key should not be convertable" do
    newkey = LandingZoneHelper.convert_key("ssh-rsa wsY6VVxbNkC4a12GM2ipOxzd/1/ZKeKmQ5j5R63Q== srichards@wgen.net", "test_key")
    assert_nil newkey
  end

end
=end
