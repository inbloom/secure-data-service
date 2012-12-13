=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


###############################################################################
# RUN JMETER TESTS
###############################################################################

require 'xml'

JMETER_PATH = "../../../../../../tools/jmeter/"
PROPERTIES_FILE = "local.properties"

Given /^I run each of the Jmeter tests:$/ do |table|
  puts "starting test"
  table.hashes.map do |row|
  	testName = row["testName"]
    puts "\n" + testName
  	runTest(testName)
  end
end

def runTest(testName)
	jmxFileName = JMETER_PATH + testName + ".jmx" 
  	propertiesFileName = JMETER_PATH + PROPERTIES_FILE
  	jMeterCommand = "jmeter -n -t " + jmxFileName + " -q " + propertiesFileName
  	puts "executing: " + jMeterCommand
  	system jMeterCommand
	parseJtlForRC(testName)
end

def parseJtlForRC(testName)
	rcMap = {}
	fileName = testName + ".jtl"
	doc = loadXML(fileName)
	doc.find('//httpSample').each do |sample|
    	label = sample.attributes["lb"]
    	rc = sample.attributes["rc"]
    	rcMap[label] = rc
    	#sample.attributes.each do |attribute|
    	#	puts attribute.name + " : " + attribute.value
    	#end
    	puts label + " : " + rc
  	end
  	rcMap
end

def loadXML(fileName)
	xml = File.read(fileName)
	parser = XML::Parser.new
	parser.string = xml
	parser.parse
end