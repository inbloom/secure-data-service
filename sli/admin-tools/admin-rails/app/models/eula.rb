class Eula
  
  def self.accepted?(params)
    if params[:commit] == "I Accept"
      true
    else 
      false
    end
  end
  
end
