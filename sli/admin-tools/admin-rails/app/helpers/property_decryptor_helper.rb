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

module PropertyDecryptorHelper

  def self.encrypt(toEncrypt)
    key = getKey()
    iv = getIV()

    aes = OpenSSL::Cipher::Cipher.new('AES-256-CBC')
    aes.encrypt
    aes.key = key
    aes.iv = iv
    encrypted = aes.update(toEncrypt) + aes.final

    #convert encrypted strings from binary to hex
    encryptedHex = encrypted.unpack("H*")[0]

    return encryptedHex
  end

  def self.decrypt(toDecrypt)
    key = getKey()
    iv = getIV()

    #convert string to decrypt from hex to binary
    toDecryptBin = [toDecrypt].pack("H*")

    aes = OpenSSL::Cipher::Cipher.new('AES-256-CBC')
    aes.decrypt
    aes.key = key
    aes.iv = iv
    decrypted = aes.update(toDecryptBin) + aes.final

    return decrypted
  end

  def self.getKey()
    keyFile = File.open(APP_CONFIG['encryption_keyfile'], 'rb')
    return keyFile.readline
  end

  def self.getIV()
    return APP_CONFIG['encryption_iv']
  end

end

