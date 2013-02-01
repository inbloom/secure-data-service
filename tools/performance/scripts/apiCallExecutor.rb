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

require 'rubygems'
require 'rest-client'
require 'json'

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']
  
$SESSION_MAP = {"demo_SLI" => "e88cb6d1-771d-46ac-a207-2e58d7f12196",
                "jdoe_IL" => "c88ab6d7-117d-46aa-a207-2a58d1f72796",
                "tbear_IL" => "c77ab6d7-227d-46bb-a207-2a58d1f82896",
                "john_doe_IL" => "a69ab2d7-137d-46ba-c281-5a57d1f22706",
                "ejane_IL" => "4ab8b6d4-51ad-c67a-1b0a-25e8d1f12701",
                "johndoe_NY" => "a49cb2f7-a31d-06ba-f281-515b01f82706",
                "ejane_NY" => "c17ab6d0-0caa-c87f-100a-2ae8d0f22601",
                "linda.kim_IL" => "4cf7a5d4-37a1-ca19-8b13-b5f95131ac85",
                "cgray_IL" => "1cf7a5d4-75a2-ba63-8b53-b5f95131cc48",
                "rbraverman_IL" => "2cf7a5d4-78a1-ca42-8b74-b5f95131ac21",
                "mario.sanchez_NY" => "8cfba5a4-39a1-ca39-8413-b5697131ac85",
                "educator_SLI" => "4cf7a5d4-37a1-ca11-8b13-b5f95131ac85",
                "leader_SLI" => "4cf7a5d4-37a1-ca22-8b13-b5f95131ac85",
                "administrator_SLI" => "4cf7a5d4-37a1-ca33-8b13-b5f95131ac85",
                "aggregator_SLI" => "4cf7a5d4-37a1-ca44-8b13-b5f95131ac85",
                "baduser_SLI" => "4cf7a5d4-37a1-ca55-8b13-b5f95131ac85",
                "nouser_SLI" => "4cf7a5d4-37a1-ca66-8b13-b5f95131ac85",
                "teacher_IL" => "4cf7a5d4-37a1-ca77-8b13-b5f95131ac85",
                "prince_IL" => "4cf7a5d4-37a1-ca88-8b13-b5f95131ac85",
                "root_IL" => "4cf7a5d4-37a1-ca99-8b13-b5f95131ac85",
                "developer_SLI" => "26c4b55b-5fa8-4287-af3d-98e7b5f98232",
                "operator_SLI" => "a8cf184b-9c7e-4253-9f45-ed4e9f4f596c",
                "bigbro_IL" => "4cf7a5d4-37a1-ca00-8b13-b5f95131ac85",
                "sunsetrealmadmin_SLI" => "d9af321c-5fa8-4287-af3d-98e7b5f9d999",
                "fakerealmadmin_SLI" => "aa391d1c-99a8-4287-af3d-481516234242",
                "anotherfakerealmadmin_SLI" => "910bcfad-5fa8-4287-af3d-98e7b5f9e786",
                "badadmin_IL" => "5cf7a5d4-57a1-c100-8b13-b5f95131ac85",
                "sampleUser_SLI" => "e88cb5c1-771d-46ac-a207-e88cb7c1771d",
                "demo_IL" => "e88cb5c1-771d-46ac-a2c7-2d58d7f12196",
                "eengland_NY" => "ebbec99c-c8cf-4982-b853-3513374d0073",
                "gcanning_NY" => "0a50a4ec-e00f-4944-abac-2abbdb99f7d9",
                "jbarrera_NY" => "2485c0ec-bf37-4b30-b96e-07b98b205bf9",
                "jpratt_NY" => "2b0608b6-5162-4e13-8669-f71e9878a2ef",
                "jsmalley_NY" => "144e272d-cfbd-42a2-a8e7-ee333e77eec6",
                "jcarlyle_NY" => "81198176-7d9f-4fc1-8f4a-9ff9dda0870d",
                "mhahn_NY" => "9e95a2f8-686c-4b0f-9816-9d8dfec3de1d",
                "rlindsey_NY" => "3fe8d3dc-577b-401e-82e0-faa847048ede",
                "sholcomb_NY" => "e6aa1a6f-1ae2-4727-b9d8-131cdfdd239a",
                "llogan_IL" => "6fb146b3-6dac-41c9-ab72-0f4d4832b873",
                "jwashington_IL" => "0b496e6d-471d-4c1b-bd83-bb3fe0d671b6",
                "jvasquez_IL" => "c294f7ee-45ee-4c56-8e72-dad9c926d42b",
                "ckoch_IL" => "a21a9381-e189-408d-b21d-b44d847af83f",
                "rrogers_IL" => "cacd9227-5b14-4685-babe-31230476cf3b",
                "mjohnson_IL" => "29da4ea2-40e1-466a-8f2c-ea357d4f096c",
                "sbantu_IL" => "79abdc40-dcd8-4412-b5db-32f63befcc90",
                "jstevenson_IL" => "9f58b6dc-0880-4e2a-a65f-3aa8b5201fbd",
                "jjackson_IL" => "b7cbbc75-23bf-4005-a545-8a110eefa063",
                "kmelendez_NY" => "d93ef071-39ff-4e41-9619-f8f43d22b4bf",
                "agibbs_NY" => "1dc64dcb-354e-4ab6-be54-e8401caa06a6",
                "charrison_NY" => "8fbd7332-1af4-4524-ae6d-f28ddf600798",
                "mgonzales_IL" => "10229764-a6a0-4367-9862-fd18781c9638",
                "akopel_IL" => "438e472e-a888-46d1-8087-0195f4e37089",
                "msmith_IL" => "5679153f-f1cc-44bd-9bfa-a21a41cd020c",
                "racosta_IL" => "3f165e8d-bb42-4b62-8a2d-92f98dcd6ffc",
                "agillespie_IL" => "ba09eeb3-a50a-4278-b363-22074168421d",
                "wgoodman_IL" => "8c950c56-74f3-4e5d-a02c-d09497fddb1d" }
  
