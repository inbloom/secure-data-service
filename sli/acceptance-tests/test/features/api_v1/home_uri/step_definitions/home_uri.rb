require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'


# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = "b078688c-9e58-4b6a-bbe2-ffde80d00538" if template == "<'educator' ID>"
  id = "c13cf9a6-6779-4de6-8b48-f3207952bfb8" if template == "<'aggregator' ID>"
  id = "0e26de79-222a-4d67-9201-5113ad50a03b" if template == "<'administrator' ID>";
  id = "269be4c9-a806-4051-a02d-15a7af3ffe3e" if template == "<'leader' ID>";
  id = "319fc71c-6bc8-4ca7-b5ce-92e0c9ca763d" if template == "<'baduser' ID>";
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)$/ do |version, uri, template, assoc|
  version + uri + Transform(template) + assoc
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)(\/[\w-]+)$/ do |version, uri, template, assoc, entity|
  version + uri + Transform(template) + assoc + entity
end

