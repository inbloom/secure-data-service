require 'json'

exclusions = ['/system/session','/system/support', '/bulk']

file = File.open '../../api/src/main/resources/wadl/resources.json'
str = file.read

json = JSON.parse str

json[0]["resources"].each do |resource|
  path = resource['path']
  next if exclusions.include? path
  puts path
  next if resource["subResources"]==nil
  resource["subResources"].each do |sub|
    puts "#{path}#{sub['path']}"
  end
end

puts
puts 'ALL DONE'