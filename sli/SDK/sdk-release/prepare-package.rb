#!/usr/bin/env ruby

require 'fileutils'
require 'pathname'

if ARGV.length != 1
  puts "Usage:  ruby prepare-release.rb sli/SDK"
  Process.exit
end

sdk = ARGV[0]
output = "sdk-release"

def delete(dir)
  begin
    FileUtils.rm_r(dir)
  rescue
  end
end

delete(output)
FileUtils.mkdir_p(output)

# prepare directory
depend_sli = output + "/lib/sli"
depend_ext = output + "/lib/external"
delete(depend_sli)
delete(depend_ext)
delete(output + "/repo/org/slc/sli/common/sli-common/1.0-SNAPSHOT/")
FileUtils.mkdir_p(depend_sli)
#FileUtils.mkdir_p(depend_ext)

# copy over dependencies
sli = ["client-api-1.0-SNAPSHOT.jarf"]

lib = sdk + "/sample/target/sample/WEB-INF/lib"
d = Dir.new(lib)
d.each do |file|
  if file == "." or file == ".."
    next
  elsif sli.include? file
    FileUtils.cp("#{lib}/#{file}", "#{depend_sli}/")
    if file == "sli-common-1.0-SNAPSHOT.jar"
      FileUtils.cp("#{lib}/#{file}", output + "/repo/org/slc/sli/common/sli-common/1.0-SNAPSHOT/")
    end
  else
    # comment me out if we don't want to copy external dependencies
#    FileUtils.cp("#{lib}/#{file}", "#{depend_ext}/")
  end
end

# copy over src
delete(output + "/client/")
FileUtils.mkdir_p(output + "/client/src")
FileUtils.cp_r(sdk + "/client/src", output + "/client/")
delete(output + "/sample/")
FileUtils.mkdir_p(output + "/sample/src")
FileUtils.cp_r(sdk + "/sample/src", output + "/sample/")

# copy pom files
delete(output + "/pom.xml")
delete(output + "/client/pom.xml")
delete(output + "/sample/pom.xml")
FileUtils.cp("pom-root.xml", output + "/pom.xml")
FileUtils.cp("pom-client.xml", output + "/client/pom.xml")
FileUtils.cp("pom-sample.xml", output + "/sample/pom.xml")

# copy sample.properties
FileUtils.cp("example-sample.properties", output + "/sample/sample.properties")

# write readme
File.open(output + "/README", "w") do |f|
  f.puts <<-EOS
This package contains the SLI Java client library and sample app.  Please see the SLI Java client library documentation for more info.

#{Time.new.inspect}
EOS
end
