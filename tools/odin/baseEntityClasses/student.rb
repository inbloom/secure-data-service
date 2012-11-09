=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'mustache'
require './baseEntity.rb'

class InterchangeStudent < Mustache

  def initialize(count)
    @count = count
  end

  def students
    #(0..@count).each.map{|i| Student.new(i)}
    (0..@count).to_a
  end

  def gen_student
    lambda do |id|
      student = Student.new id
      student.render
    end
  end

end

class Student < BaseEntity

  def choose(options)
    options[@rand.rand(options.size) - 1]
  end
  
  def initialize(id, rand = Random.new)
    @id = id
    @rand = rand
  end

  def id
    @id
  end

  def firstName
    choose(["Joe", "Bob", "Sally"])
  end

  def lastName
    choose(["Brown", "Johnson", "Smith"])
  end

  def sex
    choose(["Male", "Female"])
  end

  def birthYear
    "2000"
  end

  def birthDay
    @rand.rand(12).to_s + "-" + @rand.rand(28).to_s
  end

  def address
    @rand.rand(999).to_s + " " + choose(["North Street", "South Lane", "East Rd", "West Blvd"])
  end

  def city
    "New York"
  end

  def state
    "NY"
  end

  def postalCode
    "10292"
  end

  def email
    @rand.rand(10000).to_s + "@fakemail.com"
  end

  def hispanicLatino
    choose([true, false])
  end

  def economicDisadvantaged
    choose([true, false])
  end

  def schoolFood
    choose(["Free", "Full Price", "Reduced Price", "Unknown"])
  end

  def limitedEnglish
    choose([true, false])
  end

end
