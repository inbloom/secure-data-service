
class ChangePassword < SessionResource

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :old_pass, :new_pass, :confirmation
   
  validates_presence_of :old_pass, :new_pass, :confirmation
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new 
  
  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
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
      errors[:new_pass] <<"Old password and New password should be different."
    end
  end
end
