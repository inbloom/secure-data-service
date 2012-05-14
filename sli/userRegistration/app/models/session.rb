class Session
  def self.valid?(session)
  	session.has_key? :email
  end
end