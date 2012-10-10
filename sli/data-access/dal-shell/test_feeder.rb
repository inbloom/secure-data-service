require 'json'
require 'open3'

data = {
    :x => "value", 
    :y => "othervalue",
    :z => { :a => 1,
            :b => 2, 
            :c => { 
                    :xyz => 99.3,
                    :uvw => -4.3,
                    :abc => 15.49304840
                  }
          }
}

output = Open3.popen2("java -jar target/dal-shell-jar-with-dependencies.jar", "w+") do |stdin, stdout, wait_thr|
            loop do 
                puts "Writing !"
                json_str = data.to_json
                stdin.puts json_str
                stdin.puts "---"
                result = ""
                l = stdout.readline().strip
                while l != "---" do 
                    result << l
                    l = stdout.readline().strip
                end
                puts "GOT\n", JSON.parse(result)
            end 
        end 

puts "Output:"
puts output
