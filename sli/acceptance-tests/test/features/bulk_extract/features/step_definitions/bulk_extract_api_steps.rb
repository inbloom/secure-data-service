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
require 'open3'
require 'digest/sha1'
require_relative '../../../utils/sli_utils.rb'

$TAR_FILE_NAME = "Final.tar"

Given /^I am a valid 'service' user with an authorized long\-lived token "(.*?)"$/ do |token|
  @sessionId=token
end

When /^I make a call to the bulk extract end point "([^"]*)"$/ do |url|
  restTls(url)
end

When /^I make a call to the bulk extract end point "(.*?)" using the certificate for app "(.*?)"$/ do |url, app|
  restTls(url, nil, @format, @sessionId, app)
end

When /^I make a call retrieve the header for the bulk extract end point "(.*?)"$/ do |url|
  restHttpHead(url)
end

Given /^in my list of rights I have BULK_EXTRACT$/ do
  #  Explanatory step
end

When /^I make lea bulk extract API call for lea "(.*?)"$/ do |arg1|
  restTls("/bulk/extract/#{arg1}")
end

When /^I make a custom bulk extract API call$/ do
  restHttpCustomHeadersGet("/bulk/extract/tenant", @customHeaders)
end

When /^I make a concurrent ranged bulk extract API call and store the results$/ do
  t1=Thread.new{apiCall1()}
  t2=Thread.new{apiCall2()}
  t1.join
  t2.join

  @received_file = Dir.pwd + "/" + $TAR_FILE_NAME
  File.open(@received_file, "wb") do |outf|
    outf << @res2.body
    outf << @res1.body
  end
end

When /^I delete the tar file I'm writing to if it exists$/ do
  if File.exists?(Dir.pwd + "/" + $TAR_FILE_NAME)
    File.delete( Dir.pwd + "/" + $TAR_FILE_NAME)
  end
end

def apiCall1()
  @customHeaders = makeCustomHeader("20001-")
  @res1 = restHttpCustomHeadersGet("/bulk/extract/tenant", @customHeaders)
end

def apiCall2()
  @customHeaders = makeCustomHeader("0-20000")
  @res2 = restHttpCustomHeadersGet("/bulk/extract/tenant", @customHeaders)
end

When /^I make API call to retrieve today's delta file$/ do
  today = Time.now
  step "I make a call to the bulk extract end point \"/bulk/deltas/#{today.strftime("%Y%m%d")}\""
end

When /^I make API call to retrieve tomorrow's non existing delta files$/ do
  tomorrow = Time.now+24*3600
  step "I make a call to the bulk extract end point \"/bulk/deltas/#{tomorrow.strftime("%Y%m%d")}\""
end

When /^I prepare the custom headers for byte range from "(.*?)" to "(.*?)"$/ do |from, to|
  if  (to == "end")
    to = ""
  end
  range = from + "-" + to
  @customHeaders = makeCustomHeader(range)
end

When /^I prepare the custom headers for multiple byte ranges "(.*?)"$/ do |ranges|
  @customHeaders = makeCustomHeader(ranges)
end

When /^I prepare the custom headers for the first "(.*?)" bytes$/ do |number_of_bytes|
  to = (number_of_bytes.to_i) -1
  @customHeaders = makeCustomHeader("0-" + to.to_s)
end

When /^I prepare the custom headers for the last "(.*?)" bytes$/ do |number_of_bytes|
  from = (@content_length.to_i - number_of_bytes.to_i)
  range = from.to_s + "-#{@content_length}"
  @customHeaders = makeCustomHeader(range)
end

When /^I prepare the custom headers with incorrect etag$/ do
  @customHeaders = makeCustomHeader("0-10", "xyz")
end

When /^the If-Match header field is set to "(.*?)"$/ do |value|
  if value == "FILENAME"
    @customHeaders = {:if_match => "\"#{@etag}\""}
  elsif value == "INCORRECT_FILENAME"
    @customHeaders = {:if_match => "\"#{value}\""}
  else
    assert(false, "Unsupported value")
  end
 end

 When /^the If-Unmodified-Since header field is set to "(.*?)"$/ do |value|
  date = Date.parse(@last_modified)
  if value == "BEFORE"
    @customHeaders = {:if_unmodified_since => "#{date.prev_day.httpdate})"}
  elsif value == "AFTER"
    @customHeaders = {:if_unmodified_since => "#{date.next_day.httpdate}"}
  else 
    assert(false, "Unsupported value")
  end
 end

  When /^the If-Range header field is set to "(.*?)" for range up to "(.*?)"$/ do |value, range|
  date = Date.parse(@last_modified)
  to = (range.to_i) -1
  if value == "VALID_DATE"
    @customHeaders = makeCustomHeader("0-" + to.to_s, date.next_day.httpdate)
  elsif value == "INVALID_DATE"
    @customHeaders = makeCustomHeader("0-" + to.to_s, date.prev_day.httpdate)
  else 
    assert(false, "Unsupported value")
  end
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