class APICallExecutor

  def initialize(authenticationDetails, format="application/vnd.slc+json", instance="")
    @user = authenticationDetails[:user]
    @realm = authenticationDetails[:realm]
    @sessionId = authenticationDetails[:token]
    @format = format
    @instance = instance

    # use the token if user is nil and token is not nil
    @useToken = @user.nil? && !@sessionId.nil?
  end

  def assert(bool, message = 'assertion failure')
    raise message unless bool
  end
  
  def idpRealmLogin(user, realm="SLI")
      token = $SESSION_MAP[user+"_"+realm]
      assert(token != nil, "Could not find session for user #{user} in realm #{realm}")
      @sessionId = token
      puts(@sessionId) if $SLI_DEBUG
  end
  
  def restHttpGet(id, format = @format, sessionId = @sessionId)
    # Validate SessionId is not nil
    assert(sessionId != nil, "Session ID passed into GET was nil")
  
    urlHeader = makeUrlAndHeaders('get',id,sessionId,format)
  
    resource = RestClient::Resource.new urlHeader[:url], :timeout => 86400, :open_timeout => 86400
    @res = resource.get(urlHeader[:headers]){|response, request, result| response }
    puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
  end
  
  def makeUrlAndHeaders(verb, id, sessionId, format)
    if(verb == 'put' || verb == 'post')
      headers = {:content_type => format}
    else
      headers = {:accept => format}
    end
  
    headers.store(:Authorization, "bearer "+sessionId)
  
    url = id
    puts(url, headers) if $SLI_DEBUG
  
    return {:url => url, :headers => headers}
  end
  
  def get(resource, outputFile)
    outputWriter = File.new(outputFile, "a")
    
    if (!@useToken)
      idpRealmLogin(@user, @realm)
    end

    ############################
    # ADD IN API INSTANCE INFO #
    ############################
    if (!resource.start_with?("http") && !@instance.empty?)
      resource = "/#{resource}" if (!resource.start_with?("/"))
      resource = "#{@instance}#{resource}"
    end

    startTime = Time.new
    restHttpGet(resource, @format, @sessionId)
    endTime = Time.new
    code = @res.code
    length = 0
    length = @res.body.to_s.length if @res.body != nil
    output = "{ \"resource\" : \"#{resource}\", \"code\" : #{code}, \"length\" : #{length}, \"time\" : \"#{(endTime - startTime)*1000}\" }\n"
    outputWriter.write(output)
      
    # Decided to include timings for 404s for custom entities (Dashboard receives these as well and ignores them)
    outputWriter.write("Unexpected response code: #{@res.code}\n#{@res.body}\n") if (@res.code != 200 && !resource.end_with?("custom"))
    outputWriter.close
  end

  def getEachInFile(inputFile, outputFile)
    outputWriter = File.new(outputFile, "a")
    
    if (!@useToken)
      idpRealmLogin(@user, @realm)
    end
  
    file = File.new(inputFile, "r")
    while (resource = file.gets)
      resource.chomp!

      ############################
      # ADD IN API INSTANCE INFO #
      ############################
      if (!resource.start_with?("http") && !@instance.empty?)
        resource = "/#{resource}" if (!resource.start_with?("/"))
        resource = "#{@instance}#{resource}"
      end

      startTime = Time.new
      restHttpGet(resource, @format, @sessionId)
      endTime = Time.new
      code = @res.code
      length = 0
      length = @res.body.to_s.length if @res.body != nil
      output = "{ \"resource\" : \"#{resource}\", \"code\" : #{code}, \"length\" : #{length}, \"time\" : \"#{(endTime - startTime)*1000}\" }\n"
      outputWriter.write(output)
        
      # Decided to include timings for 404s for custom entities (Dashboard receives these as well and ignores them)
      outputWriter.write("Unexpected response code: #{@res.code}\n#{@res.body}\n") if (@res.code != 200 && !resource.end_with?("custom"))
    end
    file.close
    outputWriter.close
  end

end

