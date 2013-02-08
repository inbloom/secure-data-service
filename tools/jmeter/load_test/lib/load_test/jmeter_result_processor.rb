require 'nokogiri'

module LoadTest
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

  class JmeterResultProcessor
    IGNORED_REQUESTS = ["/api/oauth/sso", "simple-idp", "IDP Login HTTP Request", "/api/rest/saml/sso/post", "/api/oauth/token"]
    TOTAL_LABEL = "total"
    ERROR_LABEL = "error"

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
  end
end