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

# Enumerates the types of incident behaviours. From Ed-Fi-Core.xsd:
# <xs:simpleType name="IncidentBehaviorType">
#   <xs:annotation>
#     <xs:documentation>Enumeration items defining the behavior coded for use in describing an incident.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Alcohol"/>
#     <xs:enumeration value="Arson"/>
#     <xs:enumeration value="Attendance Policy Violation"/>
#     <xs:enumeration value="Battery"/>
#     <xs:enumeration value="Burglary/Breaking and Entering"/>
#     <xs:enumeration value="Disorderly Conduct"/>
#     <xs:enumeration value="Drugs Excluding Alcohol and Tobacco"/>
#     <xs:enumeration value="Fighting"/>
#     <xs:enumeration value="Harassment, Nonsexual"/>
#     <xs:enumeration value="Harassment, Sexual"/>
#     <xs:enumeration value="Homicide"/>
#     <xs:enumeration value="Inappropriate Use of Medication"/>
#     <xs:enumeration value="Insubordination"/>
#     <xs:enumeration value="Kidnapping"/>
#     <xs:enumeration value="Obscene Behavior"/>
#     <xs:enumeration value="Physical Altercation, Minor"/>
#     <xs:enumeration value="Robbery"/>
#     <xs:enumeration value="School Threat"/>
#     <xs:enumeration value="Theft"/>
#     <xs:enumeration value="Threat/Intimidation"/>
#     <xs:enumeration value="Tobacco Possession or Use"/>
#     <xs:enumeration value="Trespassing"/>
#     <xs:enumeration value="Vandalism"/>
#     <xs:enumeration value="Violation of School Rules"/>
#     <xs:enumeration value="Weapons Possession"/>
#     <xs:enumeration value="Harassment or bullying on the basis of disability"/>
#     <xs:enumeration value="Harassment or bullying on the basis of race, color, or national origin"/>
#     <xs:enumeration value="Harassment or bullying on the basis of sex"/>
#   </xs:restriction>
# </xs:simpleType>
class IncidentBehaviorType
  include Enum

  IncidentBehaviorType.define :ALCOHOL, "Alcohol"
  IncidentBehaviorType.define :ARSON, "Arson"
  IncidentBehaviorType.define :ATTENDANCE_POLICY_VIOLATION, "Attendance Policy Violation"
  IncidentBehaviorType.define :BATTERY, "Battery"
  IncidentBehaviorType.define :BURGLARY_BREAKING_AND_ENTERING, "Burglary/Breaking and Entering"
  IncidentBehaviorType.define :DISORDERLY_CONDUCT, "Disorderly Conduct"
  IncidentBehaviorType.define :DRUGS_EXCLUDING_ALCOHOL_AND_TOBACCO, "Drugs Excluding Alcohol and Tobacco"
  IncidentBehaviorType.define :FIGHTING, "Fighting"
  IncidentBehaviorType.define :HARASSMENT_NONSEXUAL, "Harassment, Nonsexual"
  IncidentBehaviorType.define :HARASSMENT_OR_BULLYING_ON_BASIS_OF_DISABILITY, "Harassment or bullying on the basis of disability"
  IncidentBehaviorType.define :HARASSMENT_OR_BULLYING_ON_BASIS_OF_RACE_COLOR_OR_ORIGIN, "Harassment or bullying on the basis of race, color, or national origin"
  IncidentBehaviorType.define :HARASSMENT_OR_BULLYING_ON_BASIS_OF_SEX, "Harassment or bullying on the basis of sex"
  IncidentBehaviorType.define :HARASSMENT_SEXUAL, "Harassment, Sexual"
  IncidentBehaviorType.define :HOMOCIDE, "Homicide"
  IncidentBehaviorType.define :INAPPROPRIATE_USE_OF_MEDICATION, "Inappropriate Use of Medication"
  IncidentBehaviorType.define :INSUBORDINATION, "Insubordination"
  IncidentBehaviorType.define :KIDNAPPING, "Kidnapping"
  IncidentBehaviorType.define :OBSCENE_BEHAVIOR, "Obscene Behavior"
  IncidentBehaviorType.define :PHYSICAL_ALTERCATION_MINOR, "Physical Altercation, Minor"
  IncidentBehaviorType.define :ROBBERY, "Robbery"
  IncidentBehaviorType.define :SCHOOL_THREAT, "School Threat"
  IncidentBehaviorType.define :THEFT, "Theft"
  IncidentBehaviorType.define :THREAT_INTIMIDATION, "Threat/Intimidation"
  IncidentBehaviorType.define :TOBACCO_POSSESSION_OR_USE, "Tobacco Possession or Use"
  IncidentBehaviorType.define :TRESPASSING, "Trespassing"
  IncidentBehaviorType.define :VANDALISM, "Vandalism"
  IncidentBehaviorType.define :VIOLATION_OF_SCHOOL_RULES, "Violation of School Rules"
  IncidentBehaviorType.define :WEAPONS_POSSESSION, "Weapons Possession"
end
