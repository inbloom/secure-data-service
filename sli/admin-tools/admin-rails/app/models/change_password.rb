
class ChangePassword < SessionResource

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :old, :new, :confirmation
   
  validates_presence_of :old, :new, :confirmation
  validates :new, :confirmation => true #password_confirmation attr
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
    if self.new != self.confirmation
      errors[:base] << "New password and Confirmation password should match"
    end
    if self.new == self.old
      errors[:base] <<"Old password and New password should be different"
    end
  end
end
