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



class ForgotPassword 

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :token, :new_pass, :confirmation
   
  validates_presence_of :token, :new_pass, :confirmation
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new 
  
  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
    end
  end
  
  def set_token(token) 
    self.token = token
  end
  
  def persisted?
    false
  end
    
  def confirm_new
    if self.new_pass != self.confirmation
      errors[:new_pass] << "New passwords do not match."
    end
  end
end
