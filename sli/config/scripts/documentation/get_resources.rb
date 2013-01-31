#!/usr/bin/env ruby

require 'json'
require 'rest-client'

$namespace_files = {}
$generated_ids = []

# use $known_ids to tell the script to get a specific ID instead of the 1-part
# URL. This is for the context re-writing.
# Jenkins is currently setup to use SDS data set.
$known_ids = {
  'assessments' => '',
  'attendances' => '4851c7adbe7beecf29cc4a3046a8e62d1bced079_id',
  'cohorts' => 'd4141f13a54a30a0daef0adced5db8ef9c3d8879_id',
  'competencyLevelDescriptor' => '',
  'courseOfferings' => '9a6352ee29305153411f0309b83c738f3efa4ac0_id',
  'courses' => '5bb6a8e0c65eb9cf5821d069cfb8116a6231b1ab_id',
  'courseTranscripts' => '',
  'disciplineActions' => '9c4d62d9af758b3b7124836ffc75afd98a858c6b_id',
  'disciplineIncidents' => '71c6e7baacd2d0367a04c056fa365a468dead7b4_id',
  'educationOrganizations' => '',
  'gradebookEntries' => '135963f2abd3320ae508546fbff31f37e10b949e_idbbfd4364e569b963aa25dbe015c5f09db96342cb_id',
  'grades' => '058750f006993539165ea75710fac72e3f7f30c6_id',
  'gradingPeriods' => '51cda601eed6f7924381ad3e054ac04540ab62de_id',
  'graduationPlans' => '',
  'learningObjectives' => '',
  'learningStandards' => '',
  'parents' => 'd0062295d13fd7a412375766eee235308b867ee9_id',
  'programs' => '983dd657325009aefa88a234fa18bdb1e11c82a8_id',
  'reportCards' => '8f3a05e77f7d902f963b73b5ec072ced1583fbda_id',
  'schools' => '772a61c687ee7ecd8e6d9ad3369f7883409f803b_id',
  'sections' => '9c1a505931e9249290b72137da119dc138bbdbae_id',
  'sessions' => 'f1f768e7ec6f9936b2414372dcb046a3bca7ad93_id',
  'staff' => '',
  'staffCohortAssociations' => '77d027fa7ebb00aac5b2887c9ffc2f1a19b8d8cd_id',
  'staffEducationOrgAssignmentAssociations' => '',
  'staffProgramAssociations' => '5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id',
  'studentAcademicRecords' => 'd4b74338921ea2c019495e7bc2485cedd4f9c345_id',
  'studentAssessments' => '25369655c44d8d9346b356a75b8ac3552bb85e6e_id379f6722061c9f5c37d4b8bd93cae1f67016f64b_id',
  'studentCohortAssociations' => '',
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
<!ENTITY % entities SYSTEM \"../../common/entities.ent\">
%entities;
]>

<chapter xml:id=\"%%XMLID%%\"
    xmlns=\"http://docbook.org/ns/docbook\"
    xmlns:xi=\"http://www.w3.org/2001/XInclude\"
    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">

    <title>Example Requests &amp; Responses for &RESTAPI; %%NAMESPACE%%</title>

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

$response_unavailable = "Response not available."

#end templates


def wait_for_api
  retry_count = 0
  api_status = 0
  url = "#{$base_url}/v1/home"

  # wait up to 180s for the API to come up
  while retry_count < 60 and api_status != 200
    retry_count += 1
    begin
      headers = {:content_type => "application/json", :accept => "application/json", :Authorization => "bearer " + $token}
      res = RestClient.get(url, headers){|response, request, result| response }
      api_status = res.code
    rescue Exception => e
      # ignore exception
    end
    sleep(3)
  end
  if api_status != 200
    $stderr.puts("Can't connect to API.")
    exit(1)
  end
end


def get_namespace_file(namespace)
  ns_file = $namespace_files[namespace]

  if !ns_file
    #if file exists, delete it
    ns_filename = get_namespace_filename(namespace)
    if File.exists?(ns_filename)
      File.delete(ns_filename)
    end

    #open file, add to hash
    ns_file = File.new(ns_filename, "w")
    $namespace_files[namespace] = ns_file

    ns_file.puts($template_header.gsub("%%XMLID%%", ns_filename.gsub(".xml", "")).gsub("%%NAMESPACE%%", namespace))
  end

  ns_file
end


def get_namespace_filename(namespace)
  return "ch-examples-#{namespace}.xml"
end


def close_all_namespace_files()
  $namespace_files.each do |k, v|
    v.puts($template_footer)
    v.close()
  end
end


def map_entity(s)
  s.gsub('childLearningObjectives', 'learningObjectives').gsub('parentLearningObjectives', 'learningObjectives')
end


def is_entity(s)
  ss = map_entity(s)
  ss = ss[0] == "/" ? ss[1..-1] : ss  # strip leading slash
  $known_ids.has_key?(ss)
end


def use_known_id(s)
  ss = s[0] == "/" ? s[1..-1] : s  # strip leading slash
  is_entity(ss) and $known_ids[ss] != ""
end


def final_entity(s)
  mapped_url = map_entity(s)

  last_slash = s.rindex("/")
  if last_slash != nil
    s[(last_slash+1)..-1]
  else
    s
  end
end


