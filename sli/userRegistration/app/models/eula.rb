class Eula
  
  def self.accepted?(params)
    if params[:commit] == "Accept"
      true
    else 
      false
    end
  end
  
end
