require 'yaml'

config = YAML.load_file("#{File.dirname(__FILE__)}/../config.yml")
odin_dir = "#{File.dirname(__FILE__)}/.."
gen_dir = "#{File.dirname(__FILE__)}/../generated"

print "###############################\n"
puts "# GENERATING DATA"
print "###############################\n"

# run the odin generator, capture output
raw_out = `bundle exec rake generate`.match(/Total generation time: (.*?) secs/)
# match the final time to generate data
total_time = $1.to_f
# remove the generated/*.zip files
`rm generated/*.zip`
# measure the size of the generated directory, analyze throughput
`du -h #{gen_dir}`.match(/([0-9]+)([a-z])/i) do |retval|
  print "###############################\n"
  puts "# FINISHED ANALYZING THROUGHPUT"
  print "###############################\n" 
  print "Raw generated data size is: #{retval.to_s.strip}\n"
  print "Time to generate is #{total_time} seconds\n\n"
  print "Final Odin throughput for #{config['scenario']} is #{$1.to_i / total_time} #{$2}B/sec\n\n"
end
