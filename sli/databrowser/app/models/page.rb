=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


class Page
  
  attr_accessor :next, :prev
  
  def initialize(headers = {})
    @prev = nil
    @next = nil
    if(!headers.nil? && headers['Link'])
      @headers = headers['Link'];
      values = @headers.split(';')
      logger.debug {values.inspect}
      values[0].scan(/offset=(\d+)/) do |d|
        @next = d.first if values[1].include? "next"
        @prev = d.first if values[1].include? "prev"
      end
    end
  end
  def next?
    !@next.nil?
  end
  
  def prev?
    !@prev.nil?
  end
  
  
end