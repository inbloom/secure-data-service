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


require_relative './baseEntity.rb'

class Student < BaseEntity
  
  def addZeroIfSingleDigit(num)
    s = num < 10 ? "0" + num.to_s : num.to_s
    s
  end

  def initialize(id, rand)
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
    addZeroIfSingleDigit(1 + @rand.rand(11)) + "-" + addZeroIfSingleDigit(1 + @rand.rand(27))
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
    choose(['Free', 'Full price', 'Reduced price', 'Unknown'])
  end

  def limitedEnglish
    choose(['Limited', 'Limited Monitored 1', 'Limited Monitored 2', 'NotLimited'])
  end

  def race
    choose(['American Indian - Alaskan Native', 'Asian', 'Black - African American', 'Native Hawaiian - Pacific Islander', 'White'])
  end

end