When /^I decrypt and save the extracted file$/ do
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

  step "the response is decrypted"
  File.open(@filePath, 'w') {|f| f.write(@plain) }
end


When /^the return code is 404 I ensure there is no bulkExtractFiles entry for Midgar$/ do
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  @coll = "bulkExtractFiles";
  @src_coll = @db[@coll]

  puts "@res.headers: #{@res.headers}"
  puts "@res.code: #{@res.code}"
  assert(@res.code == 404,"The return code is #{@res.code}. Expected: 404")

  if @src_coll.count > 0
    ref_doc = @src_coll.find({"_id" => "Midgar"}).to_a
    assert(ref_doc.count == 0, "Return code was: "+@res.code.to_s+" but find #{@coll} document with _id #{"Midgar"}")
  end
end

When /^the return code is 503 I ensure there is a bulkExtractFiles entry for Midgar$/ do
  puts "@res.headers: #{@res.headers}"
  puts "@res.code: #{@res.code}"
  assert(@res.code == 503,"The return code is #{@res.code}. Expected: 503")


  if @src_coll.count > 0
    ref_doc = @src_coll.find({"_id" => "Midgar"}).to_a
    assert(ref_doc.count > 0, "Return code was: "+@res.code.to_s+" but find no #{@coll} document with _id #{"Midgar"}")
  end
end

When /^the return code is 200 I get expected tar downloaded$/ do
	puts "@res.headers: #{@res.headers}"
	puts "@res.code: #{@res.code}"
  assert(@res.code == 200,"The return code is #{@res.code}. Expected: 200")
	
	EXPECTED_CONTENT_TYPE = 'application/x-tar'
	@content_disposition = @res.headers[:content_disposition]
	@zip_file_name = @content_disposition.split('=')[-1].strip() if @content_disposition.include? '='
	@last_modified = @res.headers[:last_modified]
	
	puts "content-disposition: #{@content_disposition}"
	puts "download file name: #{@zip_file_name}"
	puts "last-modified: #{@last_modified}"
	
	assert(@res.headers[:content_type]==EXPECTED_CONTENT_TYPE, "Content Type must be #{EXPECTED_CONTENT_TYPE} was #{@res.headers[:content_type]}")
end

Then /^I get back a response code of "(.*?)"$/ do |response_code|
  puts "@res.headers: #{@res.headers}"
  puts "@res.code: #{@res.code}"
  assert(@res.code.to_i == response_code.to_i, "The return code is #{@res.code}. Expected: #{response_code}")
end

Then /^the content length in response header is "(.*?)"$/ do |length|
  content_length = @res.headers[:content_length]
  assert(content_length.to_i == length.to_i, "Length doesn't match. Content length is: #{content_length} Expected: #{length}")
end

Then /^the content length in response header is "(.*?)" less than the total content length$/ do |length|
  content_length = @res.headers[:content_length]
  assert(content_length.to_i == @total_content_length.to_i - length.to_i)
end

Then /^the content length in response header is greater than the requested range of "(.*?)"$/ do |length|
  content_length = @res.headers[:content_length]
  assert(content_length.to_i > length.to_i, "Returned length should be larger than requested. Returned length is: #{content_length} Requested: #{length}")
end

Then /^I store the file content$/ do
  @received_file = Dir.pwd + "/Final.tar"
  File.open(@received_file, "a") do |outf|
    outf << @res.body
  end
end

Then /^I process the file content$/ do
  file = File.open(@path, "rb")
  original_tar_contents = file.read

  res_content = @res.body.split("\r\n")
  @received_file = Dir.pwd + "/Final.tar"

  File.open(@received_file, "wb") do |outf|
    res_content.each { |content|
      if not ((content.include? "--MULTIPART_BYTERANGES") || (content.include? "Content-Range"))
        outf << content
      end
    }
  end
end

Then /^the file is decrypted$/ do
  file = File.open(@received_file, "rb")
  contents = file.read
  @final_content = decrypt(contents)
end

Then /^I see that the combined file matches the tar file$/ do
  assert(File.size(@received_file) == @total_content_length.to_i, "Combined file isn't the same size as the tar file.")
  received_contents = File.open(@received_file, 'rb') { |f| f.read}
  received_hash = Digest::SHA1.hexdigest(received_contents)
  #puts "Hash of combined file is #{received_hash}"
  #sample_contents = File.open(@orig_content, 'rb') { |f| f.read}
  sample_hash = Digest::SHA1.hexdigest(@orig_content)
  #puts "Hash of the tar file is #{sample_hash}"
  assert(received_hash == sample_hash, "Combined file doesn't match the tar file.")
  File.delete(@received_file)
  @received_file = nil
