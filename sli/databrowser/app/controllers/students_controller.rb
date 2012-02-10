require 'rest_client'
require 'json'
class StudentsController < ApplicationController

  def index
    @api_url = "https://devapp1.slidev.org/api/rest"

    @accept = {"Accept" => "application/json"}
    @studentData = []
    explore("#{@api_url}/home", "links", ["getSchools", nil, "links", "getStudents"])
    print("The student data is #{@studentData}\n")

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @studentData }
    end
  end

  def explore(url, searchFor, nextArray = [])
    print("\nStarted explore() the element to search for is #{searchFor} and the search Array is #{nextArray}, auth_id is #{SessionResource.auth_id}\n")
    res = RestClient.get("#{url}?sessionId=#{SessionResource.auth_id}", @accept)
    json = ActiveSupport::JSON.decode(res)
    print("JSON response was #{json} searching for #{searchFor}\n")
    if json.is_a?(Array) and searchFor == nil
      print("Going to search through whole array\n")
      found = json
    elsif(json.is_a?(Array))
      json.each do |element|
        if element["rel"] == searchFor
          found = element
        end
      end
    elsif json.is_a?(Hash)
      found = json[searchFor]
    end


    print("We found this #{found} when searching for #{searchFor}\n")
    nextName = nextArray.shift
    print("Next is now #{nextName} and the array is #{nextArray.inspect}\n")

    print("Found is an array #{found.is_a?(Array)} and next is Nil #{searchFor == nil}\n")
    if(found.is_a?(Array) and searchFor == nil)
      print("Going through the entire array\n")
      found.each do |element|
        print("Going to #{element["link"]["href"]}\n")
        explore(element["link"]["href"], nextName, nextArray)
      end
    elsif(found.is_a?(Array))
      print("We found an array, searching for #{nextName}\n")
      found.each do |curElement|
        if curElement["rel"] == nextName
          print("Found #{nextName} with link #{curElement["href"]}\n")
          if nextName == "getStudents"
            print("WE FOUND IT\n\n\n")
            nextArray.push nextName
            foundIt(curElement["href"])
          else
            print("There's an array and stuff\n")
            explore(curElement["href"], nextArray.shift, nextArray)
          end
        end
      end
    elsif found.is_a?(Hash)
      print("We found a hash\n")
    elsif found == nil
      print("We didn't find anything\n")
    else
      print("We found something else!\n")
    end
  end

  def foundIt(url)
    res = RestClient.get("#{url}?sessionId=#{SessionResource.auth_id}", @accept)
    print("The response was #{res}\n\n")
    @studentData.concat(ActiveSupport::JSON.decode(res))
  end

end
