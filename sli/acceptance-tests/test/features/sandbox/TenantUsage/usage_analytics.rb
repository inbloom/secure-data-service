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

Then /^I see a list of "(.*?)" tenants$/ do |count|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert_equal(convert(count), @rows.size - 2, "Expected #{count}, received #{@rows.size-2}")
end

Then /^I see the total number of tenants as "(.*?)" at the bottom of the page$/ do |count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  collection_count = summary.find_element(:class_name, "tenant_metrics_collection_count")
end

Then /^I see the total data size is approximately "(.*?)"$/ do |data_size|
  summary = @driver.find_element(:class_name, "metrics_summary")
  size = summary.find_element(:class_name, "tenant_metrics_data_size").text
  assert_in_delta(data_size.to_f, size.to_f, delta(data_size.to_f.abs))
end

Then /^I see the total entity count is "(.*?)"$/ do |entity_count|
  summary = @driver.find_element(:class_name, "metrics_summary")
  count = summary.find_element(:class_name, "tenant_metrics_entity_count").text
  assert_equal(entity_count, count)
end

Then /^I see a row for tenantId <Tenant_id> with entity count <Count>, approximate size <Size>$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
     tenantId = hash["Tenant_id"]
     count = hash["Count"]
     size = hash["Size"]

     step "there is a row with tenantId \"" + tenantId + "\""
     step "I am displayed a column for mongo usage and number of records"
     step "the row for \"" + tenantId + "\" displays entity count \"" + count + "\""
     step "the row for \"" + tenantId + "\" displays size is approximately \"" + size + "\""
  end
end


Then /^there is a row with tenantId "([^"]*)"$/ do |id|
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  rows = @tranHistTable.find_elements(:tag_name, "tr")

  @driver.manage.timeouts.implicit_wait = 0.25

  f = false
  rows.each do |row|
     begin
         data = row.find_element(:class_name, "tenant_metrics_collection_name")
         if data.text == id
            f = true
            @tenant_data = row
            break
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

Then /^the row for "([^"]*)" displays entity count "(\d+)"$/ do |tenantId, count|
    assert_not_nil(@tenant_data, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_entity_count")
    assert_equal(count, data.text)

end

Then /^the row for "(.*?)" displays size is approximately "(.*?)"$/ do |tenantId, size|
    assert_not_nil(@tenant_data, "Metrics for this tenant not found.")
    data = @tenant_data.find_element(:class_name, "tenant_metrics_data_size")
    assert_in_delta(size.to_f, data.text.to_f, delta(size.to_f.abs))
end

When /^I click on the <Tenant_id> link$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
     @hash = hash
     url = PropLoader.getProps['admintools_server_url']+"/tenant_metrics/" + @hash['Tenant_id']
     @driver.get(url)
  end
end

Then /^I see a list of <Collection_Count> collections$/ do
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  @rows = @tranHistTable.find_elements(:tag_name, "tr")
  assert_equal(convert(@hash["Collection_Count"]), @rows.size - 2)
end

Then /^I see a row for collection <Collection> with entity count <CRecords>, approximate size <CSize>$/ do
  @tranHistTable = @driver.find_element(:id, "tenant-metrics-table")
  rows = @tranHistTable.find_elements(:tag_name, "tr")

  @driver.manage.timeouts.implicit_wait = 0.25

  f = false
  rows.each do |row|
     begin
         data = row.find_element(:class_name, "tenant_metrics_collection_name")
         if data.text == @hash["Collection"]
            f = true
            row_count = row.find_element(:class_name, "tenant_metrics_entity_count")
            assert_equal(@hash["CRecords"], row_count.text, "Wrong collection record count.")

            row_size = row.find_element(:class_name, "tenant_metrics_data_size")
            assert_in_delta(@hash["CSize"], row_size.text.to_f, delta(row_size.text.to_f))
            break
         end
     rescue
        next
     end
  end

  assert(f, "Did not find the expected collection: " + @hash["Collection"])
end

Then /^I see the total for the tenant is count <TCount>, approximate size <TSize>$/ do
  summary = @driver.find_element(:class_name, "metrics_summary")
  size = summary.find_element(:class_name, "tenant_metrics_data_size").text
  assert_in_delta(@hash["TSize"].to_f, size.to_f, delta(size.to_f.abs))

  count = summary.find_element(:class_name, "tenant_metrics_entity_count").text
  assert_equal(@hash["TCount"], count)
end



def delta(val)
  val * 0.5 + 200
end