def get_response(url)
  headers = {:content_type => "application/json", :accept => "application/json", :Authorization => "bearer " + $token}
  response = RestClient.get(url, headers){|response, request, result| response }

  if response.code == 200
    entity_json = JSON.parse response.gsub($base_url, $base_url_replace)

    if entity_json.is_a?(Array)
      entity_json = entity_json[0]
    end

    entity_json
  end
end


def get_entity_responses(resource_namespace)
  namespaces = resource_namespace["nameSpace"]
  resources = resource_namespace["resources"]
  entities = {}

  resources.each do |resource|
    endpoint = resource['path']

    if is_entity(endpoint)
      namespaces.each do |namespace|
        ns_endpoint = namespace + endpoint
        url = $base_url + "/" + ns_endpoint

        # use 2-part, instead of 1-part endpoint
        if use_known_id(endpoint)
          url = url + "/" + $known_ids[final_entity(endpoint)]
        end

        response = get_response(url)

        if response and response != ""
          # puts "Storing entity: #{endpoint}"
          entities.store(ns_endpoint, response)
        else
          $stderr.puts "No response for entity: #{ns_endpoint}"
        end
      end # namespace loop
    end # entity filter
  end # resource loop

  entities
end


def get_and_print_response(namespace, endpoint)
  url = $base_url + "/" + namespace + endpoint
  response = get_response(url)

  if response
    response = format_json(response)
  else
    response = $response_unavailable
    $stderr.puts "No response for entity: #{namespace}#{endpoint}"
  end

  print_response(namespace, endpoint, response)
end


def print_response(namespace, endpoint, response)
  endpoint_id = "ex-" + namespace + endpoint.gsub("/", "-").gsub("{", "").gsub("}", "").gsub(".", "_")

  if !$generated_ids.include? endpoint_id
    $generated_ids << endpoint_id

    url = $base_url_replace + "/" + namespace + endpoint
    output = $template % [endpoint_id, endpoint, "GET " + url, response]

    get_namespace_file(namespace).puts(output)
  else
    $stderr.puts "Skipping previously generated endpoint: #{endpoint_id}"
  end

end


def format_json(json, wrap_in_array = false)
  formatted_json = JSON.pretty_generate(json)
  if wrap_in_array
    formatted_json = "[#{formatted_json}]"
  end
  #replace IDs
  formatted_json.gsub(/\"\w*_id\"/, "\"{id}\"").gsub(/\/\w*_id\//, "\/{id}\/").gsub(/\/\w*_id\"/, "\/{id}\"").gsub(/\=\w*_id\"/, "\={id}\"")
end


def print_namespace_responses(resource_namespace, entities)
  namespaces = resource_namespace["nameSpace"]
  resources = resource_namespace["resources"]
  #print out subresources

  resources.each do |resource|
    endpoint = resource['path']
    namespaces.each do |namespace|
      ns_endpoint = namespace + endpoint
      url = $base_url_replace + "/" + ns_endpoint

      # handle base path
      if is_entity(endpoint)
        entity = namespace + "/" + final_entity(endpoint)
        two_part_endpoint = endpoint + "/{id}"
        formatted_1part_response = entities.has_key?(entity) ? format_json(entities[entity], true) : $response_unavailable
        formatted_2part_response = entities.has_key?(entity) ? format_json(entities[entity]) : $response_unavailable

        print_response(namespace, endpoint, formatted_1part_response)
        print_response(namespace, two_part_endpoint, formatted_2part_response)
      else
        # print non-entity response
        get_and_print_response(namespace, endpoint)
      end

      # handle subresource paths
      if resource.has_key?("subResources")
        resource['subResources'].each do |subresource|
          sr_path = subresource['path']
          sr_endpoint = endpoint + subresource['path']
          sr_entity = final_entity(sr_path)

          if is_entity(endpoint) and is_entity(final_entity(sr_path))
            sr_entity = namespace + "/" + final_entity(sr_endpoint)
            formatted_sr_response = entities.has_key?(sr_entity) ? format_json(entities[sr_entity], true) : "Response not available."
            print_response(namespace, sr_endpoint, formatted_sr_response)
          else
            # print non-entity response
            get_and_print_response(namespace, sr_endpoint)
          end
        end
      end # subresource loop

    end # namespaces loop
  end # resources loop
end


def main(sli_resources_path)
  wait_for_api

  # read in the v1 resources file
  sli_resource_namespaces = JSON.parse File.read(sli_resources_path)

  sli_resource_namespaces.each do |resource_namespace|
    # hit all the 1-part (or 2-part for $known_ids) endpoints, getting
    # a sample entity response for each one. it will be used to generate
    # all entity responses.
    entity_responses = get_entity_responses(resource_namespace)

    print_namespace_responses(resource_namespace, entity_responses)
  end # for each namespace
end


if __FILE__ == $PROGRAM_NAME
  if ARGV.size != 4
    puts "Usage: ./get_v1_resources <path/to/v1_resources.json> <token> <base_url> <base_url_replace>"
    puts "Missing entities are printed to stderr"
    exit
  end

  sli_resources_path = ARGV[0]
  $token = ARGV[1]
  $base_url = ARGV[2]
  $base_url_replace = ARGV[3]

  main(
    sli_resources_path
    )

  close_all_namespace_files

end
