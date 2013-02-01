=begin
#--

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

# This model is used to encapsulate our paging information that the Api
# gives us so that we can more easily do paging in our entities_controller.
# It should be noted that we only ever page forward and never page back, so
# the logic for getting the previous won't work.
class Page
  
  # Paging has two simple attributes previous and next which represent the
  # offset to pass to the API.
  attr_accessor :next, :prev
  
  # We take the headers from an API response and pass them to the initializer
  # which will then in turn look for the +Link+ header and try to pull out the
  # next and previous paging items and their offsets.
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
  # Tells you if there is a next page or not by doing #.nil?
  def next?
    !@next.nil?
  end
  # Tells you if there is a previous page or not by doing #.nil?
  # This is for completeness sake, as prev doesn't do anything.
  def prev?
    !@prev.nil?
  end
  
  
end
