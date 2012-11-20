#!/usr/bin/env ruby

require 'json'
require 'rest-client'

# use $known_ids to tell the script to get a specific ID instead of the 1-part
# URL. This is for the context re-writing.
# Jenkins is currently setup to use SDS data set.
$known_ids = {
  'attendances' => '4851c7adbe7beecf29cc4a3046a8e62d1bced079_id',
  'cohorts' => 'd4141f13a54a30a0daef0adced5db8ef9c3d8879_id',
  'courseOfferings' => '9a6352ee29305153411f0309b83c738f3efa4ac0_id',
  'courses' => '5bb6a8e0c65eb9cf5821d069cfb8116a6231b1ab_id',
  'disciplineActions' => '9c4d62d9af758b3b7124836ffc75afd98a858c6b_id',
  'disciplineIncidents' => '71c6e7baacd2d0367a04c056fa365a468dead7b4_id',
  'gradebookEntries' => '135963f2abd3320ae508546fbff31f37e10b949e_idbbfd4364e569b963aa25dbe015c5f09db96342cb_id',
  'grades' => '058750f006993539165ea75710fac72e3f7f30c6_id',
  'gradingPeriods' => '51cda601eed6f7924381ad3e054ac04540ab62de_id',
  'parents' => 'd0062295d13fd7a412375766eee235308b867ee9_id',
  'programs' => '983dd657325009aefa88a234fa18bdb1e11c82a8_id',
  'reportCards' => '8f3a05e77f7d902f963b73b5ec072ced1583fbda_id',
  'schools' => '772a61c687ee7ecd8e6d9ad3369f7883409f803b_id',
  'sections' => '9c1a505931e9249290b72137da119dc138bbdbae_id',
  'sessions' => 'f1f768e7ec6f9936b2414372dcb046a3bca7ad93_id',
  'staffCohortAssociations' => '77d027fa7ebb00aac5b2887c9ffc2f1a19b8d8cd_id',
  'staffProgramAssociations' => '5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id',
  'studentAcademicRecords' => 'd4b74338921ea2c019495e7bc2485cedd4f9c345_id',
  'studentAssessments' => '25369655c44d8d9346b356a75b8ac3552bb85e6e_id379f6722061c9f5c37d4b8bd93cae1f67016f64b_id',
  'studentCompetencies' => 'a899667c35703b07c8005ff17abc4f2d0d7b4f21_id',
  'studentCompetencyObjectives' => '43d8973b7492dabfcd16a11951b93b6167bb969e_id',
  'studentDisciplineIncidentAssociations' => '2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_ide9fb92ec3f88adc4ac5bf7145f3ac668190022ea_id',
  'studentGradebookEntries' => '9487088e6e1de20be97dcc4ac8470d686c58c50b_id',
  'studentParentAssociations' => '92a33cae13e838176dbea9ca8b8c354d7420eaa8_id90111b50015f1b6821d74d4952f1910dcc05bb52_id',
  'studentProgramAssociations' => 'a50802f02c7e771d979f7d5b3870c500014e6803_idfbdb2bd12da6fa64d2e74242c20c2235cd3f04d4_id',
  'students' => '6c09bcafd4f0729ab1c79abfbb3929d2bfc3e642_id',
  'studentSchoolAssociations' => '7810a2adec56ef8c5e07169bbf81179a9b20deb6_id',
  'studentSectionAssociations' => 'b653fd937c66b23cd3449d235a627da4531ade7b_idbf588361e6db5c270fb95f6f6001c6dcf34abef3_id',
  'teachers' => '8107c5ce31cec58d4ac0b647e91b786b03091f02_id',
  'teacherSchoolAssociations' => '93a4133d17303788f99e3b229b9649d46de5f42e_id',
  'teacherSectionAssociations' => 'b11d9f8e0790f441c72a15a3c2deba5ffa1a5c4a_id09f27aad1cc3054393f9c5d27fadb0c562a1b93e_id'
}

# templates

$template_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>

<!DOCTYPE chapter [
<!ENTITY % slc_entities SYSTEM \"../../common/docbook_entities-slc.ent\">
%slc_entities;
]>

<chapter xml:id=\"doc-5c19acd4-a267-4d0d-96b5-2e4fcf804a63\"
    xmlns=\"http://docbook.org/ns/docbook\"
    xmlns:xi=\"http://www.w3.org/2001/XInclude\"
    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">

    <title>&RESTAPI; Example Requests &amp; Responses</title>

    <para>
        This contents of this chapter are the sample requests and responses generated
        to represent the resources listed in previous chapters.
    </para>

    <!-- Sections generated from a Ruby script -->

"

$template_footer = "</chapter>
"

