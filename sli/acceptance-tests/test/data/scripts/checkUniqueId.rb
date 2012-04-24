#A script that goes through directories of fixture files and verifies that each ID is unique

def checkFile(file)
  fileContents = File.read(file)
  found = fileContents.scan(/"\$binary" *: *"([^"]*)"/)
  found.each do |id|
    if !$ids.member?(id)
      $ids[id] = file
    else
      puts("ERROR: The ID #{id} exists in files #{file} and #{$ids[id]}")
    end
  end 
end

$ids = {}
ARGV.each do |dir|
  Dir.glob(dir + "*.json") do |file|
    checkFile(file)
  end
end

