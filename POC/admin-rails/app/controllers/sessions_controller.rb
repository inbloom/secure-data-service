class SessionsController < ApplicationController
  def destroy
    reset_session
  end

end
