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


