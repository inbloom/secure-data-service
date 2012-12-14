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

require_relative '../../../utils/sli_utils.rb'

###############################################################################
# RUN JMETER TESTS
###############################################################################

require 'xml'
JMETER_PATH = PropLoader.getProps['jmeter_path']


PROPERTIES_FILE = "local.properties"

Given /^I run each of the Jmeter tests:$/ do |table|
  table.hashes.map do |row|
      testName = row["testName"]
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
	testPassed = true
	doc.find('//httpSample').each do |sample|
    	label = sample.attributes["lb"]
    	rc = sample.attributes["rc"]
    	truncatedLabel = label
    	optIndex = truncatedLabel.index('?')
    	if optIndex != nil
    		truncatedLabel = truncatedLabel[0, optIndex]
    	end
    	rcMap[truncatedLabel] = rc
    	validRc = validReturnCode?(rc)
    	if !validRc
    		puts truncatedLabel + ": \t " + rc
    		testPassed = false
    	end 
  	end
  	
  	if !testPassed
  		deleteJtlFile(testName)	
  	end
  	
  	rcMap.each do |label, rc|
  		assert(validReturnCode?(rc), String(rc) + " returned for " + label)
  	end
  	
end

def deleteJtlFile(testName)
	File.delete(testName + ".jtl")
end

def loadXML(fileName)
	xml = File.read(fileName)
	parser = XML::Parser.string(xml)
	parser.parse
end

def validReturnCode?(rc)
	rc.to_i >= 200 && rc.to_i < 400
end

#TODO remove and use SLI utils assert
def assert(bool, message = 'assertion failure')
  raise message unless bool
end