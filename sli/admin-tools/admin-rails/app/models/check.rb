require "net/http"
class Check
  attr_accessor :full_name, :authenticated, :realm

  def initialize(sessionId)
    json =  Check.json_http_req("#{APP_CONFIG['api_base']}/system/session/check", sessionId)
    @full_name = json['full_name']
    @authenticated = json['authenticated']
    @realm = json['realm']
  end


  def self.json_http_req(path, sessionId)
    url = URI.parse(path)
    req = Net::HTTP::Get.new(url.path)
    req.add_field("sessionId", sessionId)
    http = Net::HTTP.new(url.host, url.port)
    if url.to_s.match(/^https/)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    end
    res = http.request(req)
    return ActiveSupport::JSON.decode(res.body)
  end

end
