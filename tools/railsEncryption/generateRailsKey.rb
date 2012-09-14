=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require 'OpenSSL'

def generateKey  
	aes = OpenSSL::Cipher::Cipher.new('AES-256-CBC')
    aes.encrypt
    
    #generate a random key and iv
    key = aes.random_key()
    iv = aes.random_iv()
    
    aes.key = key
    aes.iv = iv

    encryptedClientSecretBin = aes.update(@clientSecret) + aes.final
    encryptedClientIdBin = aes.update(@clientId) + aes.final
    
    #convert encrypted strings from binary to hex
    encryptedClientSecretHex = encryptedClientSecretBin.unpack("H*")[0]
    encryptedClientIdHex = encryptedClientIdBin.unpack("H*")[0]
    
    file = File.new(@keyFilePath, "w")
    file.write(key.inspect[1..-2])
    
    puts "client_id: " + encryptedClientIdHex
    puts "client_secret: " + encryptedClientSecretHex
    puts "encryption_keyfile: " + File::expand_path(@keyFilePath)
    puts "encryption_iv: " + iv.inspect[1..-2]
    
end

if ARGV.count < 1
  puts "Usage: generateRailsKey <keyfile> <clientId> <clientSecret>"
  puts "\t keyfile - filename into which the key will be generated"
  puts "\t clientId - the clientId to be encrypted"
  puts "\t clientSecret - the clientSecret to be encrypted"
  puts "Generates an AES-256-CBC key into the specified file and ecrypts the clientId and clientSecret, outputting the relavent properties"
  exit
else
  @keyFilePath = ARGV[0]
  @clientId = ARGV[1]
  @clientSecret = ARGV[2]
  generateKey()
end
