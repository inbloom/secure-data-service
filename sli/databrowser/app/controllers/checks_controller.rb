class ChecksController < ApplicationController
  def logout
    session[:full_name] = nil
    reset_session
    cookies['iPlanetDirectoryPro'] = nil
    url = URI.parse("#{APP_CONFIG['api_base']}/system/session/logout")
    req = Net::HTTP::Get.new(url.path)
    req.add_field("sessionId", SessionResource.auth_id)
    http = Net::HTTP.new(url.host, url.port)
    if url.to_s.match(/^https/)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    end
    redirect_to root_url
  end
end