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


When /^I see a header on the page that has the text "([^"]*)"$/ do |expectedText|
  begin
    header = @explicitWait.until{@driver.find_element(:id, "sli_banner")}
    logo = header.find_elements(:tag_name,"img")
    assert(logo.length == 1, "Header logo img is not found")
    headerText = header.find_element(:class, "header_right")
  
    assert(headerText.attribute("innerHTML").to_s.strip.include?(expectedText), "Header text is not found")
  rescue
    if (header == nil)
      assert(false,"Header was not found on the page. Is Portal down?")
    end
  end
end

When /^I see a footer on the page that has the text "([^"]*)"$/ do |expectedText|
  begin
    footer = @driver.find_element(:id, "sli_footer")
    assert(footer.attribute("innerHTML").to_s.strip.include?(expectedText), "Footer text is not found")
  rescue
    if (footer == nil)
      assert(false, "Footer was not found on the page. Is Portal down?")
    end
  end
end