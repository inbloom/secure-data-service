class ApplicationController < ActionController::Base
  protect_from_forgery

  def get_time_filter_value
      if (params[:time_filter] == nil || params[:time_filter] == "0")
          start_time = 0 # Catch all... if date filter not specified, grab all events since 1970-01-01
      else
        start_time = Time.now - params[:time_filter].to_i
    end
    return start_time
  end
end
