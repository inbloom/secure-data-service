#!/usr/bin/env ruby

# stdout from WadlViewer
golden_uris = IO.readlines("/Users/chung/Desktop/golden.txt")
generated_uris =  IO.readlines("/Users/chung/Desktop/generated.txt")

golden_map = Hash.new
golden_uris.each do |uri|
  golden_map["#{uri}"] = true
end

generated_unique = []
generated_uris.each do |uri|
  if golden_map.has_key?(uri)
    golden_map.delete(uri)
  else
    generated_unique << uri if !uri.downcase.include?("aggregate")
  end
end

golden_unique = golden_map.keys

puts "Unique to Golden Wadl:"
golden_unique.sort!.each do |uri|
  puts "    #{uri}"
end

puts "\nUnique to Generated Wadl (without aggregates):"
generated_unique.sort!.each do |uri|
  puts "    #{uri}"
end
