require "selenium-webdriver"
require "json"
require "test/unit"

require_relative "../../utils/sli_utils.rb"
require_relative "../../utils/selenium_common.rb"

Before do
  extend Test::Unit::Assertions
end

Given /^I go to the "Tenant Usage Admin Tool"$/ do
  url = PropLoader.getProps['admintools_server_url']+"/tenant_metrics"
  @driver.get(url)
end

Then /^I am displayed a list of "(\d+)" tenants$/ do |count|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert_equal(convert(count), @rows.size - 2, "Expected #{count}, received #{@rows.size-2}")
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

  assert(f, "Did not find the expected tenant: " + id)
end

Then /^I am displayed a column for mongo usage and number of records$/ do
  assert_not_nil(@driver.find_element(:class_name, "metrics_summary"), "Did not find the tenant metric summary row.")
end

Then /^I am displayed the total number of tenants as "(\d+)" at the bottom of the page$/ do |count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  collection_count = summary.find_element(:class_name, "tenant_metrics_collection_count")

  assert_not_nil(collection_count, "Did not find a collection count.")
  assert_equal(count, collection_count.text)
end

Then /^the row for "([^"]*)" displays entity count "(\d+)"$/ do |tenantId, count|
    assert(@tenant_data != nil, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_entity_count")
    assert_equal(count, data.text)

end

Then /^the row for "(.*?)" displays size "(.*?)"$/ do |tenantId, count|
    assert(@tenant_data != nil, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_data_size")
    assert_in_delta(count.to_f, data.text.to_f, count.to_f.abs * 0.1 + 5)
end

Then /^I am displayed the total data size as "(.*?)"$/ do |data_size|
  summary = @driver.find_element(:class_name, "metrics_summary")
  size = summary.find_element(:class_name, "tenant_metrics_data_size").text
  assert_in_delta(data_size.to_f, size.to_f, data_size.to_f.abs * 0.1 + 5)
end

Then /^I am displayed the total entity count as "(.*?)"$/ do |entity_count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  count = summary.find_element(:class_name, "tenant_metrics_entity_count").text
  assert_equal(entity_count, count)
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

Then /^next to "(.*?)" is size "(.*?)"$/ do |name, size|
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
            assert(row_size.text == size, "Wrong collection data size.")
            break
         end
     rescue
        next
     end
  end

  assert(f == true, "Did not find the expected collection: " + name)
end

Then /^the total entity data size is "(.*?)"$/ do |count|
  pending # express the regexp above with the code you wish you had
end

Then /^the total entity count is "(.*?)"$/ do |count|
  pending # express the regexp above with the code you wish you had
end
