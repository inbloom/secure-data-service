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

# Enumerates the different languages support by ed-fi. From Ed-Fi-Core.xsd:
# <xs:simpleType name="LanguageItemType">
#   <xs:annotation>
#     <xs:documentation>The item(s) denoting language(s) spoken or written.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Spanish"/>
#     <xs:enumeration value="Vietnamese"/>
#     <xs:enumeration value="Laotian (Lao)"/>
#     <xs:enumeration value="Cambodian (Khmer)"/>
#     <xs:enumeration value="Korean"/>
#     <xs:enumeration value="Japanese"/>
#     <xs:enumeration value="French"/>
#     <xs:enumeration value="German"/>
#     <xs:enumeration value="English"/>
#     <xs:enumeration value="Other languages"/>
#     <xs:enumeration value="Afrikaans (Taal)"/>
#     <xs:enumeration value="Akan (Fante, Asante)"/>
#     <xs:enumeration value="Albanian, Gheg (Kossovo/Macedonia)"/>
#     <xs:enumeration value="Albanian, Tosk (Albania)"/>
#     <xs:enumeration value="Algonquin"/>
#     <xs:enumeration value="Amharic"/>
#     <xs:enumeration value="Apache"/>
#     <xs:enumeration value="Arabic"/>
#     <xs:enumeration value="Armenian"/>
#     <xs:enumeration value="Assyrian (Syriac, Aramaic)"/>
#     <xs:enumeration value="Balinese"/>
#     <xs:enumeration value="Bengali"/>
#     <xs:enumeration value="Bosnian"/>
#     <xs:enumeration value="Bulgarian"/>
#     <xs:enumeration value="Burmese"/>
#     <xs:enumeration value="Cantonese (Chinese)"/>
#     <xs:enumeration value="Cebuano (Visayan)"/>
#     <xs:enumeration value="Chamorro"/>
#     <xs:enumeration value="Chaochow/Teochiu (Chinese)"/>
#     <xs:enumeration value="Cherokee"/>
#     <xs:enumeration value="Chippewa/Ojibawa/Ottawa"/>
#     <xs:enumeration value="Choctaw"/>
#     <xs:enumeration value="Comanche"/>
#     <xs:enumeration value="Coushatta"/>
#     <xs:enumeration value="Creek"/>
#     <xs:enumeration value="Croatian"/>
#     <xs:enumeration value="Crow"/>
#     <xs:enumeration value="Czech"/>
#     <xs:enumeration value="Danish"/>
#     <xs:enumeration value="Dard"/>
#     <xs:enumeration value="Dutch/Flemish"/>
#     <xs:enumeration value="Efik"/>
#     <xs:enumeration value="Eskimo"/>
#     <xs:enumeration value="Estonian"/>
#     <xs:enumeration value="Ethiopic"/>
#     <xs:enumeration value="Ewe"/>
#     <xs:enumeration value="Farsi (Persian)"/>
#     <xs:enumeration value="Finnish"/>
#     <xs:enumeration value="Fukien/Hokkien (Chinese)"/>
#     <xs:enumeration value="Gaelic (Irish)"/>
#     <xs:enumeration value="Gaelic (Scottish)"/>
#     <xs:enumeration value="Greek"/>
#     <xs:enumeration value="Gujarati"/>
#     <xs:enumeration value="Guyanese"/>
#     <xs:enumeration value="Hainanese (Chinese)"/>
#     <xs:enumeration value="Haitian-Creole"/>
#     <xs:enumeration value="Hakka (Chinese)"/>
#     <xs:enumeration value="Hausa"/>
#     <xs:enumeration value="Hebrew"/>
#     <xs:enumeration value="Hindi"/>
#     <xs:enumeration value="Hmong"/>
#     <xs:enumeration value="Hopi"/>
#     <xs:enumeration value="Hungarian"/>
#     <xs:enumeration value="Ibo/Igbo"/>
#     <xs:enumeration value="Icelandic"/>
#     <xs:enumeration value="Ilonggo (Hiligaynon)"/>
#     <xs:enumeration value="Indonesian"/>
#     <xs:enumeration value="Italian"/>
#     <xs:enumeration value="Kache (Kaje, Jju)"/>
#     <xs:enumeration value="Kannada (Kanarese)"/>
#     <xs:enumeration value="Kanuri"/>
#     <xs:enumeration value="Kashmiri"/>
#     <xs:enumeration value="Kickapoo"/>
#     <xs:enumeration value="Konkani"/>
#     <xs:enumeration value="Kpelle"/>
#     <xs:enumeration value="Krio"/>
#     <xs:enumeration value="Kurdish"/>
#     <xs:enumeration value="Kwa"/>
#     <xs:enumeration value="Latvian"/>
#     <xs:enumeration value="Lingala"/>
#     <xs:enumeration value="Lithuanian"/>
#     <xs:enumeration value="Luganda"/>
#     <xs:enumeration value="Lunda"/>
#     <xs:enumeration value="Luyia (Luhya)"/>
#     <xs:enumeration value="Macedonian"/>
#     <xs:enumeration value="Malay"/>
#     <xs:enumeration value="Malayalam"/>
#     <xs:enumeration value="Maltese"/>
#     <xs:enumeration value="Mandarin (Chinese)"/>
#     <xs:enumeration value="Mande"/>
#     <xs:enumeration value="Marathi"/>
#     <xs:enumeration value="Menominee"/>
#     <xs:enumeration value="Mien (Yao)"/>
#     <xs:enumeration value="Navajo"/>
#     <xs:enumeration value="Nepali"/>
#     <xs:enumeration value="Norwegian"/>
#     <xs:enumeration value="Okinawan"/>
#     <xs:enumeration value="Oneida"/>
#     <xs:enumeration value="Oriya"/>
#     <xs:enumeration value="Orri (Oring)"/>
#     <xs:enumeration value="Pampangan"/>
#     <xs:enumeration value="Panjabi (Punjabi)"/>
#     <xs:enumeration value="Pashto (Pushto)"/>
#     <xs:enumeration value="Pilipino (Tagalog)"/>
#     <xs:enumeration value="Pima"/>
#     <xs:enumeration value="Polish"/>
#     <xs:enumeration value="Portuguese"/>
#     <xs:enumeration value="Pueblo"/>
#     <xs:enumeration value="Romanian"/>
#     <xs:enumeration value="Romany (Gypsy)"/>
#     <xs:enumeration value="Russian"/>
#     <xs:enumeration value="Samoan"/>
#     <xs:enumeration value="Serbian"/>
#     <xs:enumeration value="Shanghai (Chinese)"/>
#     <xs:enumeration value="Shona"/>
#     <xs:enumeration value="Sikkimese"/>
#     <xs:enumeration value="Sindhi"/>
#     <xs:enumeration value="Sinhalese (Sri Lanka)"/>
#     <xs:enumeration value="Sioux (Dakota)"/>
#     <xs:enumeration value="Slavic"/>
#     <xs:enumeration value="Slovenian (Slovene)"/>
#     <xs:enumeration value="Somali"/>
#     <xs:enumeration value="Sotho"/>
#     <xs:enumeration value="Swahili"/>
#     <xs:enumeration value="Swedish"/>
#     <xs:enumeration value="Taiwanese/Formosan/Min Nan (Chinese)"/>
#     <xs:enumeration value="Tamil"/>
#     <xs:enumeration value="Telugu (Telegu)"/>
#     <xs:enumeration value="Thai"/>
#     <xs:enumeration value="Tibetan"/>
#     <xs:enumeration value="Tigrinya"/>
#     <xs:enumeration value="Tiwa"/>
#     <xs:enumeration value="Tuluau"/>
#     <xs:enumeration value="Turkish"/>
#     <xs:enumeration value="Ukrainian"/>
#     <xs:enumeration value="Urdu"/>
#     <xs:enumeration value="Welsh"/>
#     <xs:enumeration value="Winnebago"/>
#     <xs:enumeration value="Yiddish"/>
#     <xs:enumeration value="Yombe"/>
#     <xs:enumeration value="Yoruba"/>
#   </xs:restriction>
# </xs:simpleType>
class LanguageItemType
  include Enum

  LanguageItemType.define :CAMBODIAN_KHMER, "Cambodian (Khmer)"
  LanguageItemType.define :ENGLISH, "English"
  LanguageItemType.define :FRENCH, "French"
  LanguageItemType.define :GERMAN, "German"
  LanguageItemType.define :JAPANESE, "Japanese"
  LanguageItemType.define :KOREAN, "Korean"
  LanguageItemType.define :LAOTIAN_LAO, "Laotian (Lao)"
  LanguageItemType.define :SPANISH, "Spanish"
  LanguageItemType.define :VIETNAMESE, "Vietnamese"
end
