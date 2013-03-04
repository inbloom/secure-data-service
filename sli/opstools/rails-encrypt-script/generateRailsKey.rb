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
    
    #generate a random key and iv
    key = aes.random_key()
    iv = aes.random_iv()
    
    aes.key = key
    aes.iv = iv

    file = File.new(@keyFilePath, "w")
    file.puts(key.inspect[1..-2])
    file.puts(iv.inspect[1..-2])

    puts "encryption_keyfile: " + File::expand_path(@keyFilePath)
    puts "encryption_iv: " + iv.inspect[1..-2]
    
end

if ARGV.count < 1
  puts "Usage: generateRailsKey <keyfile>"
  puts "\t keyfile - filename into which the key will be generated"
  puts "Generates an AES-256-CBC key into the specified file and outputting the relavent properties"
  exit
else
  @keyFilePath = ARGV[0]
  generateKey()
end
