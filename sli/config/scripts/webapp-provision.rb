#!/usr/bin/env ruby

=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'rubygems'
require 'yaml'
require 'json'

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
	if env_config
		env_config.each do |k,v|
			if v
				v.each do |prop_k, prop_v|
			        result << "#{prop_k} = #{(prop_v.is_a? Hash) ? prop_v.to_json : prop_v}"
			        prop_ids << prop_k.strip
				end
			end
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
	unless ARGV.length == 3
		puts "Usage: " + $0 + " YAML_CONFIG_FILE PROFILE OUTPUT_FILE\n"
		puts
		puts "Generates a property file from a given YAML file that contain configuration information.\n"
		puts
		puts "  YAML_CONFIG_FILE ... YAML configuration file that contains different profiles."
		puts "  PROFILE          ... Specifies the profile to use to determine the properties. Must be defined in the YAML file."
		puts "  OUTPUT_FILE      ... Path to the file where the properties should be stored."
		exit(1)
	end

	input_fname  = ARGV[0]
	env          = ARGV[1]
	output_fname = ARGV[2]

	# read the configuration from spread out config files and write them to a yaml file
	config = get_config(input_fname, env)
	write_prop_file(config, output_fname, env)
end
