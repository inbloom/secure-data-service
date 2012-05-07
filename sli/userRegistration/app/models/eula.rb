class Eula
    include ActiveModel::Validations
    include ActiveModel::Conversion
    extend ActiveModel::Naming

   attr_accessor :termsOfService, :eula, :loremIpsum

   validates_presence_of :termsOfService,:eula, :loremIpsum

   def initialize(attributes = {})
       attributes.each do |name, value|
         send("#{name}=", value)
       end
     end

     def persisted?
       false
     end
end

