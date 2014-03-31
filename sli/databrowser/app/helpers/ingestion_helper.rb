=begin
#--

Copyright 2012-2014 inBloom, Inc. and its affiliates.

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

require 'rest-client'

module IngestionHelper

  # Method to get the Ingestion data needed for the ingestion pages and tables
  def get_ingestion_data_by_id(id, filename)
    logger.info("Ingestion: #{id}.#{filename}")
    url = drop_url_version + "/ingestionJobs/#{id}.#{filename}"
    begin
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
    rescue => e
      logger.info("Could not get ed orgs for #{entities} because of #{e.message}")
    end
    entities
  end
  
end