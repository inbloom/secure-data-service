require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

When /^I navigate to a countOnly "([^"]*)" with "([^"]*)"$/ do |arg1, arg2|
  @format = "application/vnd.slc+json"

  # we're going to add "countOnly=true" to the URI, which might
  # already have "?" and a parameter on it; determine whether we
  # need to use "?" or "&" to add our parameter. 
  separationCharacter = "?"
  if arg1.include? "?" 
    separationCharacter = "&"
  end

  # if arg1 includes a "#{id}", then substitute it, otherwise just use arg1 as the url
  if arg1.include? "/#\{id\}"
    url = arg1.gsub!(/#\{id\}/, arg2)
  else
    url = arg1
  end
  
  # add the global parameter for a countOnly query, and execute it.
  url = url << separationCharacter << "countOnly=true"
  restHttpGet(url)
end

Then /^I should get back just a count of ([^"]*)$/ do |count|
  # if any body is returned, that's an error
  assert @res.body == "[]"

#  if @res.raw_headers["totalcount"].nil?
#    puts "nil" << " and " << "#{count}"
#  else
#    puts @res.raw_headers["totalcount"] << "#{count}"
#  end

  # we get the count out of the HTTP header 'totalcount'
  # if it's not there we fail, otherwise compare it to the given count
  returnedCount = @res.raw_headers["totalcount"]
  if returnedCount.nil? 
    puts "no count returned"
    false
  else
    assert returnedCount[0].to_i == count.to_i
  end
end
