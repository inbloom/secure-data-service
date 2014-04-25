class ForgotPassword

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :token, :new_pass, :confirmation

  validates_presence_of :token, :new_pass, :confirmation
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
  end

  def set_password
    user = APP_LDAP_CLIENT.read_user_resetkey(token)
    if !!user && valid?
      begin
        emailToken = user[:emailtoken]
        if emailToken.nil?
          currentTimestamp = DateTime.current.utc.to_i.to_s
          emailToken = Digest::MD5.hexdigest(SecureRandom.base64(10)+currentTimestamp+user[:email]+user[:first]+user[:last])
        end
        update_info = {
          :email => user[:email],
          :password => new_pass,
          :resetKey => '',
          :emailtoken => emailToken
        }
        response =  APP_LDAP_CLIENT.update_user_info(update_info)
        emailAddress = user[:emailAddress]
        fullName = user[:first] + " " + user[:last]
        yield(emailAddress, fullName)
        return true
      rescue InvalidPasswordException => e
        Rails.logger.error e.message
        Rails.logger.error e.backtrace.join("\n")
        APP_CONFIG['password_policy'].each { |msg|  errors.add(:new_pass, msg) }
      rescue Exception => e
        Rails.logger.error e.message
        Rails.logger.error e.backtrace.join("\n")
        errors.add(:base, "Unable to change password, please try again.")
      end
    end
    return false
  end

end
