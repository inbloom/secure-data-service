require 'rest_client'
require 'json'
class StudentsController < ApplicationController

  def index
    @api_url = "https://devapp1.slidev.org/api/rest"

    @accept = {"Accept" => "application/json"}
    @studentData = []
    explore("#{@api_url}/home", "links", ["getSchools", nil, "links", "getStudents"])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @studentData }
    end
  end

  def explore(url, searchFor, nextArray = [])
    res = RestClient.get("#{url}?sessionId=#{SessionResource.auth_id}", @accept)
    json = ActiveSupport::JSON.decode(res)
    if json.is_a?(Array) and searchFor == nil
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


    nextName = nextArray.shift
    if(found.is_a?(Array) and searchFor == nil)
      found.each do |element|
        explore(element["link"]["href"], nextName, nextArray)
      end
    elsif(found.is_a?(Array))
      found.each do |curElement|
        if curElement["rel"] == nextName
          if nextName == "getStudents"
            nextArray.push nextName
            foundIt(curElement["href"])
          else
            explore(curElement["href"], nextArray.shift, nextArray)
          end
        end
      end
    end
  end

  def foundIt(url)
    res = RestClient.get("#{url}?sessionId=#{SessionResource.auth_id}", @accept)
    @studentData.concat(ActiveSupport::JSON.decode(res))
  end

end
