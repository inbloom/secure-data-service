#!/usr/bin/env ruby 

require 'yaml'

def get_config(fname, env)
	parsed = begin
    		YAML.load(File.open(fname))
  		rescue ArgumentError => e
    		puts "Could not parse YAML in file #{fname}: #{e.message}"
	end
	result = parsed[env]
end

def get_properties(env_config)
	prop_ids = [] 
	result = [] 
	env_config.each do |k,v| 
		v.each do |prop_k, prop_v| 
	        result << "#{prop_k} = #{prop_v}"
	        prop_ids << prop_k.strip 
		end
	end

	# do a sanity check to make sure that there are not duplicates 	
	uniques = {} 
	prop_ids.each do |k|
		if uniques.key?(k)
			raise KeyError, "Duplicate entry:#{k}"
		end
		uniques[k] = nil
	end

	result
end

def write_prop_file(config, output_fname, env)
	lines = ["# Generated properties file do not edit directly\n# Env used: #{env}"] + get_properties(config)
	File.open(output_fname, "w") do |f|
		f.puts(lines)
	end
end

if __FILE__ == $0
	if ARGV.length < 3
		puts "Usage: " + $0 + " yaml_config_file environment outputfile"
		exit(1)
	end

	input_fname  = ARGV[0]
	env          = ARGV[1]
	output_fname = ARGV[2]

	# read the configuration from spread out config files and write them to a yaml file
	config = get_config(input_fname, env)
	write_prop_file(config, output_fname, env)
end
