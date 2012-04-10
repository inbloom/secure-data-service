#!/usr/bin/env ruby 

require 'find'
require 'yaml'
require 'ostruct'

def build_config(root_dir)
	root_dir = root_dir + "/" if root_dir[-1]!="/"
	len_rd = root_dir.length
	current = {}
	current.default = {}
	Find.find(root_dir) do |f|
		if File.file?(f)
	   		# remove the root path 
	   		rm_prefix = f[len_rd..(f.length)] 
   			rel_path = rm_prefix.split("/")
   			level_1 = rel_path[0]
   			level_2 = rel_path[1]
   			#puts f + "   |    " + rel_path.to_s
   			if not current.key?(level_1)
   				current[level_1]= {}
   			end
   			if not current[level_1].key?(level_2)
   				current[level_1][level_2] = []
   			end
   			current[level_1][level_2] << "# " + rm_prefix
   			current[level_1][level_2] += read_prop_file(f)
   		end
	end
	current
end

def read_prop_file(fname)
	result = [] 
	File.open(fname, "r") do |f| 
		while (line = f.gets)
			line = line.strip 
			if (line == "") || (line[0] == "#")
				result << line
			else
				name, value = line.split("=", 2)
				result << OpenStruct.new(:name => name.strip, :value => value.strip)
			end
		end
	end
	result
end

def write_yaml(node, f, indent)
	if node.is_a?(Hash)
		node.each do |k,v|
			f.write(indent + k + ":\n")
			write_yaml(v, f, indent + "    ")
		end
	elsif node.is_a?(Array)
		#puts "NODE: " + node.to_s
		node.each {|x| write_yaml(x, f, indent)}
	elsif node.is_a?(OpenStruct)
		f.write(indent + "#{node.name}: #{node.value}\n")
	else
		sn = node.strip 
		if sn == ""
			f.write("\n")
		elsif sn[0] == "#"
			f.write(indent + sn + "\n")
		else
			f.write(indent + "- " + sn + "\n")
		end
	end
end

def write_config(config, output_fname)
    File.open(output_fname, "w") do |f|
    	  f.write("---\n")
    	  write_yaml(config, f, "")
    end
    puts "Wrote configuration to #{output_fname}."
end

if __FILE__==$0
	if ARGV.length < 3
		puts "Usage: " + $0 + " yaml_config_file environment outputfile"
		exit(1)
	end

	input_fname  = ARGV[0]
	env          = ARGV[1]
	output_fname = ARGV[2]

	# read the configuration from spread out config files and write them to a yaml file
	config = get_config(input_fname, env)
	write_prop_file(config, output_fname)
end
