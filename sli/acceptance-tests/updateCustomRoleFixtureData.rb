#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require 'logger'

# To use this script find all directories with student fixture data and put in a file
# find `pwd` -name *customRole_*.json -or -name *customRole.json | xargs -L1 dirname | sort -u > fixture_directories
# run using that file: ruby updateCustomRoleFixtureData.rb fixture_directories

def process_file (filename)

  @log.info "CustomRole file: " + filename
  if File.exists?('updated.json')
    @log.error "updated.json already exists"
    exit 1
  end
  if File.exists?(filename)
    File.readlines(filename).each do |line|
      begin
        json = JSON.parse(line)
      rescue Exception => e
        @log.error "Unable to parse line as json: " + line
        e.backtrace.each { |trace_line| @log.error trace_line }
        next
      end
      body = json["body"]
      roles = body["roles"]

      roles.each do |role|
        if role["groupTitle"] == 'IT Administrator'
          new_rights = Array.new
          new_rights = role["rights"]
          if !new_rights.include?('APP_AUTHORIZE')
            new_rights.push('APP_AUTHORIZE')
            role["rights"] = new_rights
          end
        end
      end
      body["roles"] = roles
      File.open('updated.json', 'a') do |file|
        file.puts(json.to_json)
      end
    end
  end
  FileUtils.mv('updated.json', filename) if File.exist?('updated.json')

end

$stdout.sync = true
@log = Logger.new($stdout)
@log.level = Logger::INFO

if ARGV.length != 1
  @log.error "Usage: updateCustomRoleFixtureData.rb {directory_file}"
  exit 1
end

directory_file = ARGV[0]
@log.info "File containing directories: " + directory_file

  File.open(directory_file).each do |directory|
    # remove newline
    directory = directory[0..-2]
    @log.info "Directory: " + directory
    Dir.chdir(directory) do
      Dir.glob('*customRole{.json,_*.json}').each do |filename|
        process_file(filename)
      end
    end
  end