require 'rubygems'
require 'net/ssh'

class RemoteLogMarker

  def initialize(host, user, password)
    @host = host
    @user = user
    @password = password
  end

  def markRemoteLog(remoteLog, marker)
    Net::SSH.start(@host, @user, :password => @password) do |ssh|
      puts "echo #{marker} >> #{remoteLog}"
      output = ssh.exec!("echo #{marker} >> #{remoteLog}")
      puts output if output != nil
    end
  end

end

