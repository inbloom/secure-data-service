require 'yaml'

config = YAML.load_file("#{File.dirname(__FILE__)}/../config.yml")
odin_dir = "#{File.dirname(__FILE__)}/.."
gen_dir = "#{File.dirname(__FILE__)}/../generated"

GIGA_SIZE = 1073741824.0
MEGA_SIZE = 1048576.0
KILO_SIZE = 1024.0
# Return the file size with a readable style.
def readable_file_size(size)
  size = size*KILO_SIZE
  if size == 1
    return 1, "Byte" 
  elsif size < KILO_SIZE
    return size, "Bytes" 
  elsif size < MEGA_SIZE
    return (size / KILO_SIZE), "KB" 
  elsif size < GIGA_SIZE
    return (size / MEGA_SIZE), "MB"  
  else
    return (size / GIGA_SIZE), "GB"
  end
end

print "###############################\n"
puts "WARNING: This operation will delete ALL data in the tools/odin/generated directory.. "
while 1
  print "  Are you sure you want to continue? (y/n) "
  response = gets
  exit if response.match(/^n/i)
  exit if response.match(/^exit/i)
  if !response.match(/^y/i)
    puts "Wha? Try again slick.. yes or no. Type exit if you scurred."
  else
    break
  end
end
puts "Currently in #{Dir.pwd}"
Dir["generated/*"].each do |f|
  puts "Removing #{f}"
  `rm -f #{f}`
end
print " OK\n"

puts "GENERATING #{config['scenario']}"

# run the odin generator, capture output
raw_out = `bundle exec rake generate`.match(/Total generation time: (.*?) secs/)
# match the final time to generate data
total_time = $1.to_f
# remove the generated/*.zip files
`rm generated/*.zip`
# measure the size of the generated directory, analyze throughput
`du -ks generated`.match(/^([0-9]+)\s+generated/i)
total_size, units = readable_file_size($1.to_i)
print "###############################\n"
puts "# FINISHED ANALYZING THROUGHPUT"
print "###############################\n" 
print "Total generated data size is: #{total_size} #{units}\n"
print "Time to generate is #{total_time} seconds\n\n"
print "Final Odin throughput for #{config['scenario']} is %.3f #{units}/sec\n\n" % (total_size / total_time)

