require 'rubygems'
require 'net/sftp'
require 'net/ssh'

class RemoteLogReducer
  
  def initialize(host, user, password)
    @host = host
    @user = user
    @password = password
  end

  def reduceRemoteLog(remoteLog, reducedLog, marker, pattern)
    Net::SSH.start(@host, @user, :password => @password) do |ssh|
      puts "wc -l #{remoteLog} | awk '{print $1}'"
      totalLines = ssh.exec!("wc -l #{remoteLog} | awk '{print $1}'")
      totalLines.chomp!
      puts "#{remoteLog} has #{totalLines} lines"
  
      puts "grep -n \"#{marker}\" #{remoteLog} | awk -F: '{print $1}' | tail -1"
      ln = ssh.exec!("grep -n \"#{marker}\" #{remoteLog} | awk -F: '{print $1}' | tail -1")
      ln.chomp!
      puts "#{marker} is on line #{ln}"
  
      tailLines = totalLines.to_i - ln.to_i + 1
  
      puts "tail -#{tailLines} #{remoteLog} | grep -P \"#{pattern}\" > #{reducedLog}"
      output = ssh.exec!("tail -#{tailLines} #{remoteLog} | grep -P \"#{pattern}\" > #{reducedLog}")
      puts output if output != nil
    end
  end
  
end

