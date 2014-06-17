class NewAccountPassword < ForgotPassword

  validates_presence_of :token
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new
  validate :accept_tou

  # attributes passed to the template to render 
  attr_accessor :inviter, :edorg, :terms_and_conditions, :tou_required

  def accept_tou
    if self.tou_required && self.terms_and_conditions != '1'
      self.errors[:terms_and_conditions] << 'must be accepted'
    end
  end
end
