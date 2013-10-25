#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require 'logger'

# To use this script find all directories with student fixture data and put in a file
# find `pwd` -name *applicationAuthorization_*.json -or -name *applicationAuthorization.json | xargs -L1 dirname | sort -u > fixture_directories
# run using that file: ruby updateApplicationAuthorizationFixtureData.rb fixture_directories

def process_file (filename)

  @log.info "ApplicationAuthorization file: " + filename
  if File.exists?('updated.json')
    @log.error "updated.json already exists"
    exit 1
  end
  if File.exists?(filename)
    File.readlines(filename).each do |line|
      begin
        aa_json = JSON.parse(line)
      rescue Exception => e
        @log.error "Unable to parse line as json: " + line
        e.backtrace.each { |trace_line| @log.error trace_line }
        next
      end
      body = aa_json["body"]
      edorgs = body["edorgs"]
      new_edorgs = Array.new
      new_hash = Hash.new
      edorgs.each do |edorg|
        new_hash["authorizedEdorg"] = edorg
        new_edorgs.insert(-1, new_hash)
      end
      body["edorgs"] = new_edorgs
      File.open('updated.json', 'a') do |file|
        file.puts(aa_json.to_json)
      end
    end
  end
  FileUtils.mv('updated.json', filename) if File.exist?('updated.json')

end

$stdout.sync = true
@log = Logger.new($stdout)
@log.level = Logger::INFO

if ARGV.length != 1
  @log.error "Usage: updateApplicationAuthorizationFixtureData.rb {directory_file}"
  exit 1
end

directory_file = ARGV[0]
@log.info "File containing directories: " + directory_file

  File.open(directory_file).each do |directory|
    # remove newline
    directory = directory[0..-2]
    @log.info "Directory: " + directory
    Dir.chdir(directory) do
      Dir.glob('*applicationAuthorization{.json,_*.json}').each do |filename|
        process_file(filename)
      end
    end
  end