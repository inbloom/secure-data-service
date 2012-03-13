require "net/http"
class Check
  attr_accessor :full_name, :authenticated, :realm

  def initialize(access_token)
    json =  Check.json_http_req("#{APP_CONFIG['api_base']}/system/session/check", access_token)
    Rails.logger.debug {"Json response: #{json}"}
    @full_name = json['full_name']
    @authenticated = json['authenticated']
    @realm = json['realm']
  end


  def self.json_http_req(path, access_token)
    headers = {}
    if !access_token.nil?
      headers = {"Authorization" => "Bearer #{access_token}"}
    end
    url = URI.parse(path)
    req = Net::HTTP::Get.new(url.path, headers)
    http = Net::HTTP.new(url.host, url.port)
    if url.to_s.match(/^https/)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    end
    res = http.request(req)
    return ActiveSupport::JSON.decode(res.body)
  end

end
