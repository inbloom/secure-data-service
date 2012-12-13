require 'nokogiri'
require 'json'

JMETER_EXEC = "#{ARGV[0]}/bin/jmeter"
RUNNER_HOME = File.dirname(File.absolute_path(__FILE__))
CONFIG_HOME = "#{RUNNER_HOME.split('/')[0..-2].join('/')}"
JMETER_PROP = "#{CONFIG_HOME}/local.properties"
RESULT_DIR = "#{RUNNER_HOME}/result"
RESULT_JSON_FILE = "#{RESULT_DIR}/result.json"
JTL_FILE_PREFIX = "test"
TOTAL_LABEL = "total"
MAX_AVG_ELAPSED_TIME = 30000
REMOTE_SERVERS = ["localhost"]
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

def run_jmeter(jmx_file, jtl_file, thread_count)
  File.delete(jtl_file) if File.exist? jtl_file
  command = "#{JMETER_EXEC} -n -t #{jmx_file} -G #{JMETER_PROP}  -l #{jtl_file} -Gthreads=#{thread_count} -Gloops=1 -R #{REMOTE_SERVERS.join(',')}"

  p "Spawning process command: #{command}"
  pid = Process.spawn(command)
  Process.wait(pid)
end

def build_scenario_result(thread_count, jtl_file)
  # parse JTL files
  label_to_sample_array = {}
  File.open(jtl_file, 'rb') do |file|
    doc = Nokogiri.XML(file)
    doc.css('httpSample').each do |sample|
      sample = Sample.new(sample)
      label_to_sample_array[sample.label] = [] if label_to_sample_array[sample.label].nil?
      label_to_sample_array[sample.label] << sample
    end
  end

  # ignore login requests
  label_to_sample_array.reject! do |key, value|
    key.include?("/api/oauth/sso") || ["/api/oauth/sso", "simple-idp", "IDP Login HTTP Request", "/api/rest/saml/sso/post", "/api/oauth/token"].include?(key)
  end

  scenario_result = {}
  average_total = 0
  label_to_sample_array.each do |label, samples|
    total_elapsed_time = 0
    samples.each do |sample|
      total_elapsed_time += sample.elapsed_time
    end
    scenario_result[label] = (total_elapsed_time * 1.0 / thread_count / REMOTE_SERVERS.size).ceil
    average_total += scenario_result[label]
  end
  scenario_result[TOTAL_LABEL] = average_total
  scenario_result
end

def collect_all_data(config_dir, result_dir)
  Dir.mkdir(result_dir) unless Dir.exists?(result_dir)
  Dir.foreach(config_dir) do |file|
    if match_index = file =~ /[.]jmx$/
      full_path = File.join(result_dir, file[0..(match_index - 1)])
      Dir.mkdir(full_path) unless Dir.exists?(full_path)
      (0..20).each do |n|
        thread_count = 2 ** n
        jtl_file = File.join(full_path, "#{JTL_FILE_PREFIX}#{thread_count}.jtl")
        run_jmeter(File.join(config_dir, file), jtl_file, thread_count) unless File.exists?(jtl_file)
        break if build_scenario_result(thread_count, jtl_file)[TOTAL_LABEL] > MAX_AVG_ELAPSED_TIME
      end
    end
  end
end

def aggregate_all_result(result_dir)
  results = {}
  Dir.foreach(result_dir) do |scenario_folder|
    unless [".", "..", "oauth"].include? scenario_folder
      path = File.join(RESULT_DIR, scenario_folder)
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

collect_all_data(CONFIG_HOME, RESULT_DIR)
File.open(RESULT_JSON_FILE, "w") do |f|
  f.write(aggregate_all_result(RESULT_DIR).to_json)
end
