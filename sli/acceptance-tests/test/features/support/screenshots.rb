After('@screenshot') do |scenario|
    if (scenario.failed?)
        page.driver.browser.save_screenshot("#{scenario_id_}.png")
        embed("#{scenario._id_}.png", "image/png", "SCREENSHOT")
    end
end