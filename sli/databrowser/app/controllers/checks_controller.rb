class ChecksController < ApplicationController
  def logout
    Entity.url_type = "system/session/logout"
    Entity.format = ActiveResource::Formats::JsonLinkFormat
    response = Entity.get("")
    puts "response = #{response}"

    session[:full_name] = nil
    reset_session
    cookies['iPlanetDirectoryPro'] = nil

    url = URI.parse("#{APP_CONFIG['api_base']}/system/session/logout".gsub("v1/", ""))
    req = Net::HTTP::Get.new(url.path)
    req.add_field("Authorization", "Bearer #{SessionResource.access_token}")
    http = Net::HTTP.new(url.host, url.port)
    if url.to_s.match(/^https/)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    end
    render :layout=> false, :file => "#{Rails.root}/public/LoggedOut.html"
  end
end
