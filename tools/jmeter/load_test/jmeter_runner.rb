require 'nokogiri'
require 'json'

IGNORED_REQUESTS = ["/api/oauth/sso", "simple-idp", "IDP Login HTTP Request", "/api/rest/saml/sso/post", "/api/oauth/token"]
JTL_FILE_PREFIX = "test"
TOTAL_LABEL = "total"
ERROR_LABEL = "error"

module ApiLoadTest
  class Sample
    attr_accessor :elapsed_time, :latency, :timestamp, :success_flag, :label, :response_code, :response_message,
                  :thread_name, :data_type, :bytes
    def initialize(sample)
      hash = {}
      sample.each do |pair|
        hash[pair[0]] = pair[1]
      end

      @elapsed_time = hash["t"].to_i
      @latency = hash["lt"].to_i
      @timestamp = hash["ts"].to_i
      @success_flag = (hash["s"] == "true")
      @label = hash["lb"]
      @response_code = hash["rc"]
      @response_message = hash["rm"]
      @thread_name = hash["tn"]
      @data_type = hash["dt"]
      @bytes = hash["by"].to_i

      @label = "simple-idp" if @label.include?("simple-idp")
    end
  end

  class Runner
    def initialize(config = {})
      @remote = config[:remote] || false
      @remote_servers = config[:remote_servers]
      @result_dir = config[:result_dir]
      @jmeter_exec = config[:jmeter_exec]
      @max_avg_elapsed_time = config[:max_avg_elapsed_time] || 300000
      @jmeter_prop = config[:jmeter_prop]
      @ignore = config[:ignore]
    end

    def run_jmeter(jmx_file, jtl_file, thread_count)
      File.delete(jtl_file) if File.exist? jtl_file
      command = @remote ? "#{@jmeter_exec} -n -t #{jmx_file} -G #{@jmeter_prop} -l #{jtl_file} -Gthreads=#{thread_count} -Gloops=1 -R #{@remote_servers.join(',')}" :
          "#{@jmeter_exec} -n -q #{@jmeter_prop} -t #{jmx_file} -l #{jtl_file} -Jthreads=#{thread_count} -Jloops=1"

      p "Spawning process command: #{command}"
      pid = Process.spawn(command)
      Process.wait(pid)
    end

    def build_request_to_sample_array(jtl_file)
      scenario_to_sample_array = {}
      File.open(jtl_file, 'rb') do |file|
        doc = Nokogiri.XML(file)
        doc.css('httpSample').each do |sample|
          sample = Sample.new(sample)
          scenario_to_sample_array[sample.label] = [] if scenario_to_sample_array[sample.label].nil?
          scenario_to_sample_array[sample.label] << sample
        end
      end
      scenario_to_sample_array
    end

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

    def build_scenario_result(thread_count, jtl_file)
      request_to_sample_array = build_request_to_sample_array(jtl_file)
      request_to_sample_array.reject! do |label, samples|
        label.include?("/api/oauth/sso") || IGNORED_REQUESTS.include?(label)
      end

      scenario_result = {}
      average_total = 0
      error_count = 0
      request_to_sample_array.each do |request, samples|
        total_elapsed_time = 0
        samples.each do |sample|
          if sample.success_flag
            total_elapsed_time += sample.elapsed_time
          else
            error_count += 1
          end
        end
        scenario_result[request] = @remote ? (total_elapsed_time * 1.0 / thread_count / @remote_servers.size).ceil :
            (total_elapsed_time * 1.0 / thread_count).ceil
        average_total += scenario_result[request]
      end
      scenario_result[TOTAL_LABEL] = average_total
      scenario_result[ERROR_LABEL] = error_count
      scenario_result
    end

    def collect_all_data(config_dir, thread_count_array)
      FileUtils.mkdir_p(@result_dir)
      Dir.foreach(config_dir) do |file|
        next if @ignore.include? file
        if match_index = file =~ /[.]jmx$/
          full_path = File.join(@result_dir, file[0..(match_index - 1)])
          FileUtils.mkdir_p(full_path)
          thread_count_array.each do |thread_count|
            jtl_file = File.join(full_path, "#{JTL_FILE_PREFIX}#{thread_count}.jtl")
            run_jmeter(File.join(config_dir, file), jtl_file, thread_count) unless File.exists?(jtl_file)
            break if build_scenario_result(thread_count, jtl_file)[TOTAL_LABEL] > @max_avg_elapsed_time
          end
        end
      end
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
