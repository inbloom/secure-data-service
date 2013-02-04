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


require 'net/scp'
      
 def scp_upload(address, username, local_path, remote_path, options = {}, upload_options = {})
  key_manager = Net::SSH::Authentication::KeyManager.new(nil, options)

  # auth check
  unless options[:key_data] || options[:keys] || options[:password] || key_manager.agent
    raise ArgumentError.new(':key_data, :keys, :password or a loaded ssh-agent is required to initialize SSH')
  end

  @options  = { :paranoid => false }.merge(options)

  begin
    Net::SCP.start(address, username, @options) do |scp|
      scp.upload!(local_path, remote_path, upload_options) do |ch, name, sent, total|
        percentage = format('%.2f', received.to_f / total.to_f * 100) + '%'
        print "Saving to #{name}: Received #{received} of #{total} bytes" + " (#{percentage})               \r"
        STDOUT.flush
      end
    end
  rescue Exception => error
    raise error
  end
end


