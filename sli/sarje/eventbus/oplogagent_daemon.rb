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
require 'socket'

if __FILE__ == $0
    unless ARGV.length == 1
        puts "Usage: " + $0 + " config.yml"
        exit(1)
    end

    config = YAML::load( File.open( ARGV[0] ))
    
    # make the config symbol based
    config.keys().each { |k| config[k.to_sym] = config.delete(k) }
    
    # setup the logger
    $stdout.sync = true
    logger = Logger.new(Logger.const_get(config[:logger_output]))
    logger.level = Logger.const_get(config[:logger_level])

    # set up the oplog agent and keep waiting indefinitely until the threads terminate
    node_id = config.fetch(:node_id, "") + Socket.gethostname 
    agent = Eventbus::EventPublisher.new(node_id, '/topic/oplog', config, logger)
    config.update(:event_publisher => agent)
    oplog_agent = Eventbus::OpLogAgent.new(config, logger)
    threads = oplog_agent.threads 
    threads.each { |aThread| aThread.join }
end
