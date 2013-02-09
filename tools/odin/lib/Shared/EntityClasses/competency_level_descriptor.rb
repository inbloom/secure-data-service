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

# creates competency level descriptor
#
# from Ed-Fi-Core.xsd:
# <xs:complexType name="CompetencyLevelDescriptor">
#   <xs:annotation>
#     <xs:documentation>This descriptor defines various levels for assessed competencies.</xs:documentation>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="CodeValue" type="CodeValue">
#           <xs:annotation>
#             <xs:documentation>A code or abbreviation that is used to refer to a competency level.</xs:documentation>
#           </xs:annotation>
#         </xs:element>
#         <xs:element name="Description" type="Description" minOccurs="0">
#           <xs:annotation>
#             <xs:documentation>The description of the competency Level</xs:documentation>
#           </xs:annotation>
#         </xs:element>
#         <xs:element name="PerformanceBaseConversion" type="PerformanceBaseType" minOccurs="0">
#           <xs:annotation>
#             <xs:documentation>A conversion of the level to a standard set of competency levels.</xs:documentation>
#           </xs:annotation>
#         </xs:element>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class CompetencyLevelDescriptor < BaseEntity

  # required fields
  attr_accessor :code_value                  # maps to 'CodeValue'

  # optional fields
  attr_accessor :description                 # maps to 'Description'
  attr_accessor :performance_base_conversion # maps to 'PerformanceBaseConversion'

  def initialize(code_value, description, performance_base_conversion)
    @rand       = Random.new(code_value.size)
    @code_value = code_value
    
    optional { @description                 = description                 }
    optional { @performance_base_conversion = performance_base_conversion }
  end
end
