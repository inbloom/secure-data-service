After ('javascript') do |scenario|
    if (scenario.failed?)
        puts @driver.page_source.to_s
    end
end