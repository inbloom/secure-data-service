require 'rubygems'
require 'net/sftp'
require 'net/ssh'
  
class LogDownloader
  
  def initialize(host, user, password)
    @host = host
    @user = user
    @password = password
  end
  
  def downloadLog(remoteLog, localLog)
    Net::SFTP.start(@host, @user, :password => @password) do |sftp|
      puts "Downloading #{remoteLog} to #{localLog}"
      sftp.download!(remoteLog, localLog)
    end
  end
  
end

