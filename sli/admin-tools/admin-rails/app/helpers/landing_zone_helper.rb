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

require 'fileutils'

module LandingZoneHelper

  @@rsaRegex = /\A---- BEGIN SSH2 PUBLIC KEY ----[\r|\n|(\r\n)](.{0,72}[\r|\n|(\r\n)])+---- END SSH2 PUBLIC KEY ----\z/
  @@keyConversionCmdPrefix = "ssh-keygen -e -f "
  @@keyConversionTimeout = 3

  # Lightweight check of RSA key format - will not reject all invalid keys; just makes sure they look pretty much right
  # SEE: RFC4716
  #
  # First line MUST be "---- BEGIN SSH2 PUBLIC KEY ----"
  # Middle lines must not be longer than 72 characters, excluding line terminators
  # Last line MUST be "---- END SSH2 PUBLIC KEY ----"
  #
  # Future improvement: each line MUST NOT be longer than 72 8-bit bytes excluding line termination characters
  #
  # Input Parameters:
  #   - key : RSA key string
  #
  # Returns:
  #     true for valid key, false for invalid
  #
  def self.valid_rsa_key?(key)
    Rails.logger.debug("Using regex #{@@rsaRegex}")
    return @@rsaRegex.match(key) ? true : false
  end

  def self.create_key(key, uid)
    rsaKeyDir = APP_CONFIG['rsa_key_dir']
    # ensure the rsa key directory exists
    FileUtils.makedirs(rsaKeyDir)
    keyFile = File.join(rsaKeyDir, uid)
    Rails.logger.debug("Writing public key to #{keyFile}")
    # Format key as needed by sftp: 
    # remove carrage returns
    key = key.tr("\r", "")
    # append trailing newline. 
    key = key + "\n"
    file = File.new(keyFile, "w")
    file.write(key)
    file.close()
  end

  def self.convert_key(key, uid)
    tempKeyFile = File.join(APP_CONFIG['tmp_dir'], uid + "_tmpKey")
    Rails.logger.debug("Converting public key in #{tempKeyFile}")
    file = File.new(tempKeyFile, "w")
    file.write(key)
    file.close()
    cmd = @@keyConversionCmdPrefix + tempKeyFile
    newkey = ExternalProcessRunner.run(cmd,@@keyConversionTimeout)
    File.delete(tempKeyFile)
    unless newkey.nil?
      return newkey.chop
    else
      return nil
    end
  end

end
