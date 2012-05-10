
class EulasController < ApplicationController
  
  SUBJECT = "SLI Account Verification Request"
  
  REST_HEADER = {
    "Content-Type" => "application/json",
    "content-type" => "application/json",
    "accept" => "application/json"
  }
  
  # GET /eula 
  def show
    respond_to do |format|
      format.html 
    end
  end
  
  def get_email_info(guid)
    url = APP_CONFIG['api_base'] + "/" + session[:guuid]
    res = RestClient.get(url, REST_HEADER){|response, request, result| response }
    
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
  
  def email_user_account_verification
    guid = session[:guuid]
    
    user_email_info = get_email_info guid
    
    email_conf = {
      :host => APP_CONFIG["email_host"],
      :port => APP_CONFIG["email_port"],
      :sender_name => APP_CONFIG["email_sender_name"],
      :sender_email_addr => APP_CONFIG["email_sender_address"],
    }
    
    message = "Your SLI account has been created pending email verification.\n" <<
      "\n\nPlease visit the following link to confirm your account:\n" <<
      "\n\n" + APP_CONFIG["validate_base"] + "/#{guid}\n\n"
    
    email = Emailer.new email_conf
    email.send_approval_email(
      user_email_info["email_address"], 
      user_email_info["first_name"], 
      user_email_info["last_name"],
      SUBJECT, 
      message
    )
  end

  def create
    if Eula.accepted?(params)
      email_user_account_verification()
      render :finish
    else 
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
