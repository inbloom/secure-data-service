require 'nokogiri'
require 'json'

API_LOAD_TEST_HOME = File.dirname(File.absolute_path(__FILE__))
IGNORED_JMX_FILES = ["oauth.jmx"]
IGNORED_REQUESTS = ["/api/oauth/sso", "simple-idp", "IDP Login HTTP Request", "/api/rest/saml/sso/post", "/api/oauth/token"]
JTL_FILE_PREFIX = "test"
TOTAL_LABEL = "total"

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
      @result_dir = config[:result_dir] || "#{API_LOAD_TEST_HOME}/result"
      @jmeter_exec = config[:jmeter_exec] || "/opt/apache-jmeter/bin/jmeter"
      @max_avg_elapsed_time = config[:max_avg_elapsed_time] || 30000
      @jmeter_prop = config[:jmeter_prop] || "#{API_LOAD_TEST_HOME.split('/')[0..-2].join('/')}/local.properties"
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
      request_to_sample_array.each do |request, samples|
        total_elapsed_time = 0
        samples.each do |sample|
          total_elapsed_time += sample.elapsed_time
        end
        scenario_result[request] = @remote ? (total_elapsed_time * 1.0 / thread_count / @remote_servers.size).ceil :
            (total_elapsed_time * 1.0 / thread_count).ceil
        average_total += scenario_result[request]
      end
      scenario_result[TOTAL_LABEL] = average_total
      scenario_result
    end

    def collect_all_data(config_dir, max_threads)
      Dir.mkdir(@result_dir) unless Dir.exists?(@result_dir)
      Dir.foreach(config_dir) do |file|
        next if IGNORED_JMX_FILES.include? file
        if match_index = file =~ /[.]jmx$/
          full_path = File.join(@result_dir, file[0..(match_index - 1)])
          Dir.mkdir(full_path) unless Dir.exists?(full_path)
          # TODO: does not make sense to iterate here. Move it out!
          (0..max_threads).each do |n|
            thread_count = 2 ** n
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

    def collect_all_errors(config_dir)
      errors = {}
      Dir.foreach(config_dir) do |file|
        next if IGNORED_JMX_FILES.include? file
        if match_index = file =~ /[.]jmx$/
          scenario = file[0..(match_index - 1)]
          full_path = File.join(@result_dir, scenario)
          jtl_file = File.join(full_path, "#{JTL_FILE_PREFIX}1.jtl")
          errors[scenario] = get_errors(jtl_file)
        end
      end
      errors
    end

    def self.run(config = {})
      runner = Runner.new(config)
      config_home = config[:config_home] || "#{API_LOAD_TEST_HOME}/.."
      result_json_file = config[:result_json_file] || "#{API_LOAD_TEST_HOME}/result/result.json"
      max_threads = config[:max_threads] || 20

      runner.collect_all_data(config_home, max_threads)
      File.open(result_json_file, "w") do |f|
        f.write(runner.aggregate_all_result().to_json)
      end
    end
  end
end
