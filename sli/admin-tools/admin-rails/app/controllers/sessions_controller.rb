class SessionsController < ApplicationController
  def destroy
    reset_session
    url = URI.parse("#{APP_CONFIG['api_base']}/system/session/logout")
    req = Net::HTTP::Get.new(url.path)
    req.add_field("sessionId", SessionResource.auth_id)
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    res = http.request(req)
  end

end
