class Session
  def self.valid?(session)
    if session[:guuid].nil?
      false
    else
      true
    end
  end
end