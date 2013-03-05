require "mongo"
require 'date'

@WEEK = 604800

@now = Time.now.to_i

def calcWeight(timestamp)
  weight=0
  delta = @now - timestamp
  
  if delta > @WEEK*4
    weight=0.1
  elsif delta > @WEEK*2
    weight=0.5
  else
    weight=1
  end
  return weight;
end

counts = {}

conn = Mongo::Connection.new("jenkins.slidev.org")
db = conn.db("test_job_failures")
failures = db.collection("failure")

# Load feature files
Dir.chdir("../test/features")
Dir.glob(File.join("**","*.feature")) do |filename|
  File.open(filename, "r") do |infile|
      while (line = infile.gets)
        if match=line.match("^Scenario\s?(Outline)?:\s* (.+)$")
          scenarioName = match[2]
          puts "Processing #{scenarioName}"
          count=0
          
          failures.find({:scenario=>scenarioName}).each do |doc|
            count+=calcWeight(doc['timestamp'])
          end
          counts[scenarioName]=count
        end
      end
  end
end

sorted = counts.sort_by {|key,value| -value}

puts
printf("%-165s %s\n", "Scenario", "Weighted Failures")
printf("%-165s %s\n", "---------", "-------------")
sorted.each do |entry|
  printf("%-165s %s\n", entry[0], entry[1].round(2))
end
puts "----------"
puts "ALL DONE"
