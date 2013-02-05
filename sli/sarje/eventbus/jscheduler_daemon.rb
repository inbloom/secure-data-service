#!/usr/bin/env ruby 

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

localdir = File.dirname(__FILE__)
$LOAD_PATH << localdir + "/lib"

require 'eventbus'
require 'yaml'
require 'logger'
require 'daemons'

if __FILE__ == $0
  config_file = if ENV['SARJE_CONFIG']
                    ENV['SARJE_CONFIG']
                elsif ARGV.length == 1
                    ARGV[0]
                else 
                    puts "Usage: " + $0 + " config.yml"
                    exit(1)
                end

  config = YAML::load(File.open(config_file))

  # make the config symbol based
  config.keys().each { |k| config[k.to_sym] = config.delete(k) }

  #set up logger configuration
  $stdout.sync = true
  logger = Logger.new(Logger.const_get(config[:logger_output]))
  logger.level = Logger.const_get(config[:logger_level])

  # create instances of the queue listener and the job runner and
  # and plug them into an Jobscheduler
  listener = Eventbus::EventSubscriber.new(config, "/topic/oplog", "hadoop_job", logger)
  jobrunner = Eventbus::HadoopJobRunner.new({:hadoop_jars => config[:hadoop_jars], 
                                             :hadoop_home => config[:hadoop_home]})
  active_config = config.update(:event_subscriber => listener,
                                :jobrunner => jobrunner)

  scheduler = Eventbus::JobScheduler.new(active_config, logger)
  scheduler.join

end
