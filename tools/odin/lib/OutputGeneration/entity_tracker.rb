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

require 'json'
require 'logger'

class EntityTracker
  attr_accessor :counts

  def initialize
    @counts = Hash.new(0)
  end

  def lookup_edfi(entity)
    entity.name
  end

  def track(entity)
    edfi_name = lookup_edfi(entity.class)
    if edfi_name == "LocalEducationAgency" || edfi_name == "StateEducationAgency" || edfi_name == "School"
      @counts["EducationOrganization"] += 1
    else
      @counts[edfi_name] += 1
    end
  end

  def display
    pattern = "%-45s%10i\n"
    "-------------------------------------------------------\n" +
    "Ed-fi entity counts: \n" +
    "-------------------------------------------------------\n" +
    @counts.sort.map{|type, count|
      pattern % ["#{type}:", count]
    }.inject(:+) +
    "-------------------------------------------------------\n" +
    pattern % ["Total entity count:", @counts.values.inject(:+)] +
    "-------------------------------------------------------"
  end

  def count(entity_type)
    entity_name =  lookup_edfi(entity_type)
    if entity_name == "LocalEducationAgency" || entity_name == "StateEducationAgency" || entity_name == "School"
      @counts["EducationOrganization"]
    else
      @counts[entity_name]
    end
  end

  def clear
    @counts.clear
  end

  # print out a manifest of entity counts
  def write_edfi_manifest(file)
    file.write JSON.pretty_generate(@counts)
  end

end
