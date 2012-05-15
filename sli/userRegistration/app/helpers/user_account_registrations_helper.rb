require 'proxy'

module UserAccountRegistrationsHelper
	INDEX=0

  	URL=APP_CONFIG['approval_uri']
     

     def self.register_user(user_account_registration)
     	@apiResponse ={
	     	"redirect" => true,
	     	"error" =>"",
        "verificationToken"=> ""
       }
       @currEnvironment =(APP_CONFIG["is_sandbox"] == true)
       ApprovalEngineProxy.init(URL,@currEnvironment)
      res = ApprovalEngineProxy.doesUserExist(user_account_registration.email)
      jsoBody=JSON.parse(res)
      puts("SAMPLE RESPONSE**********#{jsoBody}")
      if(res["validated"] == true)
        @apiResponse["redirect"]=false
        @apiResponse["error"]="User name already exists in record"
        return @apiResponse 
      else
        return persist_record(res["exists"]==false ,user_account_registration)
      end
  end

   def self.persist_record(isPost,user_account_registration)
        
        if user_account_registration.vendor.nil? or user_account_registration.vendor==""
           user_account_registration.vendor = "None"
        end
        
        ApprovalEngineProxy.init(URL,@currEnvironment)
        if isPost == true
          response=ApprovalEngineProxy.submitUser(user_account_registration)
        else
        	response=ApprovalEngineProxy.updateUser(user_account_registration)
        end
        @apiResponse["verificationToken"]=response["verificationToken"]
       return @apiResponse 
      end
end