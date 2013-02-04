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

require_relative 'Enum.rb'

# Enumerates the types of assessment item response indicators. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ResponseIndicatorType">
#   <xs:annotation>
#     <xs:documentation>Indicator of the response.  For example:
#     Nonscorable response
#     Ineffective response
#     Effective response
#     Partial response
#     ...
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Nonscorable response"/>
#     <xs:enumeration value="Ineffective response"/>
#     <xs:enumeration value="Effective response"/>
#     <xs:enumeration value="Partial response"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentItemResponseType
  include Enum

  AssessmentItemResponseType.define :EFFECTIVE_RESPONSE, "Effective response"
  AssessmentItemResponseType.define :INEFFECTIVE_RESPONSE, "Ineffective response"
  AssessmentItemResponseType.define :NONSCORABLE_RESPONSE, "Nonscorable response"
  AssessmentItemResponseType.define :PARTIAL_RESPONSE, "Partial response"
end
