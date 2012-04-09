#!/usr/bin/env ruby 

require 'yaml'

def getConfig(fname)
	parsed = begin
    		YAML.load(File.open(fname))
  		rescue ArgumentError => e
    		puts "Could not parse YAML in file #{fname}: #{e.message}"
	end
	parsed
end

if __FILE__ == $0
	config = getConfig(ARGV[0])
	puts config
=begin
	config.each do |k,v| 
		puts k
	end
=end
end
