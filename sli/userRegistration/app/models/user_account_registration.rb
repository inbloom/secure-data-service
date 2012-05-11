
class UserAccountRegistration
  SUBJECT = "SLI Account Verification Request"
  
  URL=APP_CONFIG['api_base']
  URL_HEADER = {
    "Content-Type" => "application/json",
    "content_type" => "json",
    "accept" => "application/json"
  }

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :email, :firstName, :lastName, :password, :vendor
   
  validates_presence_of :firstName, :email,:lastName, :password
  validates_format_of :email, :with => /^[-a-z0-9_+\.]+\@([-a-z0-9]+\.)+[a-z0-9]{2,4}$/i
  validates :password, :confirmation => true #password_confirmation attr
  
  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
    end
  end

  def persisted?
    false
  end
  
  def self.accept(guid)
    
    user_email_info = get_email_info guid
    
    email_conf = {
      :host => APP_CONFIG["email_host"],
      :port => APP_CONFIG["email_port"],
      :sender_name => APP_CONFIG["email_sender_name"],
      :sender_email_addr => APP_CONFIG["email_sender_address"],
    }
    
    userEmailValidationLink = APP_CONFIG["validate_base"] + "/" + ApplicationHelper.get_email_token(user_email_info["email_address"])
    
    message = "Your SLI account has been created pending email verification.\n" <<
      "\n\nPlease visit the following link to confirm your account:\n" <<
      "\n\n#{userEmailValidationLink}\n\n"
    
    email = Emailer.new email_conf
    email.send_approval_email(
      user_email_info["email_address"], 
      user_email_info["first_name"], 
      user_email_info["last_name"],
      SUBJECT, 
      message
    )
  end
  
  #
  # When a user account request rejects the EULA, the user's account is
  # removed from both the LDAP and the database.
  #
  def self.reject(guid)
    res = RestClient.get(URL + "/" + guid, URL_HEADER){|response, request, result| response }
    if (res.code==200)
      jsonDocument = JSON.parse(res.body)
      puts(jsonDocument)
      puts(jsonDocument["userName"])
      ApplicationHelper.remove_user(jsonDocument["userName"])
      res = RestClient.delete(URL+"/"+guid, URL_HEADER){|response, request, result| response }
    end
  end
    
private

  def self.get_email_info(guid)
    url = URL + "/" + guid
    res = RestClient.get(url, URL_HEADER){|response, request, result| response }
    
    if (res.code==200)
        jsonDocument = JSON.parse(res.body)
        return {
            "email_address" => jsonDocument["userName"],
            "first_name" => jsonDocument["firstName"],
            "last_name" => jsonDocument["lastName"],
        }
    else 
        return {
            "email_address" => "NONE",
            "first_name" => "NONE",
            "last_name" => "NONE",
        }
    end
  end
  
end
