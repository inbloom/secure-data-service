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

require 'openssl'

def encrypt(keyFilePath, password, property)
	aes = OpenSSL::Cipher::Cipher.new('AES-256-CBC')
    aes.encrypt
    
    #retrieve key and iv
    key_file = File.open(keyFilePath, "rb")
    aes.key = key_file.readline
    aes.iv = key_file.readline
    
    encryptedLDAPPassBin = aes.update(password) + aes.final
    encryptedLDAPPassHex = encryptedLDAPPassBin.unpack("H*")[0]
    
    puts "#{property}: #{encryptedLDAPPassHex}"
end
