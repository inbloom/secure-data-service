require 'fileutils'

require_relative './jmeter_result_processor'

module LoadTest
  class JmeterRunner
    JTL_FILE_PREFIX = "test"

    def initialize(jmeter_exec, config = {})
      @remote = config[:remote] || false
      @remote_servers = config[:remote_servers]
      @jmeter_exec = jmeter_exec
      @max_avg_elapsed_time = config[:max_avg_elapsed_time]
      @thread_count_array = config[:thread_count_array]
      @jmeter_prop = config[:jmeter_prop]
      @ignore = config[:ignore]
      @stop_when_error_occurs = config[:stop_when_error_occurs]
    end

    def run_jmeter(jmx_file, jtl_file, thread_count)
      File.delete(jtl_file) if File.exist? jtl_file
      command = @remote ? "#{@jmeter_exec} -n -t #{jmx_file} -G #{@jmeter_prop} -l #{jtl_file} -Gthreads=#{thread_count} -Gloops=1 -R #{@remote_servers.join(',')}" :
          "#{@jmeter_exec} -n -q #{@jmeter_prop} -t #{jmx_file} -l #{jtl_file} -Jthreads=#{thread_count} -Jloops=1 >> jmeter_runner.log"
      pid = Process.spawn(command)
      Process.wait(pid)
    end

    def duration(secs)
      secs  = secs.to_int
      mins  = secs / 60
      hours = mins / 60
      days  = hours / 24

      if days > 0
        "#{days} days and #{hours % 24} hours"
      elsif hours > 0
        "#{hours} hours and #{mins % 60} minutes"
      elsif mins > 0
        "#{mins} minutes and #{secs % 60} seconds"
      elsif secs >= 0
        "#{secs} seconds"
      end
    end

    def collect_all_data(test_name, file, result_dir)
      FileUtils.mkdir_p(result_dir)
      ended_early = false
      @thread_count_array.each do |thread_count|
        jtl_file = File.join(result_dir, "#{JTL_FILE_PREFIX}#{thread_count}.jtl")

        puts "Running #{test_name} with #{thread_count} thread#{thread_count > 1 ? 's' : ''}#{@remote_servers.nil? ? '' : " on #{@remote_servers.size} jmeter nodes"}."
        start_time = Time.now
        run_jmeter(file, jtl_file, thread_count)

        jmeter_result_processor = JmeterResultProcessor.new
        scenario_result = jmeter_result_processor.build_scenario_result(thread_count, jtl_file)
        total_average = scenario_result[JmeterResultProcessor::TOTAL_LABEL]
        error_count = scenario_result[JmeterResultProcessor::ERROR_LABEL]


        puts "Test completed in #{duration(Time.now - start_time)}."
        puts "Average total time per thread took #{duration(total_average)} to complete."
        puts "There #{error_count > 1 ? 'are' : 'is'} #{error_count} error#{error_count > 1 ? 's' : ''}"

        if total_average > @max_avg_elapsed_time
          puts "Ending #{test_name} test because total average time #{total_average}ms exceeds #{@max_avg_elapsed_time}ms\n"
          ended_early = true
          break
        end
        if (@stop_when_error_occurs && error_count > 0)
          puts "Ending #{test_name} test because error exists\n"
          ended_early = true
          break
        end
      end
      unless ended_early
        puts "Test #{test_name} completed successfully.\n"
      end
    end
  end
end