$template = "<simplesect xml:id=\"%s\">
    <title>Example request/response for <varname>%s</varname>
    </title>
    <para>
        Request:
    </para>
    <programlisting>
        <![CDATA[%s]]>
    </programlisting>
    <para>
        Response:
    </para>
    <programlisting language=\"json\"><![CDATA[%s]]></programlisting>
</simplesect>

"

#end templates

def wait_for_api(token, base_url)
  retry_count = 0
  api_status = 0
  url = "#{base_url}/home"

  # wait up to 180s for the API to come up
  while retry_count < 60 and api_status != 200
    begin
      headers = {:content_type => "application/json", :accept => "application/json", :Authorization => "bearer " + token}
      response = RestClient.get(url, headers){|response, request, result| response }
      api_status = response.code
    rescue Exception => e
      # ignore exception
    end
    sleep(3)
  end
end

def final_entity(url)
  last_slash = url.rindex("/")
  if last_slash != nil
    url[(last_slash+1)..-1]
  else
    url
  end
end

def get_response(url, token, base_url, base_url_replace)
  headers = {:content_type => "application/json", :accept => "application/json", :Authorization => "bearer " + token}
  response = RestClient.get(url, headers){|response, request, result| response }

  if response.code == 200
    entity_json = JSON.parse response.gsub(base_url, base_url_replace)

    # to account for DE2172
    # this should always be an array
    if entity_json.is_a?(Array)
      entity_json = entity_json[0]
    end

    entity_json
  end
end

def get_v1_responses(v1_resources, token, base_url, base_url_replace)
  entities = {}
  v1_resources['resources'].each do |resource|
    endpoint = resource['path']
    url = base_url + endpoint
    entity = final_entity(url)

    # use 2-part, instead of 1-part endpoint
    if $known_ids.has_key?(entity)
      url = url + "/" + $known_ids[entity]
    end

    response = get_response(url, token, base_url, base_url_replace)

    if response and response != ""
      # puts "Storing entity: #{entity}"
      entities.store(entity, response)
    else
      $stderr.puts "No response for entity: #{entity}"
    end
  end
  entities
end

def print_response(endpoint, url, response)
  endpoint_id = "ex" + endpoint.gsub("/", "-").gsub("{", "").gsub("}", "")
  printf($template, endpoint_id, endpoint, "GET " + url, response)
end

def format_json(json, wrap_in_array = false)
  formatted_json = JSON.pretty_generate(json)
  if wrap_in_array
    formatted_json = "[#{formatted_json}]"
  end
  #replace IDs
  formatted_json.gsub(/\"\w*_id\"/, "\"{id}\"").gsub(/\/\w*_id\//, "\/{id}\/").gsub(/\/\w*_id\"/, "\/{id}\"").gsub(/\=\w*_id\"/, "\={id}\"")
end

def print_v1_responses(v1_resources, entities, base_url)
  #print out subresources

  v1_resources['resources'].each do |resource|
    endpoint = resource['path']
    url = base_url + endpoint
    entity = final_entity(url)
    two_part_endpoint = endpoint + "/{id}"
    two_part_url = base_url + two_part_endpoint

    # format responses
    formatted_1part_response = entities.has_key?(entity) ? format_json(entities[entity], true) : "Response not available."
    formatted_2part_response = entities.has_key?(entity) ? format_json(entities[entity]) : "Response not available."

    # print 1-part and 2-part endpoints
    print_response(endpoint, url, formatted_1part_response)
    print_response(two_part_endpoint, two_part_url, formatted_2part_response)

    #iterate over subresources
    if resource.has_key?("subResources")
      resource['subResources'].each do |subresource|
        sr_endpoint = endpoint + subresource['path']
        sr_url = base_url + sr_endpoint
        sr_entity = final_entity(sr_url)

        formatted_response = entities.has_key?(sr_entity) ? format_json(entities[sr_entity], true) : "Response not available."
        print_response(sr_endpoint, sr_url, formatted_response)
      end
    end # subresources loop
  end # resources loop
end

def main(v1_resources_path, token, base_url, base_url_replace)
  wait_for_api(token, base_url)

  # read in the v1 resources file
  v1_resources = JSON.parse File.read(v1_resources_path)

  # hit all the 1-part (or 2-part for $known_ids)
  entities = get_v1_responses(v1_resources, token, base_url, base_url_replace)

  puts($template_header)
  print_v1_responses(v1_resources, entities, base_url_replace)
  puts($template_footer)
end

if __FILE__ == $PROGRAM_NAME
  if ARGV.size != 4
    puts "Usage: ./get_v1_resources <path/to/v1_resources.json> <token> <base_url> <base_url_replace>"
    puts "Missing entities are printed to stderr"
    exit
  end

  v1_resources_path = ARGV[0]
  token = ARGV[1]
  base_url = ARGV[2]
  base_url_replace = ARGV[3]

  main(
    v1_resources_path,
    token,
    base_url,
    base_url_replace
    )
end
