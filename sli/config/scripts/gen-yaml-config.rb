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


#!/usr/bin/env ruby 

# This is a utility script written while moving the properties out of 
# the packaged war and jar files. 
# It collects all property files in a directory try and generates
# a YAML file that reflects that hierachy. 
#


require 'find'
require 'yaml'
require 'ostruct'

$REMOVE_PROPS = {
    "sli.db.dialect"=>nil,
    "sli.db.driver"=>nil,
    "sli.db.password"=>nil,
    "sli.db.url"=>nil,
    "sli.db.username"=>nil,
    "sli.hibernate.generateDdl"=>nil,
    "sli.hibernate.showsql"=>nil
}

def build_config(root_dir)
    root_dir = root_dir + "/" if root_dir[-1]!="/"
    len_rd = root_dir.length
    unique_options = []
    current = {}
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
               props = read_prop_file(f)
               current[level_1][level_2] += props
               props.each do |opt|
                   if opt.is_a?(OpenStruct)
                       unique_options << [level_2, opt.name]
                   end
               end
           end
    end
    [current, unique_options.uniq!]
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
                name.strip!
                value.strip!
                if !($REMOVE_PROPS.key?(name))
                    result << OpenStruct.new(:name => name, :value => value)
                else
                    puts "Excluding: #{name} = #{value}"
                end
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
    if ARGV.length < 2
        puts "Usage: " + $0 + " config_dir [,config_dir, config_dir] output_yaml_file"
        exit(1)
    end

    config_dir   = ARGV[0]
    output_fname = ARGV[1]

    # read the configuration from spread out config files and write them to a yaml file
    config, unique_options = build_config(config_dir)
    write_config(config, output_fname)

=begin
    unique_options.sort!
    puts "Uniques: \n----------------------------------------------\n"
    unique_options.each do |opt| 
        puts opt 
    end
=end
end
