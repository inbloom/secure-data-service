#!/usr/bin/env ruby 

require 'yaml'

DEFAULT_ENV = "default"

def read_config(fname)
	parsed = begin
    		YAML.load(File.open(fname))
  		rescue ArgumentError => e
    		puts "Could not parse YAML in file #{fname}: #{e.message}"
	end
	parsed
end

def iter_props(config)
    prop_by_env = {}
    prop_by_env.default = []
    config.each do |env, sections| 
        puts "EEE:" + env
        sections.each do |sec, props|
            props.each do |k,v|
                prop_by_env[env] = prop_by_env[env] + [k] 
                yield env, sec, k
            end
        end
    end
    prop_by_env 
end


def check_properties(config)
	# go through all sections and collect the union of properties 
    unique_props = []
    unique_sections = [] 
    props_by_env = iter_props(config) do |env, section, prop_name|
        unique_props << prop_name
        unique_sections << section
    end

    # make all the props unique 
    unique_props.uniq!
    unique_props.sort!
    unique_sections.uniq!
    unique_sections.sort!

    puts "UNIQUE PROPERTIES ----- >"
    unique_props.each do |x| 
        puts x
    end
    puts "-------------------------"

    # iterate through the environements and see if they contain default values 
    puts "Checking all profiles whether they contain properties."
    props_by_env.each do |env, props| 
        if !env.start_with?("test-")
            result_str = "ENV '#{env}' "
            err = [] 
            sorted_props = props.sort
            if sorted_props.uniq.length != sorted_props.length
                us_props = sorted_props.uniq
                dupes = us_props.select {|p| sorted_props.count(p) > 1}
                err << "Properties in environment '#{env}' are not unique. Duplicates:\n    " + dupes.join("\n    ")
            end
            intersect = unique_props - sorted_props 
            if !intersect.empty? 
                err << "Missing properties:\n    " + intersect.join("\n    ")
            end
            if !err.empty? 
                result_str << err.join("\n")
            else 
                result_str << "Success."
            end
            puts result_str
        end
    end
end


if __FILE__==$0
	if ARGV.length < 1
		puts "Usage: " + $0 + " yaml_config "
		exit(1)
	end

	config_fname = ARGV[0]
	config = read_config(config_fname)
	check_properties(config)
=begin
	unique_options.sort!
	puts "Uniques: \n----------------------------------------------\n"
	unique_options.each do |opt| 
		puts opt 
	end
=end
end
