require 'rest-client'

class WaitlistUser < SessionResource
  attr_accessor :email
  
  #TODO: the URL_HEADER is litter all over the code. Need to figure out how to refactor it.
  URL_HEADER = {
    "Content-Type" => "application/json",
    "content_type" => "json",
    "accept" => "application/json"
  }
  
  def initialize(waitlist_user = nil)
    @waitlist_user = waitlist_user
  end
  
  def save
    # TODO: There's definitely something wrong here. Should be using some built-in validator. Not good enough with Ruby on Rails to figure how to fix it.
    if( @waitlist_user["email"].blank? || !(@waitlist_user["email"] =~ /^[a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$/))
      return false
    end
    restResult = RestClient.post(APP_CONFIG['api_base']+"/v1/userAccounts/createWaitingListUser", @waitlist_user.to_json, URL_HEADER) {|response, request, result| response }
    restResult.code == 201
  end
  
  def persisted?
    false
  end
end