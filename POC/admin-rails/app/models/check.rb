require "net/http"
class Check
  attr_accessor :full_name, :authenticated

  def initialize(sessionId)
    json =  Check.json_http_req('https://testapi1.slidev.org/api/rest/system/session/check', sessionId)
    @full_name = json['full_name']
    @authenticated = json['authenticated']
  end


  def self.json_http_req(path, sessionId)
    url = URI.parse(path)
    req = Net::HTTP::Get.new(url.path)
    req.add_field("sessionId", sessionId)
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    res = http.request(req)
    return ActiveSupport::JSON.decode(res.body)
  end

end
