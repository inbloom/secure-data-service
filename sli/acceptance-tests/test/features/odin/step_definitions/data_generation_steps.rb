require 'mongo'
require 'nokogiri'
require 'logger'
require 'fileutils'
require_relative '../../utils/sli_utils.rb'

def generate(scenario="10students_optional")
  # clear the generate dir
  `rm -rf #{@gen_path}*` if @gen_path && !@gen_path.empty? && @gen_path != '/'

  # run bundle exec rake in the Odin directory
  FileUtils.cd @odin_working_path
  t1 = Time.now
  `bundle exec ruby driver.rb --normalgc #{scenario}`

  # Get the list of generated files for verification (excluding hidden files)
  @files = Dir.entries(@gen_path).reject{|f| ['.','..'].include?(f)}.sort

  FileUtils.cd @at_working_path
end

Before do
  @at_working_path = Dir.pwd
  @odin_working_path = "#{File.dirname(__FILE__)}" + "/../../../../../../tools/odin/"
  @gen_path = "#{@odin_working_path}generated/"
end

# TODO: Only called by bulk extract -- move it there (or eliminate)
Given /^I am using the odin working directory$/ do
  @odin_working_path = "#{File.dirname(__FILE__)}" + "/../../../../../../tools/odin/"
end

When /^I generate a typical data set$/ do
  generate
end

When /^I generate the "(.*)" data set$/ do |scenario|
  generate scenario
end

When /^I generate the bulk extract data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  generate("api_2lea_testing")
end

When /^I zip generated data under filename (.*?) to the new (.*?) directory$/ do |zip_file, new_dir|
  @zip_path = "#{@gen_path}#{new_dir}/"
  FileUtils.mkdir_p(@zip_path)
  FileUtils.chmod(0777, @zip_path)
  runShellCommand("zip -j #{@zip_path}#{zip_file} #{@gen_path}*.xml #{@gen_path}*.ctl")
end

When /^I copy generated data to the new (.*?) directory$/ do |new_dir|
  @zip_path = "#{@gen_path}#{new_dir}"
  Dir["#{@gen_path}*.xml"].each do |f|
    FileUtils.cp(f, @zip_path)
    puts "Copied file #{f} to #{@zip_path}"
  end
  Dir["#{@gen_path}*.ctl"].each {|f| FileUtils.cp(f, @zip_path)}
end

# TODO this should be removed once Odin supports hybrid edorgs and multiple parents natively
When /^I convert school "(.*?)" to a charter school in "(.*?)" with additional parent refs$/ do |schoolId, filename, table|
  edorg_xml_file = "#{@gen_path}#{filename}"
  school_to_charterSchool_with_parents(schoolId, edorg_xml_file, table)
end

# TODO this should be removed once Odin supports hybrid edorgs and multiple parents natively
When /^I convert edorg "(.*?)" to an Education Service Center in "(.*?)"$/ do |edorgId, filename|
  edorg_xml_file = "#{@gen_path}#{filename}"
  edorg_to_esc(edorgId, edorg_xml_file)
end

# TODO this should be removed once Odin supports hybrid edorgs and multiple parents natively
When /^I update edOrg "(.*?)" in "(.*?)" with updated parent refs$/ do |schoolId, filename, table|
  edorg_xml_file = "#{@gen_path}#{filename}"
  update_parents(schoolId, edorg_xml_file, table)
end

When /^I update edOrg "(.*?)" in "(.*?)" with additional parent refs$/ do |schoolId, filename, table|
  edorg_xml_file = "#{@gen_path}#{filename}"
  update_parents(schoolId, edorg_xml_file, table)
end

# TODO this should be removed once Odin supports hybrid edorgs and multiple parents natively
When /^I convert edorg "(.*?)" to a "(.*?)" in "(.*?)"$/ do |edorgId, orgCategory, filename|
  edorg_xml_file = "#{@gen_path}#{filename}"
  edorg_to_orgCategory(edorgId, edorg_xml_file, orgCategory)
end

When /^I convert edorg "(.*?)" to add "(.*?)" in "(.*?)"$/ do |edorgId, orgCategory, filename|
  edorg_xml_file = "#{@gen_path}#{filename}"
  edorg_to_add_orgCategory(edorgId, edorg_xml_file, orgCategory)
end

############################################################
# STEPS: THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
############################################################
Then /^I should see generated file <File>$/ do |table|
  generated_files = Dir.entries(@gen_path)
  table.hashes.each do |f|
    raise "Did not find expected file #{f}" unless generated_files.find(f)
  end
end

Then /^I should see the generated files?:$/ do |table|
  @files.should == table.raw.flatten.sort
end

Given /^the edfi manifest that was generated in the '([^\']*)' directory$/ do |dir|
  @manifest = "#{@odin_working_path}#{dir}/manifest.json"
end

Given /^the tenant is '([^\']*)'$/ do |tenant_id|
  @tenant_id = tenant_id
end

Then /^the sli\-verify script completes successfully$/ do
  disable_NOTABLESCAN
  results = `bundle exec ruby #{@odin_working_path}sli-verify.rb #{@tenant_id} #{@manifest}`
  assert(results.include?("All expected entities found\n"), "verification script failed, results are #{results}")
  enable_NOTABLESCAN
end

def ingest_odin(scenario)
  step "I am using preconfigured Ingestion Landing Zone for \"Midgar-Daybreak\""
  @gen_path = "#{@odin_working_path}generated/"
  generate(scenario)
  steps %Q{
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
    When I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
    And I copy generated data to the new OdinSampleDataSet directory
    Given I am using odin data store
    And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OdinSampleDataSet.zip" is completed in database
  }