end

Then /^I combine the overlapped parts$/ do
  #assuming chunks are in order
  chunk_content = @res.body
  chunk_length = @res.headers[:content_length]
  chunk_range = @res.headers[:content_range]
  range = chunk_range.split("bytes ")[1].split("/")[0]
  range_start = range.split("-")[0].to_i
  range_end = range.split("-")[1].to_i

  current_file_size = File.size(@received_file)
  if (range_end > current_file_size)
    range_start = current_file_size - range_start
  end

  nonoverlap_range = Range.new(range_start, range_end)
  nonoverlap_content = chunk_content[nonoverlap_range]
  File.open(@received_file, "a") do |outf|
    outf << nonoverlap_content
  end
end

Then /^I verify the bytes I have are correct$/ do
  chunk_length = @res.headers[:content_length]
  chunk_range = @res.headers[:content_range]
  range = chunk_range.split("bytes ")[1].split("/")[0]
  range_start = range.split("-")[0].to_i
  range_end = range.split("-")[1].to_i
  
  assert(compareWithOriginalFile(@res.body, range_start, range_end) == true, "Files differ between bytes #{range_start} and #{range_end}")
end

Then /^the file size is "(.*?)"$/ do |file_size|
  actual_file_size = File.size(@received_file)
  puts "Actual file size: #{actual_file_size}"
  assert(file_size == actual_file_size,"Actual file size does not match expected. Actual: #{actual_file_size} Expected: #{file_size}")
end

Then /^I verify I do not have the complete file$/ do
  assert(File.size(@received_file) != @orig_content.size, "Apparently, I do have the complete file")
  File.delete(@received_file)
  @received_file = nil
end

Then /^I store the contents of the first call$/ do
  res_content = @res.body.split(%r{--MULTIPART_BYTERANGES\r\nContent-Type: application/x-tar\r\nContent-Range: bytes \d{1,6}-\d{1,6}/\d{1,6}\r\n})
  @content1 = res_content[1].strip()
  puts @content1.size
  @content3 = res_content[2].split(%r{\r\n--MULTIPART_BYTERANGES--\r\n})[0].strip()
  puts @content3.size
end

Then /^I store the contents of the second call$/ do
  res_content = @res.body.split(%r{--MULTIPART_BYTERANGES\r\nContent-Type: application/x-tar\r\nContent-Range: bytes \d{1,6}-\d{1,6}/\d{1,6}\r\n})
  @content2 = res_content[1].strip()
  puts @content2.size
  @content4 = res_content[2].split(%r{\r\n--MULTIPART_BYTERANGES--\r\n})[0].strip()
  puts @content4.size
end

Then /^I combine the file contents$/ do
  @received_file = Dir.pwd + "/Final.tar"
  File.open(@received_file, "wb") do |outf|
        outf << @content1
        outf << @content2
        outf << @content3
        outf << @content4
  end
  puts File.size(@received_file)
end

Then /^I check the version of http response headers$/ do
  LATEST_API_VERSION = "v1.2"

  returned_version = @res.headers[:x_executedpath].split("/").first

  assert(returned_version==LATEST_API_VERSION, "Returned version is wrong. Actual: #{returned_version} Expected: #{LATEST_API_VERSION}")
end

Then /^I check the http response headers$/ do  

  if @zip_file_name == "sample-extract.tar"
    EXPECTED_LAST_MODIFIED = "Not Specified"
    assert(@res.headers[:last_modified].to_s==EXPECTED_LAST_MODIFIED, "Last Modified date is wrong! Actual: #{@res.headers[:last_modified]} Expected: #{EXPECTED_LAST_MODIFIED}" )
  elsif @res.code == 200
    @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
    coll = "bulkExtractFiles";
    src_coll = @db[coll]
    raise "Could not find #{coll} collection" if src_coll.count == 0

    ref_doc = src_coll.find({"body.tenantId" => "Midgar"}).to_a
    raise "Could not find #{coll} document with tenant #{"Midgar"}" if ref_doc.count == 0

    puts "bulkExtractFiles record: #{ref_doc}"
    
    found = false
    ref_doc.each do |row|
      
      path = row['body']['path']
      assert(path != nil, "A mongo record doesn't have data for a bulk extract file's location")
      
      file_name = File.basename(path)
      
      if file_name == @zip_file_name

        dateFromMongo = row['body']['date'].to_datetime.to_time.to_s
        if dateFromMongo != nil
          @last_modified = DateTime.parse(@last_modified).to_time.to_s
          assert(@last_modified==dateFromMongo, "last-modified must be #{dateFromMongo} was #{@last_modified}")
        end
        
        found = true
        
      end
    end
    
    assert(found, "A bulk extract with #{@zip_file_name} was not found in the mongo database")
    
  end
