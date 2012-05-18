class Session
  def self.valid?(session)
  	session.has_key? :guuid
  end
end