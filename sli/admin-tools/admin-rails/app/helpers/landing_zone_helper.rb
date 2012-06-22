module LandingZoneHelper

  @@rsaRegex = /\A---- BEGIN SSH2 PUBLIC KEY ----[\r|\n|(\r\n)](.{0,72}[\r|\n|(\r\n)])+---- END SSH2 PUBLIC KEY ----\z/

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

  # key conversion
  #newKey = %x[ssh-keygen -e -f ~/.ssh/id_rsa.pub]

  def self.create_key(key, uid)
    keyFile = File.join(APP_CONFIG['rsa_key_dir'], uid)
    Rails.logger.debug("Writing public key to #{keyFile}")
    file = File.new(keyFile, "w")
    file.write(key)
    file.close()
  end

end
