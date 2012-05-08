class EatController < ApplicationController
  
  # Example
  def food
    Resque.enqueue(Eat, params[:food])
    render :text => "Put #{params[:food]} in fridge to eat later."
  end
  
end
