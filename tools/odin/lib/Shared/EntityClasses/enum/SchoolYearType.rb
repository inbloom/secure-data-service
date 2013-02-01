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

# Enumerates a limited set of school years. From Ed-Fi-Core.xsd:
# <xs:simpleType name="SchoolYearType">
#   <xs:annotation>
#     <xs:documentation>Identifier for a school year</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="1996-1997"/>
#     <xs:enumeration value="1997-1998"/>
#     <xs:enumeration value="1998-1999"/>
#     <xs:enumeration value="1999-2000"/>
#     <xs:enumeration value="2000-2001"/>
#     <xs:enumeration value="2001-2002"/>
#     <xs:enumeration value="2002-2003"/>
#     <xs:enumeration value="2003-2004"/>
#     <xs:enumeration value="2004-2005"/>
#     <xs:enumeration value="2005-2006"/>
#     <xs:enumeration value="2006-2007"/>
#     <xs:enumeration value="2007-2008"/>
#     <xs:enumeration value="2008-2009"/>
#     <xs:enumeration value="2009-2010"/>
#     <xs:enumeration value="2010-2011"/>
#     <xs:enumeration value="2011-2012"/>
#     <xs:enumeration value="2012-2013"/>
#     <xs:enumeration value="2013-2014"/>
#     <xs:enumeration value="2014-2015"/>
#     <xs:enumeration value="2015-2016"/>
#     <xs:enumeration value="2016-2017"/>
#     <xs:enumeration value="2017-2018"/>
#     <xs:enumeration value="2018-2019"/>
#     <xs:enumeration value="2019-2020"/>
#     <xs:enumeration value="2020-2021"/>
#     <xs:enumeration value="2021-2022"/>
#     <xs:enumeration value="2022-2023"/>
#     <xs:enumeration value="2023-2024"/>
#     <xs:enumeration value="2024-2025"/>
#     <xs:enumeration value="2025-2026"/>
#     <xs:enumeration value="2026-2027"/>
#     <xs:enumeration value="2027-2028"/>
#     <xs:enumeration value="2028-2029"/>
#     <xs:enumeration value="2029-2030"/>
#   </xs:restriction>
# </xs:simpleType>
class SchoolYearType
  include Enum

  SchoolYearType.define :START_1996, "1996-1997"
  SchoolYearType.define :START_1997, "1997-1998"
  SchoolYearType.define :START_1998, "1998-1999"
  SchoolYearType.define :START_1999, "1999-2000"
  SchoolYearType.define :START_2000, "2000-2001"
  SchoolYearType.define :START_2001, "2001-2002"
  SchoolYearType.define :START_2002, "2002-2003"
  SchoolYearType.define :START_2003, "2003-2004"
  SchoolYearType.define :START_2004, "2004-2005"
  SchoolYearType.define :START_2005, "2005-2006"
  SchoolYearType.define :START_2006, "2006-2007"
  SchoolYearType.define :START_2007, "2007-2008"
  SchoolYearType.define :START_2008, "2008-2009"
  SchoolYearType.define :START_2009, "2009-2010"
  SchoolYearType.define :START_2010, "2010-2011"
  SchoolYearType.define :START_2011, "2011-2012"
  SchoolYearType.define :START_2012, "2012-2013"
  SchoolYearType.define :START_2013, "2013-2014"
  SchoolYearType.define :START_2014, "2014-2015"
  SchoolYearType.define :START_2015, "2015-2016"
  SchoolYearType.define :START_2016, "2016-2017"
  SchoolYearType.define :START_2017, "2017-2018"
  SchoolYearType.define :START_2018, "2018-2019"
  SchoolYearType.define :START_2019, "2019-2020"
  SchoolYearType.define :START_2020, "2020-2021"
  SchoolYearType.define :START_2021, "2021-2022"
  SchoolYearType.define :START_2022, "2022-2023"
  SchoolYearType.define :START_2023, "2023-2024"
  SchoolYearType.define :START_2024, "2024-2025"
  SchoolYearType.define :START_2025, "2025-2026"
  SchoolYearType.define :START_2026, "2026-2027"
  SchoolYearType.define :START_2027, "2027-2028"
  SchoolYearType.define :START_2028, "2028-2029"
  SchoolYearType.define :START_2029, "2029-2030"
end
