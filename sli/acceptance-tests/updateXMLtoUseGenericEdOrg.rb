#!/usr/bin/env ruby

require 'nokogiri'
require 'logger'

    def process_file(filename)
        #filename = "test/features/ingestion/test_data/BroadSetOfTypes/InterchangeEducationOrganization.xml"
        infile = File.open(filename)
        begin
            doc = Nokogiri::XML(infile) do |config|
                config.strict.nonet.noblanks
            end
            infile.close
        rescue
            @log.warn "File could not be processed " + filename
            return
        end

        doc.css('InterchangeEducationOrganization').each do

            doc.css('Action').each do |action|
                action.css('SchoolReference').each do |schoolRef|
                    schoolRef.name = "EducationOrganizationReference"
                end
                action.css('LocalEducationAgencyReference').each do |leaRef|
                    leaRef.name = "EducationOrganizationReference"
                end
                action.css('StateEducationAgencyReference').each do |seaRef|
                    seaRef.name = "EducationOrganizationReference"
                end
            end

            doc.css('StateEducationAgencyReference').each do |seaRef|
                seaRef.name = "ParentEducationAgencyReference"
            end
            doc.css('LocalEducationAgencyReference').each do |leaRef|
                leaRef.name = "ParentEducationAgencyReference"
            end
            doc.css('EducationServiceCenterReference').each do |escRef|
                escRef.name = "ParentEducationAgencyReference"
            end

            doc.css('School').each do |school|
                school.name = "EducationOrganization"
                orgCategories = school.at_css "OrganizationCategories"
                school.css('ParentEducationAgencyReference').each do |peaRef|
                    orgCategories.add_next_sibling(peaRef.to_s) unless peaRef.nil?
                    peaRef.remove unless peaRef.nil?
                end
            end
            doc.css('LocalEducationAgency').each do |lea|
                lea.name = "EducationOrganization"
                orgCategories = lea.at_css "OrganizationCategories"
                lea.css('ParentEducationAgencyReference').each do |peaRef|
                    orgCategories.add_next_sibling(peaRef.to_s) unless peaRef.nil?
                    peaRef.remove unless peaRef.nil?
                end
            end
            doc.css('StateEducationAgency').each do |sea|
                sea.name = "EducationOrganization"
                orgCategories = sea.at_css "OrganizationCategories"
                sea.css('ParentEducationAgencyReference').each do |peaRef|
                    orgCategories.add_next_sibling(peaRef.to_s) unless peaRef.nil?
                    peaRef.remove unless peaRef.nil?
                end
            end

            outfile = File.new(filename, "w")
            outfile.puts doc.to_xml
            outfile.close

            @log.info "#{filename} processed successfully"
        end
    end

    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::DEBUG

    Dir.chdir("test/features/ingestion/test_data/") do
        Dir.glob('*/*xml').each do |file_name|
            process_file(file_name)
        end
    end

