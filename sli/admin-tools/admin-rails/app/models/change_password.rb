class ChangePassword < SessionResource

  attr_accessor :old_pass, :new_pass, :confirmation

  validates_presence_of :old_pass, :new_pass, :confirmation
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new

  def initialize(attributes = {})
    attributes.each {|name, value| send("#{name}=", value)} if attributes
  end

  def persisted?
    false
  end

  def confirm_new
    errors[:new_pass] << 'New passwords do not match.' if new_pass != confirmation
    errors[:new_pass] << 'The new password must be different from the old password.' if new_pass == old_pass
  end
end
