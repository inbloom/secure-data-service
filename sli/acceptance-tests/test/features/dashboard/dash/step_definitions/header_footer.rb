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