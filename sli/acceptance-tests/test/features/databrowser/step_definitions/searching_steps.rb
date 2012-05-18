Then /^I can search for <Type> with a <Field> and get a <Result>$/ do |table|
  table.hashes.each do |hash|
    select = @driver.find_element(:tag_name, "select")
    all_options = select.find_elements(:tag_name, "option")
    all_options.each do |option|
      if option.attribute("value") == hash["Type"]
        option.click
        break
      end
    end
    @driver.find_element(:id, "search_id").clear
    @driver.find_element(:id, "search_id").send_keys(hash["Field"])
    @driver.find_element(:css, "input[type='submit']").click
    errors = @driver.find_elements(:id, "flash")
    if hash["Result"] == "Pass"
      assert(errors.size == 0, "Should not be a flash error for #{hash["Type"]}/#{hash["Field"]}")
    else
      assert(errors.size == 1, "There should be an error message for #{hash["Type"]}/#{hash["Field"]}")
    end   
  end
end