module LandingZoneHelper

  @@rsaRegex = /^---- BEGIN SSH2 PUBLIC KEY ----\n(.{0,72}\n)+---- END SSH2 PUBLIC KEY ----$/

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
    return @@rsaRegex.match(key)? true : false
  end

  # key conversion
  #newKey = %x[ssh-keygen -e -f ~/.ssh/id_rsa.pub]

end
