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


When /^I see a header on the page$/ do
  begin
    header = @explicitWait.until{@driver.find_element(:id, "sli_banner")}
  rescue
    if (header == nil)
      assert(false,"Header was not found on the page. Is Portal down?")
    end
  end
end

When /^I see a footer on the page$/ do
  begin
    footer = @driver.find_element(:id, "sli_footer")
    rescue
    if (footer == nil)
      assert(false, "Footer was not found on the page. Is Portal down?")
    end
  end
end
