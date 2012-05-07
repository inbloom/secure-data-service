class UserAccountRegistration
    include ActiveModel::Validations
    include ActiveModel::Conversion
    extend ActiveModel::Naming

   attr_accessor :email, :firstName, :lastName, :password, :vendor

   validates_presence_of :firstName, :email,:lastName
   validates_format_of :email, :with => /^[-a-z0-9_+\.]+\@([-a-z0-9]+\.)+[a-z0-9]{2,4}$/i

   def initialize(attributes = {})
       attributes.each do |name, value|
         send("#{name}=", value)
       end
     end

     def persisted?
       false
     end
end