end

Then /^the response is decrypted$/ do
  private_key = OpenSSL::PKey::RSA.new File.read './test/features/utils/keys/vavedra9ub.key'
  assert(@res.body.length >= 512)
  encryptediv = @res.body[0,256] 
  encryptedsecret = @res.body[256,256]
  encryptedmessage = @res.body[512,@res.body.length - 512]

  decrypted_iv = private_key.private_decrypt(encryptediv)
  decrypted_secret = private_key.private_decrypt(encryptedsecret)
 
  aes = OpenSSL::Cipher.new('AES-128-CBC')
  aes.decrypt
  aes.key = decrypted_secret
  aes.iv = decrypted_iv
  @plain = aes.update(encryptedmessage) + aes.final
  if $SLI_DEBUG 
    puts("Decrypted iv type is #{decrypted_iv.class} and it is #{decrypted_iv}")
    puts("Encrypted message is #{encryptedmessage}")
    puts("Cipher is #{aes}")
    puts("Plain text length is #{@plain.length} and it is #{@plain}")
    puts "length #{@res.body.length}"
  end
 # @plain = decrypt(@res.body) 
end

Then /^I have all the information to make a custom bulk extract request$/ do
  puts "@res.headers: #{@res.headers}"
  @last_modified = @res.headers[:last_modified]
  @accept_ranges = @res.headers[:accept_ranges]
  @etag = @res.headers[:etag]
  @content_range = @res.headers[:content_range]
  @content_length = @res.headers[:content_length]
  assert(@last_modified != nil, "Last-Modified header is empty")
  assert(@accept_ranges == "bytes", "Accept-Ranges header is not bytes")
  assert(@etag != nil, "ETag header is empty")
  assert(@content_length.to_i > 512, "Content-Length header is incorrect")
  assert(@content_range != nil, "Content-Range header is incorrect")
  @total_content_length = @content_length
  @orig_content = @res.body
end

When /^I make a head request with each returned URL$/ do
  assert(@res.body.has_key?("list"), "Response contains no lis of URLs")

  types = ["fullLeas", "deltaLeas", "fullSea", "deltaSea"]

  types.each do |type| 
    @res.body[type].each do |leaId, links|
      puts "Checking LEA #{leaid}"
      links.each do |key, link|
        restHttpHeadFullPath(link)
        step "the return code is 200 I get expected tar downloaded"
      end
    end
  end
end

Then /^check to find if record is in collection:$/ do |table|
  table.hashes.map do |row|
    assert(@res.body[row["fieldName"]].length == row["count"], "Response contains wrong number of URLS, expected {} count{}, returned {}", row["fieldName"], row["count"], @res.body[row["fieldName"]])
  end
end


def getAppId()
  db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  userSessionColl = db.collection("userSession")
  clientId = userSessionColl.find_one({"body.appSession.token" => @sessionId}) ["body"]["appSession"][0]["clientId"]
  appColl = db.collection("application")
  appId = appColl.find_one({"body.client_id" => clientId}) ["_id"]
  return appId
end

def decrypt(content, client_id = "vavedra9ub")
  private_key = OpenSSL::PKey::RSA.new File.read "./test/features/utils/keys/#{client_id}.key"
  assert(content.length >= 512)
  encryptediv = content[0,256] 
  encryptedsecret = content[256,256]
  encryptedmessage = content[512,content.length - 512]

  decrypted_iv = private_key.private_decrypt(encryptediv)
  decrypted_secret = private_key.private_decrypt(encryptedsecret)
 
  aes = OpenSSL::Cipher.new('AES-128-CBC')
  aes.decrypt
  aes.key = decrypted_secret
  aes.iv = decrypted_iv
  @decrypted = aes.update(encryptedmessage) + aes.final

  if $SLI_DEBUG 
    puts("Decrypted iv type is #{decrypted_iv.class} and it is #{decrypted_iv}")
    puts("Encrypted message is #{encryptedmessage}")
    puts("Cipher is #{aes}")
    puts("Plain text length is #{@decrypted.length} and it is #{@decrypted}")
    puts "length #{content.length}"
  end
  return @decrypted
end

def compareWithOriginalFile(content, range_start, range_end)
  range = Range.new(range_start, range_end)
  file_range_content = @orig_content[range]
  
  if (file_range_content == content)
    return true
  else
    return false
  end
end

def makeCustomHeader(range, if_range = @etag, last_modified = @last_modified)
   header = {:if_range => if_range}
   header.store(:last_modified, last_modified)
   header.store(:range, "bytes=" + range)
   return header
end
