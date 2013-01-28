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

require 'pp'
require 'rexml/document'

require_relative '../../../utils/sli_utils.rb'

include REXML

###############################################################################
# RUN JMETER TESTS
###############################################################################

JMETER_JMX_PATH = PropLoader.getProps['jmeter_jmx_path']
PROPERTIES_FILE = PropLoader.getProps['jmeter_properties']
JMETER_BIN = PropLoader.getProps['jmeter_bin']
JMETER_JTL_ARCHIVE = PropLoader.getProps['jmeter_jtl_archive']
JMETER_FAILED_JTL_ARCHIVE = PropLoader.getProps['jmeter_failed_jtl_archive']
REGRESSION_THRESHOLD = PropLoader.getProps['jmeter_regression_threshold'].to_f

Before do
  @time = Time.now.strftime("%Y%m%d_%H%M%S")
  @testsRun = Array.new

  makeDirs([JMETER_JTL_ARCHIVE, JMETER_FAILED_JTL_ARCHIVE])
end

def makeDirs(dirs)
  dirs.each do |dir|
    unless File.directory?(dir)
      puts "Creating #{dir}"
      FileUtils.mkdir_p(dir)
    end
  end
end

def archiveJtlFile(file)
	archivedFile = File.join(JMETER_JTL_ARCHIVE, "#{file}.#{@time}")
	puts "Moving #{file} to #{archivedFile}"
	FileUtils.cp(file, archivedFile)
end

def archiveFailedJtlFile(file)
	archivedFile = File.join(JMETER_FAILED_JTL_ARCHIVE, "#{file}.#{@time}")
	puts "Moving #{file} to #{archivedFile}"
	FileUtils.cp(file, archivedFile)
end

Given /^I run each of the Jmeter tests:$/ do |table|
  table.hashes.map do |row|
    testName = row["testName"]
    @testsRun << testName
    puts "\n" + testName
    runTest(testName)
  end
end

def runTest(testName)
  jmxFileName = File.join(JMETER_JMX_PATH, testName + ".jmx")
  propertiesFileName = File.join(JMETER_JMX_PATH, PROPERTIES_FILE)
  jtlFileName = testName + ".jtl"
  jMeterCommand = JMETER_BIN + " -n -t " + jmxFileName + " -q " + propertiesFileName
  puts "executing: " + jMeterCommand
  system jMeterCommand
  puts "Parsing JTL file for #{testName}"
  parseJtlForRC(testName)
end

def parseJtlForRC(testName)
  rcMap = {}
  resultsFile = testName + ".jtl"
  fileName = File.join(JMETER_JMX_PATH, resultsFile))
  puts "Loading #{fileName} xml into doc.."
  doc = loadXML(fileName)
  testPassed = true
  doc.get_elements('//httpSample').each do |sample|
    label = sample.attributes["lb"]
    rc = sample.attributes["rc"]
    #puts label + " : " + rc
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
    puts "Test failed, deleting #{testName}"
    deleteJtlFile(testName)
  end

  rcMap.each do |label, rc|
    assert(validReturnCode?(rc), String(rc) + " returned for " + label)
  end
end

def deleteJtlFile(testName)
	fileName = "#{testName}.jtl"
  	archiveFailedJtlFile(fileName)
  	File.delete(fileName)
end

def loadXML(fileName)
  xml = File.read(fileName)
  Document.new(xml)
end

def validReturnCode?(rc)
  rc.to_i >= 200 && rc.to_i < 400
end

Then /^no performance regressions should be found/ do
  superRegressionMap = {}
  @testsRun.each do |testName|
    regressionsFound = checkForRegression(testName)
    if regressionsFound.empty?
      	archiveJtlFile("#{testName}.jtl")
    else
    	archiveFailedJtlFile("#{testName}.jtl")
    	superRegressionMap[testName] = regressionsFound
    end
  end

  assert(superRegressionMap.size == 0, "Regressions over #{REGRESSION_THRESHOLD} found: #{superRegressionMap.to_s}")
end

Then /^I only check "(.*?)" for performance regression$/ do |lbNames|
  @arrayOfLb = lbNames.split(';')
end

def checkForRegression(testName)
  puts "Checking #{testName} for regression"

  currentJtl = "#{testName}.jtl"
  previousJtl = findPreviousJtl(testName)

  if previousJtl.nil?
    puts "No previous jtl for #{testName}"
    return {}
  end

  currentDoc = loadXML(currentJtl)
  previousDoc = loadXML(previousJtl)

  currentMap = parseJtlForTimings(currentDoc)
  previousMap = parseJtlForTimings(previousDoc)

  currentStats = aggregate(currentMap)
  previousStats = aggregate(previousMap)

  regressions = {}

  currentStats.each_pair do |label, avgCurrentTime|
    avgPreviousTime = previousStats[label]
    if avgPreviousTime.nil?
      puts "No previous timings for #{label}"
      next
    end

    fractionalDifference = calculateFractionalDifference(avgCurrentTime, avgPreviousTime)
    if fractionalDifference >= REGRESSION_THRESHOLD
      puts "#{fractionalDifference} is greater than or equal to #{REGRESSION_THRESHOLD}, adding map for #{label} to #{fractionalDifference.to_s}"
      regressions[label] = fractionalDifference
    end
  end

  return regressions
end

def findPreviousJtl(testName)
  pattern = "#{testName}\\.jtl\\..*"
  previousJtl = nil
  Dir.foreach(JMETER_JTL_ARCHIVE) do |archivedFile|
    archivedFile.match(pattern) do |matchedFile|
      previousJtl = File.join(JMETER_JTL_ARCHIVE, matchedFile.to_s)
    end
  end

  puts "Previous jtl is #{previousJtl}"
  return previousJtl
end

def parseJtlForTimings(doc)
  map = {}
  # could cause issues if a root element isn't httpSample
  doc.root.elements.each do |sample|
    label = sample.attributes["lb"]
    if (@arrayOfLb.nil? || @arrayOfLb.include?(label))
      timings = map[label]
      if timings.nil?
        timings = Array.new
        map[label] = timings
      end
      timings << sample.attributes["t"]
    end
  end

  return map
end

def aggregate(map)
  aggregateMap = {}
  map.each_pair do |label, timings|
    sum = 0
    timings.each do |timing|
      sum = sum + timing.to_f
    end
    avg = sum / timings.count
    aggregateMap[label] = avg
  end

  return aggregateMap
end

def calculateFractionalDifference(avgCurrentTime, avgPreviousTime)
  return (avgCurrentTime - avgPreviousTime) / avgPreviousTime
end
