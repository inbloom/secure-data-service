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

def generateKey  
    aes = OpenSSL::Cipher::Cipher.new('AES-256-CBC')
    aes.encrypt
    
    #retrieve key and iv
    key_file = File.open(@keyFilePath, "rb")
    aes.key = key_file.readline
    aes.iv = key_file.readline

    encryptedClientSecretBin = aes.update(@clientSecret) + aes.final
    aes.reset
    encryptedClientIdBin = aes.update(@clientId) + aes.final
    
    #convert encrypted strings from binary to hex
    encryptedClientSecretHex = encryptedClientSecretBin.unpack("H*")[0]
    encryptedClientIdHex = encryptedClientIdBin.unpack("H*")[0]
    
    puts "client_id: " + encryptedClientIdHex
    puts "client_secret: " + encryptedClientSecretHex
end

if ARGV.count < 3
  puts "Usage: encryptClientIDAndSecret <keyfile> <clientId> <clientSecret>"
  puts "\t keyfile - filename into which the key is stored, which was created by generateRailsKey.rb script"
  puts "\t clientId - the clientId to be encrypted"
  puts "\t clientSecret - the clientSecret to be encrypted"
  puts "Use the specified key file to ecrypt the clientId and clientSecret, outputting the relavent properties"
  exit
else
  @keyFilePath = ARGV[0]
  @clientId = ARGV[1]
  @clientSecret = ARGV[2]
  generateKey()
end
