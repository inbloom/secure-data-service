#!/usr/bin/env ruby

require 'nokogiri'
require 'logger'

    def process_file(filename)
        #filename = "test/features/ingestion/test_data/BroadSetOfTypes/InterchangeAttendance.xml"
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

        doc.css('InterchangeStudentAttendance').each do

            doc.css('AttendanceEvent').each do |event|
                event.css('SessionReference').each do |sessionRef|
                    sessionRef.remove
                end
            end

            doc.css('AttendanceEventType').each do |aet|
                aet.remove
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

