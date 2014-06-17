class UserAccountRegistration

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :email, :firstName, :lastName, :password, :vendor

  validates_presence_of :firstName, :lastName, :email, :password, :password_confirmation
  validates :email, :email => true
  validates :password, :confirmation => true #password_confirmation attr
  validates_presence_of :vendor unless APP_CONFIG['is_sandbox']

  def initialize(attributes = {})
    attributes.each { |name, value| send("#{name}=", value) } if attributes
  end

  def persisted?
    false
  end

  def register
    @vendor = 'None' if @vendor.blank?
    ApplicationHelper.add_user(self)
  end

  private
  def is_sandbox?
    APP_CONFIG['is_sandbox']
  end

end
