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

require 'nokogiri'
require 'open-uri'

def xsd_for_file ( file )
  interchange = file.to_str.match( /Interchange(.*).xml/m)[1]
  return "SLI-Interchange-#{interchange}.xsd";
end

def validate_file ( file )
  xsd = xsd_for_file ( file )

  doc = Nokogiri.XML( File.open( file ) )

  valid = true
  schemata_by_ns = Hash[ doc.root['schemaLocation'].scan(/(\S+)\s+(\S+)/) ]
  schemata_by_ns.each do |ns,xsd_uri|
    xsd = Nokogiri::XML.Schema(open(xsd_uri))
    xsd.validate(doc).each do |error|
      valid = false
      puts "File: #{file} Line: #{error.line} #{error.message}"
    end
  end
  return valid
end