end

def runtime(t1, t2)
  t = (t2 - t1).to_s
  t_sec, t_dec = t.split(".")
  puts "Data generation took approximately: " + t_sec + "." + t_dec[0..-5] + " seconds to complete."
end

def school_to_charterSchool_with_parents(schoolId, filename, parents)
    STDOUT.puts "Converting school #{schoolId} to be a charter school in file #{filename} with additional parents"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do

        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(schoolId)
                # add LEA as an org category
                orgCategories = edorg.at_css "OrganizationCategories"
                leaCategory = Nokogiri::XML::Node.new "OrganizationCategory", doc
                leaCategory.content = "Local Education Agency"
                STDOUT.puts "Adding org category : " + leaCategory.content
                orgCategories.add_child(leaCategory) unless orgCategories.nil?
                escCategory = Nokogiri::XML::Node.new "OrganizationCategory", doc
                escCategory.content = "Education Service Center"
                STDOUT.puts "Adding org category : " + escCategory.content
                orgCategories.add_child(escCategory) unless orgCategories.nil?

                # add parent ref to sea
                peaRef = edorg.at_css("ParentEducationAgencyReference")
                parents.hashes.map do |row|
                    parentRefId = row["ParentReference"]
                    STDOUT.puts "Adding parent ref to " + parentRefId
                    parentRef = peaRef.clone
                    parentRef.at_css("StateOrganizationId").content = parentRefId
                    peaRef.before(parentRef) unless peaRef.nil?
                end
            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

def edorg_to_esc(edorgId, filename)
    STDOUT.puts "Converting lea #{edorgId} to be a Education Service Center in file #{filename}"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do

        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(edorgId)
                # change org category to Education Service Center
                orgCategory = edorg.at_css "OrganizationCategory"
                STDOUT.puts "Found org category : " + orgCategory.to_s
                orgCategory.content = "Education Service Center"
                STDOUT.puts "Changed org category to " + orgCategory.to_s

            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

def add_parents(schoolId, filename, parents)
    STDOUT.puts "Converting school #{schoolId} to be a charter school in file #{filename} with additional parents"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do

        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(schoolId)
                # add LEA as an org category
                orgCategories = edorg.at_css "OrganizationCategories"
                leaCategory = Nokogiri::XML::Node.new "OrganizationCategory", doc
                leaCategory.content = "Local Education Agency"
                STDOUT.puts "Adding org category : " + leaCategory.content
                orgCategories.add_child(leaCategory) unless orgCategories.nil?
                escCategory = Nokogiri::XML::Node.new "OrganizationCategory", doc
                escCategory.content = "Education Service Center"
                STDOUT.puts "Adding org category : " + escCategory.content
                orgCategories.add_child(escCategory) unless orgCategories.nil?

                # add parent ref to sea
                peaRef = edorg.at_css("ParentEducationAgencyReference")
                parents.hashes.map do |row|
                    parentRefId = row["ParentReference"]
                    STDOUT.puts "Adding parent ref to " + parentRefId
                    parentRef = peaRef.clone
                    parentRef.at_css("StateOrganizationId").content = parentRefId
                    peaRef.before(parentRef) unless peaRef.nil?
                end
            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

def update_parents(schoolId, filename, parents)
    STDOUT.puts "Converting school #{schoolId} to be a charter school in file #{filename} with new parents"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do
        #TODO - below only works for one edOrg, needs to be updated for multiples
        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(schoolId)
                # replace parent ref to sea
                peaRef = edorg.at_css("ParentEducationAgencyReference")
                parents.hashes.map do |row|
                    parentRefId = row["ParentReference"]
                    STDOUT.puts "Updating parent ref to " + parentRefId
                    peaRef.at_css("StateOrganizationId").content = parentRefId
                end
            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

def edorg_to_orgCategory(edorgId, filename, orgCat)
    STDOUT.puts "Converting lea #{edorgId} to be a #{orgCat} in file #{filename}"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do

        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(edorgId)
                orgCategory = edorg.at_css "OrganizationCategory"
                STDOUT.puts "Found org category : " + orgCategory.to_s
                orgCategory.content = orgCat.to_s
                STDOUT.puts "Changed org category to " + orgCategory.to_s
            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

def edorg_to_add_orgCategory(edorgId, filename, orgCat)
    STDOUT.puts "Converting lea #{edorgId} to be a #{orgCat} in file #{filename}"
    infile = File.open(filename)
    begin
        doc = Nokogiri::XML(infile) do |config|
            config.strict.nonet.noblanks
        end
        infile.close
    rescue
        STDOUT.puts "File could not be processed " + filename
        return
    end

    doc.css('InterchangeEducationOrganization').each do

        doc.css('EducationOrganization').each do |edorg|
            stateId = edorg.at_css "StateOrganizationId"
            if stateId.content.eql?(edorgId)
                orgCategory = edorg.at_css "OrganizationCategory"
                STDOUT.puts "Found org category : " + orgCategory.to_s
                newOrgCategory = orgCategory.clone
                newOrgCategory.content = orgCat.to_s
                orgCategory.before(newOrgCategory) unless orgCategory.nil?
                STDOUT.puts "Added org category " + orgCategory.to_s
            end
        end

        outfile = File.new(filename, "w")
        outfile.puts doc.to_xml
        outfile.close

        STDOUT.puts "#{filename} processed successfully"
    end
end

