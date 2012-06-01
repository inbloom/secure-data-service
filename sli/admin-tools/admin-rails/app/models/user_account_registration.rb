class EmailValidator < ActiveModel::EachValidator
  def validate_each(record, attribute, value)
    if not value =~ /^[-a-z0-9_+\.]+\@([-a-z0-9]+\.)+[a-z0-9]{2,4}$/i
      record.errors[attribute] << "Please enter a valid email address"
    else
      record.errors[attribute] << "An account with this email already exists" if ApplicationHelper.user_exists? value
    end
  end
end

class UserAccountRegistration

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :email, :firstName, :lastName, :password, :vendor, :confirmation

  validates_presence_of :firstName, :lastName, :password, :confirmation
  validates :email, :presence => true, :email => true
  validates :password, :confirmation => true #password_confirmation attr
  validates_presence_of :vendor unless APP_CONFIG["is_sandbox"]

  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
    end
  end

  def persisted?
    false
  end
  
private
  def is_sandbox?
    APP_CONFIG["is_sandbox"]
  end

  
end
