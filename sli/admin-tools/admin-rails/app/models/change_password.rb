=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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



class ChangePassword < SessionResource

  attr_accessor :old_pass, :new_pass, :confirmation

  validates_presence_of :old_pass, :new_pass, :confirmation
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new

  def initialize(attributes = {})
    unless attributes.nil?
      attributes.each do |name, value|
        send("#{name}=", value)
      end
    end
  end

  def persisted?
    false
  end

  def confirm_new
    if self.new_pass != self.confirmation
      errors[:new_pass] << "New passwords do not match."
    end
    if self.new_pass == self.old_pass
      errors[:new_pass] << "The new password must be different from the old password."
    end
  end
end
