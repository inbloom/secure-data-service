require_relative 'slc_fixer'

if ARGV.count < 1
  puts "Usage: edorg_stamper <dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
else
  hp = ARGV[0].split(":")
  connection = Mongo::Connection.new(hp[0], hp[1].to_i)
  db = connection['sli']
  puts db
  fixer = SLCFixer.new(db)
  fixer.start
end

