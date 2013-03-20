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

When /^I make bulk extract API call$/ do
  restHttpGet("/bulk/extract")
end

Then /^I get expected zip downloaded$/ do  

  EXPECTED_CONTENT_TYPE = 'application/zip'
  @content_disposition = @res.headers[:content_disposition]
  @zip_file_name = @content_disposition.split('=')[-1].strip() if @content_disposition.include? '='
  @is_sampled_file = @zip_file_name=="NY-WALTON-2013-03-08.zip"
  @last_modified = @res.headers[:last_modified]

  assert(@res.headers[:content_type]==EXPECTED_CONTENT_TYPE, "Content Type must be #{EXPECTED_CONTENT_TYPE} was #{@res.headers[:content_type]}")
end


Then /^I check the http response headers$/ do  
  
  EXPECTED_BYTE_COUNT = 901

  if @is_sampled_file
    assert(@res.headers[:content_length].to_i==EXPECTED_BYTE_COUNT, "File Size is wrong! Actual: #{@res.headers[:content_length]} Expected: #{EXPECTED_BYTE_COUNT}" )
  else
    @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
    coll = "bulkExtractFiles";
    src_coll = @db[coll]
    raise "Could not find #{coll} collection" if src_coll.count == 0

    src_coll.find().each do |row|
      if row['body']['tenantId'] == "Midgar"
        date = row['body']['date'].to_datetime.to_s
        if date != nil
      printf("\n%s vs %s\n",date,@last_modified);
#          assert(@last_modified==date, "Content Type must be #{EXPECTED_CONTENT_TYPE} was #{@res.headers[:content_type]}")
        end

        path = row['body']['path']
        file_name = path.split('/')[-1] if path.include? '/'
        if file_name != nil
          assert(@zip_file_name==file_name, "File Name must be #{file_name} was #{@zip_file_name}")
        end
      end
    end


#    src_id = @ref_doc[coll]
  end
end


#=============================================================

Given /^in my list of rights I have BULK_EXTRACT$/ do
  #  Explanatory step
end
