require 'open-uri'

Given /^test type "([^\"]*)"$/ do |test_type|
  puts open("http://local.slidev.org:8081/shtick/crud-consistency?testType=#{test_type}").read
end