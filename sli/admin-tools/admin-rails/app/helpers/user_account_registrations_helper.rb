module UserAccountRegistrationsHelper
  include ReCaptcha::ViewHelper
  
  
	INDEX=0

  	URL=APP_CONFIG['api_base']+"/v1/userAccounts"
  	URL_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
      }
     

     def self.register_user(user_account_registration)
     	@apiResponse ={
	     	"redirect" => true,
	     	"error" =>"",
	     	"guuid" => "0"
	     }
      currEnvironment=APP_CONFIG["is_sandbox"]? "Sandbox":"Production"
      res = RestClient.get(URL+"?userName="+user_account_registration.email+"&environment="+currEnvironment, URL_HEADER){|response, request, result| response }

        if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            if(jsonDocument[INDEX].nil?)
                return persist_record(true,user_account_registration)
            elsif (jsonDocument[INDEX]["validated"] == true)
            	@apiResponse["redirect"]=false
            	@apiResponse["error"]="User name already exists in record"
                return @apiResponse 
            else
                return persist_record(false,user_account_registration,jsonDocument[INDEX]["id"])
            end
        else
          @apiResponse["error"]="Error occurred while storing record"
          @apiResponse["redirect"]=false
          return @apiResponse
        end
      
  end

   def self.persist_record(isPost,user_account_registration,gUID="0")
        
        if user_account_registration.vendor.nil? or user_account_registration.vendor==""
           user_account_registration.vendor = "None"
        end
        
        post_data={
                "userName" =>    user_account_registration.email,
                "firstName" => user_account_registration.firstName,
                "lastName" => user_account_registration.lastName,
                "vendor" => user_account_registration.vendor,
                "validated" => "false",
                "environment" =>  APP_CONFIG["is_sandbox"]? "Sandbox": "Production"
            }
        success=false
        if isPost == true
            commitResult= RestClient.post(URL,post_data.to_json,URL_HEADER){|response, request, result| response }
            if commitResult.code ==201
            	success=true
            	headers = commitResult.raw_headers
            	s = headers['location'][0]
            	@apiResponse["guuid"]= s[s.rindex('/')+1..-1]
            end
        else
        	@apiResponse["guuid"]= gUID
            commitResult= RestClient.put(URL+"/"+gUID,post_data.to_json,URL_HEADER){|response, request, result| response }
             if commitResult.code ==204
            	success=true
            end
        end
       if success
       		if isPost == true
            ApplicationHelper.add_user(user_account_registration)
        else
            ApplicationHelper.update_user_info(user_account_registration)
        end
       else
       	 @apiResponse["error"]="Error occurred while storing record"
         @apiResponse["redirect"]=false
       end
       return @apiResponse 
      end
end