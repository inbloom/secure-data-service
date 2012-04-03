require 'active_support/core_ext/object/blank'

class Logger
  def format_message(severity, timestamp, progname, msg)
    
    
    puts     Thread.current[:awesome]
    puts "logger remote: "
     puts  Thread.current[:remote] 
     puts "end logger remote"
    output = { :serverity => severity, 
      :remote_ip => Thread.current[:remote],
      #:user => Thread.current[:env],
 :awesome =>      Thread.current[:awesome],
 :request => Thread.current[:request],
      :thread => Thread.current,
      :session => Thread.current[:session_id] , :timestamp => timestamp, :progname => progname, :msg => msg }
   # puts "Request #{request}"
    #puts "UUID #{uuid}"
     #puts "remote ip #{remote_ip}"
   
    "#{output.to_json} \n"
   # "FORMAT #{timestamp.to_formatted_s(:db)} #{severity} #{msg}\n" 
  end 
end

