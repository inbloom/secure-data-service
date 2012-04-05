require 'active_support/core_ext/object/blank'

# An extension of logger, trying to get access to more thread local variables.
# We're currently using the logging Gem, so this is here for historic reason. :)
class Logger
  def format_message(severity, timestamp, progname, msg)
    
    
   
    output = { :serverity => severity, 
      :remote_ip => Thread.current[:remote],
      :user => Thread.current[:env],
      :session => Thread.current[:session] , 
      :timestamp => timestamp, :progname => progname, :msg => msg }
   
   
    "#{output.to_json} \n"
   end 
end

