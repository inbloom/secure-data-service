require_relative 'slc_fixer'

if ARGV.count < 1
  puts "Usage: teacher_stamper <dbhost:port> <database> <terminates>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "\t terminates - if there is any parameter for this, it will run forever."
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
else
  terminates = (ARGV[2].nil? ? false : true)
  database = (ARGV[1].nil? ? 'sli' : ARGV[1])
  hp = ARGV[0].split(":")
  connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 5, :pool_timeout => 5, :safe => {:wtimeout => 500})
  log = Logger.new(STDOUT)
  log.level = Logger::WARN
  db = connection[database]
  fixer = SLCFixer.new(db, log)
  if terminates
    while true
      begin
        fixer.start
      rescue Exception => e  
        log.error "#{e}"
      end
    end
  else
    fixer.start
  end
end

