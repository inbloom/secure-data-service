class Eula

  def self.accepted?(params)
    params[:commit] == 'I Accept'
  end

end
