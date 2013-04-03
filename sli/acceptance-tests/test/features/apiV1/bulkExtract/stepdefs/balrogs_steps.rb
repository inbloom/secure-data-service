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

Given /^I am a valid 'service' user with an authorized long\-lived token "(.*?)"$/ do |token|
  @sessionId=token
end

When /^I make API call to retrieve sampled bulk extract file$/ do
  restHttpGet("/bulk/extract")
end

When /^I make bulk extract API call$/ do
  restHttpGet("/bulk/extract/tenant")
end

When /^I make API call to retrieve today's delta file$/ do
  today = Time.now
  restHttpGet("/bulk/deltas/#{today.strftime("%Y%m%d")}")
end

When /^I make API call to retrieve tomorrow's non existing delta files$/ do
  tomorrow = Time.now+24*3600
  restHttpGet("/bulk/deltas/#{tomorrow.strftime("%Y%m%d")}")
end

When /^I save the extracted file$/ do
  @filePath = "extract/extract.tar"
  @unpackDir = File.dirname(@filePath) + '/unpack'
  if (!File.exists?("extract"))
      FileUtils.mkdir("extract")
  end
  if (File.exists?(@filePath)) 
      FileUtils.rm(@filePath)
      puts "Removed existing #{@filePath}"
  end
  if (File.exists?(@unpackDir))
      FileUtils.rm_r(@unpackDir)
      puts "Removed existing #{@unpackDir}"
  end

  File.open(@filePath, 'w') {|f| f.write(@res.body) }
end

When /^the return code is 404 I ensure there is no bulkExtractFiles entry for Midgar$/ do
    @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
    @coll = "bulkExtractFiles";
    @src_coll = @db[@coll]

    if @res.code == 404
  		puts "@res.headers: #{@res.headers}"
  		puts "@res.code: #{@res.code}"

	    if @src_coll.count > 0
	    		ref_doc = @src_coll.find({"_id" => "Midgar"}).to_a
    			assert(ref_doc.count == 0, "Return code was: "+@res.code.to_s+" but find #{@coll} document with _id #{"Midgar"}")
	    end
    end
end

When /^the return code is 503 I ensure there is a bulkExtractFiles entry for Midgar$/ do
    if @res.code == 503
  		puts "@res.headers: #{@res.headers}"
  		puts "@res.code: #{@res.code}"

	    if @src_coll.count > 0
	    		ref_doc = @src_coll.find({"_id" => "Midgar"}).to_a
    			assert(ref_doc.count > 0, "Return code was: "+@res.code.to_s+" but find no #{@coll} document with _id #{"Midgar"}")
	    end
    end
end

When /^the return code is 200 I get expected tar downloaded$/ do
	  puts "@res.headers: #{@res.headers}"
	  puts "@res.code: #{@res.code}"
    if @res.code == 200
	  puts "@res.headers: #{@res.headers}"
	  puts "@res.code: #{@res.code}"
	
	  EXPECTED_CONTENT_TYPE = 'application/x-tar'
	  @content_disposition = @res.headers[:content_disposition]
	  @zip_file_name = @content_disposition.split('=')[-1].strip() if @content_disposition.include? '='
	  @last_modified = @res.headers[:last_modified]
      @is_sampled_file = @zip_file_name=="NY-WALTON-2013-03-19T13-02-02.tar"
	
	  puts "content-disposition: #{@content_disposition}"
	  puts "download file name: #{@zip_file_name}"
	  puts "last-modified: #{@last_modified}"
	
	  assert(@res.headers[:content_type]==EXPECTED_CONTENT_TYPE, "Content Type must be #{EXPECTED_CONTENT_TYPE} was #{@res.headers[:content_type]}")
    end
end

Then /^I check the http response headers$/ do  

  if @is_sampled_file
    EXPECTED_BYTE_COUNT = 5632
    assert(@res.headers[:content_length].to_i==EXPECTED_BYTE_COUNT, "File Size is wrong! Actual: #{@res.headers[:content_length]} Expected: #{EXPECTED_BYTE_COUNT}" )
  elsif @res.code == 200
    @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
    coll = "bulkExtractFiles";
    src_coll = @db[coll]
    raise "Could not find #{coll} collection" if src_coll.count == 0

    ref_doc = src_coll.find({"_id" => "Midgar"}).to_a
    raise "Could not find #{coll} document with _id #{"Midgar"}" if ref_doc.count == 0

    puts "bulkExtractFiles record: #{ref_doc}"

    ref_doc.each do |row|
      raise "#{coll} document with wrong tenantId" if row['body']['tenantId'] != "Midgar"

        dateFromMongo = row['body']['date'].to_datetime.to_time.to_s
        if dateFromMongo != nil
          @last_modified = DateTime.parse(@last_modified).to_time.to_s
          assert(@last_modified==dateFromMongo, "last-modified must be #{dateFromMongo} was #{@last_modified}")
        end

        path = row['body']['path']
        file_name = path.split('/')[-1] if path.include? '/'
        if file_name != nil
          assert(@zip_file_name==file_name, "File Name must be #{file_name} was #{@zip_file_name}")
        end
    end
    
  end
end


#=============================================================

Given /^in my list of rights I have BULK_EXTRACT$/ do
  #  Explanatory step
end
