=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


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

