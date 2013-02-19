require 'nokogiri'
require 'json'

IGNORED_REQUESTS = ["/api/oauth/sso", "simple-idp", "IDP Login HTTP Request", "/api/rest/saml/sso/post", "/api/oauth/token"]
JTL_FILE_PREFIX = "test"
TOTAL_LABEL = "total"
ERROR_LABEL = "error"

module ApiLoadTest

  class Runner
    def get_errors(jtl_file)
      request_to_sample_array = build_request_to_sample_array(jtl_file)
      errors = {}
      request_to_sample_array.each do |request, samples|
        samples.each do |sample|
          if sample.success_flag == false
            errors[sample.label] = [] if errors[sample.label].nil?
            errors[sample.label] << sample
          end
        end
      end
      errors
    end

    def aggregate_all_result
      results = {}
      Dir.foreach(@result_dir) do |scenario_folder|
        unless [".", "..", "oauth"].include? scenario_folder
          path = File.join(@result_dir, scenario_folder)
          if File.directory? path
            results[scenario_folder] = {}
            Dir.foreach(path) do |jtl_file|
              if match_index = jtl_file.match(/test([0-9]+)[.]jtl$/)
                thread_count = match_index[1].to_i
                results[scenario_folder][thread_count] = build_scenario_result(thread_count, File.join(path, jtl_file))
              end
            end
          end
        end
      end
      results
    end

    def aggregate_multinode_test_result
      aggregated_result = {}
      Dir.foreach(@result_dir) do |result_folder|
        unless [".", ".."].include?(result_folder)
          path = File.join(@result_dir, result_folder)
          if File.directory? path
            test_name = path.split('/')[-1]
            file = File.read(File.join(path, "result.json"))
            json = JSON.parse(file)
            json.each do |scenario, result|
              aggregated_result[scenario] = {} if aggregated_result[scenario].nil?
              categories = result.keys.sort! {|x,y| x.to_i <=> y.to_i}
              categories.each do |category|
                aggregated_result[scenario][category] = {} if aggregated_result[scenario][category].nil?
                aggregated_result[scenario][category][test_name] = result[category]["total"]
              end
            end
          end
        end
      end
      aggregated_result
    end

    def collect_all_errors(config_dir)
      errors = {}
      Dir.foreach(config_dir) do |file|
        next if @ignore.include? file
        if match_index = file =~ /[.]jmx$/
          scenario = file[0..(match_index - 1)]
          full_path = File.join(@result_dir, scenario)
          #TODO: generalize error collection
          jtl_file = File.join(full_path, "#{JTL_FILE_PREFIX}1.jtl")
          errors[scenario] = get_errors(jtl_file)
        end
      end
      errors
    end

  end
end
