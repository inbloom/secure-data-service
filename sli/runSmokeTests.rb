#!/usr/bin/env ruby

require 'fileutils'
require 'socket'
require 'timeout'

def is_port_open_orig?(port)
  begin
    Timeout::timeout(2) {
      begin
        puts 'starting'
        s = TCPServer.new(port)
        s.close
        puts 'stopped'
        return true
      rescue Exception => e #Errno::ECONNREFUSED, Errno::EHOSTUNREACH
        return false
      end
    }
  rescue Timeout::Error
    return false
  end
end

def is_port_open?(host,port)
  begin
    t = TCPSocket.new(host,port)
    t.close
    return false
  rescue Exception => e
    @exception = e
    return true
  end
end

def wait_until_found(filepath,pattern)
  print "\n  wait_until_found: pattern '#{pattern}' in file '#{filepath}' ."
  $stdout.flush

  begin
    Timeout::timeout(30) {
      while true do
        text = File.read filepath
        return true if text =~ pattern
        print '.'
        $stdout.flush
        sleep 1
      end
    }
  rescue Timeout::Error
  end

  puts '\nTimeout Error, pattern not found!'
  false
end


dir = Dir.pwd
puts "PWD: #{dir}"
log_dir = "#{dir}/everyLog"
Dir.mkdir(log_dir) unless File.exists?(log_dir)

jetty_pattern=/Starting scanner at interval of 5 seconds/

procs = [
  {name: 'API', port: 8080, dir: "#{dir}/api", exec: "mvn -o jetty:run", pattern: jetty_pattern},
  {name: 'Ingestion', port: 8000, dir: "#{dir}/ingestion/ingestion-service", exec: "mvn -o jetty:run", pattern: jetty_pattern},
  {name: 'Dashboard', port: 8888, dir: "#{dir}/dashboard", exec: "mvn -o jetty:run", pattern: jetty_pattern},
  {name: 'SimpleIDP', port: 8082, dir: "#{dir}/simple-idp", exec: "mvn -o jetty:run", pattern: jetty_pattern},
  {name: 'Databrowser', port: 3000, dir: "#{dir}/databrowser", exec: "bundle exec rails server", pattern: />> Listening on/},
  {name: 'AdminTools', port: 3001, dir: "#{dir}/admin-tools/admin-rails", exec: "bundle exec rails server", pattern: /WEBrick::HTTPServer#start:/}
]

procs.each { |p|
  if is_port_open?('localhost', p[:port])
    print "Starting #{p[:name]} on port #{p[:port]}"
    $stdout.flush
    Dir.chdir p[:dir]
    `#{p[:exec]} > #{log_dir}/#{p[:name]}Console.log 2>&1 &`
    p[:pid] = $?.pid
    puts ": pid = #{$?.pid}"
  else
    puts "Not Starting #{p[:name]}: port #{p[:port]} is already in use"
  end
}

procs.each { |p|
  wait_until_found("#{log_dir}/#{p[:name]}Console.log", p[:pattern]) if p.has_key? :pid
}

puts "\n\nStarting Smoke Tests\n"
Dir.chdir "#{dir}/acceptance-tests"
exec 'bundle exec rake smokeTests'

