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


require 'pty'
require 'timeout'

module ExternalProcessRunner

  #
  # Spawns an external command
  #
  # Parameters:
  #   cmd - a string, representing the command to be run
  #   timeout - an integer, represents the timout in seconds
  #
  # Returns the command's stdout if it finishes within timeout seconds,
  # Otherwise returns null
  #
  def self.run(cmd, timeout)
    retVal = ''

    # set timeout
    begin
      timeout (timeout) do

        # Spawn process and read from output
        begin
          PTY.spawn( cmd ) do |stdin, stdout, pid|
            begin
              # accumulate cmd's output to stdout (called stdin here)
              stdin.each { |line| retVal = retVal + line }
            rescue Errno::EIO => e 
              Rails.logger.error e.message
              Rails.logger.error e.backtrace.join("\n")
            end
          end
        rescue PTY::ChildExited
        end

      end

        # handle timeout
    rescue Timeout::Error
      retVal = nil
    end

    return retVal

  end

end
