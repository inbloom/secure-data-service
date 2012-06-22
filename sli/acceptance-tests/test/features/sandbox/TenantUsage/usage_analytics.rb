require "selenium-webdriver"
require "json"

require_relative "../../utils/sli_utils.rb"
require_relative "../../utils/selenium_common.rb"


Given /^I go to the "Tenant Usage Admin Tool"$/ do
  url = PropLoader.getProps['admintools_server_url']+"/tenant_metrics"
  @driver.get(url)
end

Then /^I am displayed a list of "(\d+)" tenants$/ do |count|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert(@rows.size - 2 == convert(count), "Expected #{count}, received #{@rows.size-2}")
end

Then /^there is a row with tenantId "([^"]*)"$/ do |id|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  rows = @tranHistTable.find_elements(:tag_name, "tr")

  f = false
  rows.each do |row|
     begin
         data = row.find_element(:class_name, "tenant_metrics_collection_name")
         if data.text == id
            f = true
            @tenant_data = row
         end
     rescue
        next
     end
  end

  assert(f == true, "Did not find the expected tenant: " + id)
end

Then /^I am displayed a column for mongo usage and number of records$/ do
  assert(@driver.find_element(:class_name, "metrics_summary") != nil, "Did not find the tenant metric summary row.")
end

Then /^I am displayed the total number of tenants as "(\d+)" at the bottom of the page$/ do |count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  collection_count = summary.find_element(:class_name, "tenant_metrics_collection_count")

  assert(collection_count != nil, "Did not find a collection count.")
  assert(collection_count.text == count)
end

Then /^the row for "([^"]*)" displays entity count "(\d+)"$/ do |tenantId, count|
    assert(@tenant_data != nil, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_entity_count")
    assert(data.text == count)

end

Then /^the row for "(.*?)" displays size greater than "(.*?)"$/ do |tenantId, size|
    assert(@tenant_data != nil, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_data_size")
    assert(data.text >= size)
end

Then /^I am displayed the total data size is greater than "(.*?)"$/ do |data_size|
  summary = @driver.find_element(:class_name, "metrics_summary")
  size = summary.find_element(:class_name, "tenant_metrics_data_size").text
  assert(size >= data_size, "Expecting size: " + data_size + ", found size: " + size)
end

Then /^I am displayed the total entity count as "(.*?)"$/ do |entity_count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  count = summary.find_element(:class_name, "tenant_metrics_entity_count").text
  assert(count == entity_count)
end

When /^I click on the "(.*?)" link$/ do |tenantId|
    url = PropLoader.getProps['admintools_server_url']+"/tenant_metrics/" + tenantId
    @driver.get(url)
end

Then /^I am displayed a list of "(\d+)" collections$/ do |count|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert(@rows.size - 2 == convert(count), "Expected #{count}, received #{@rows.size-2}")
end

Then /^next to "(.*?)" is the entity count "(\d+)"$/ do |name, count|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  rows = @tranHistTable.find_elements(:tag_name, "tr")

  f = false
  data = nil
  rows.each do |row|
     begin
         data = row.find_element(:class_name, "tenant_metrics_collection_name")
         if data.text == name
            f = true
            row_count = row.find_element(:class_name, "tenant_metrics_entity_count")
            assert(row_count.text == count, "Wrong collection record count.")
            break
         end
     rescue
        next
     end
  end

  assert(f == true, "Did not find the expected collection: " + name)
end

Then /^next to "(.*?)" is size greater than "(.*?)"$/ do |name, size|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  rows = @tranHistTable.find_elements(:tag_name, "tr")

  f = false
  data = nil
  rows.each do |row|
     begin
         data = row.find_element(:class_name, "tenant_metrics_collection_name")
         if data.text == name
            f = true
            row_size = row.find_element(:class_name, "tenant_metrics_data_size")
            assert(row_size.text >= size, "Wrong collection data size.")
            break
         end
     rescue
        next
     end
  end

  assert(f == true, "Did not find the expected collection: " + name)
end
