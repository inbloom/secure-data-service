require 'json'

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

output = IO.popen("java -jar target/dal-shell-1.0-SNAPSHOT-jar-with-dependencies.jar", "w+") do |pipe|
            json_str = data.to_json
            pipe.puts json_str
            pipe.puts "---"
            pipe.close_write
            pipe.read
        end 

puts "Output:"
puts output
