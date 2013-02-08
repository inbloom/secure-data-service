module LoadTest
  module Config
    IGNORED_JMX_FILES = ["oauth.jmx"]
    UPDATE_JMX_FILES = ["update-attendance.jmx", "update-gradebooks.jmx"]
    DEFAULT_CONFIG = {
        :config_root => "..",
        :data_root => "data",
        :thread_count_array => (0..10).collect{|x| 2**x},
        :max_avg_elapsed_time => 60000,
        :stop_when_error_occurs => false,
        :ignore => IGNORED_JMX_FILES
    }

    CONFIGS = {}
    CONFIGS[:local] = {
        :description => "run load tests locally",
        :jmeter_prop => "../odin-local-setup/local.properties",
        :stop_when_error_occurs => true,
        :ignore => [IGNORED_JMX_FILES, UPDATE_JMX_FILES].flatten
    }

    CONFIGS.each do |name, config|
      CONFIGS[name] = DEFAULT_CONFIG.merge(config)
    end
  end
end
