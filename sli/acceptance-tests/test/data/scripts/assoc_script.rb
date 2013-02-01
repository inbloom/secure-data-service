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


require 'radix'

def make3CharString(i)
  intString = i.to_s
  retval = "00"+intString if intString.length == 1
  retval = "0"+intString  if intString.length == 2
  retval = intString      if intString.length == 3
  retval
end

def make4CharString(i)
  intString = i.to_s
  retval = "000"+intString if intString.length == 1
  retval = "00"+intString  if intString.length == 2
  retval = "0"+intString   if intString.length == 3
  retval = intString       if intString.length == 4
  retval
end

def HexToBase64(hex) 
  base64Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
  base64 = ""
  group = ""
  for i in [0,6,12,18,24]
    group = hex[i,6].to_i(16)
    base64 += base64Digits[(group >> 18) & 0x3f]
    base64 += base64Digits[(group >> 12) & 0x3f]
    base64 += base64Digits[(group >> 6) & 0x3f]
    base64 += base64Digits[group & 0x3f]
  end
  group = hex[30,2].to_i(16)
  base64 += base64Digits[(group >> 2) & 0x3f]
  base64 += base64Digits[(group << 4) & 0x3f]
  base64 += "==";
  return base64;
end

def JUUID(uuid)
  hex = uuid.gsub(/[-]+/, '')
  msb = hex[0, 16]
  lsb = hex[16, 16]
  msb = msb[14, 2] + msb[12, 2] + msb[10, 2] + msb[8, 2] + msb[6, 2] + msb[4, 2] + msb[2, 2] + msb[0, 2]
  lsb = lsb[14, 2] + lsb[12, 2] + lsb[10, 2] + lsb[8, 2] + lsb[6, 2] + lsb[4, 2] + lsb[2, 2] + lsb[0, 2]
  hex = msb + lsb;
  base64 = HexToBase64(hex);
  return base64
end


File.open("studentSectionAssocation_data_temp.json", 'w') do|f| 
  # add 3 chars at end to make string
  bareString =      "00000000-0000-0000-0000-000000000"
  otherBareString = "00000000-0000-0000-0000-00000000"
  idforAssoc = "00000000-0000-0000-0000-000000001000"
  idforStudent = "00000000-0000-0000-0000-000000000001"
  idsForSections = [
    "00000000-0000-0000-0000-000000000700",
    "00000000-0000-0000-0000-000000000701",
    "00000000-0000-0000-0000-000000000702",
    "00000000-0000-0000-0000-000000000703",
    "00000000-0000-0000-0000-000000000704"
  ]
  secIDCounter=0
  assocIDCounter=1000
  for i in (1..100)
    f.puts "{\"_id\":{\"$binary\":\""+ JUUID(otherBareString+assocIDCounter.to_s) +"\",\"$type\":\"03\"},\"type\":\"studentSectionAssociation\",\"body\":{\"repeatIdentifier\":\"Not_repeated\",\"studentId\":\""+ bareString+make3CharString(i) +"\",\"sectionId\":\""+ idsForSections[secIDCounter] +"\"},\"tenantId\":\"Zork\"}"
    secIDCounter+=1 if i%20 == 0
    assocIDCounter+=1
  end
end













