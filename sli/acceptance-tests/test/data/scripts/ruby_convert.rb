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



def HexToBase64(hex) 
     base64Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
     base64 = ""
     group
    for i in (0..30)
        group = parseInt(hex.slice(i, 6), 16)
        base64 += base64Digits[(group >> 18) & 0x3f]
        base64 += base64Digits[(group >> 12) & 0x3f]
        base64 += base64Digits[(group >> 6) & 0x3f]
        base64 += base64Digits[group & 0x3f]
    end
    group = parseInt(hex.slice(30, 2), 16)
    base64 += base64Digits[(group >> 2) & 0x3f]
    base64 += base64Digits[(group << 4) & 0x3f]
    base64 += "=="
    return base64
end

def Base64ToHex(base64) 
  base64Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
  hexDigits = "0123456789abcdef"
  hex = ""
  i = 0 
  while i < 24
    e1 = base64Digits.index(base64[i])
    i += 1                 
    e2 = base64Digits.index(base64[i])
    i += 1                 
    e3 = base64Digits.index(base64[i])
    i += 1                 
    e4 = base64Digits.index(base64[i])
    i += 1
    c1 = (e1 << 2) | (e2 >> 4)
    c2 = ((e2 & 15) << 4) | (e3 >> 2)
    c3 = ((e3 & 3) << 6) | e4
    hex += hexDigits[c1 >> 4]
    hex += hexDigits[c1 & 15]
    if (e3 != 64) 
      hex += hexDigits[c2 >> 4]
      hex += hexDigits[c2 & 15]
    end
    if (e4 != 64) 
      hex += hexDigits[c3 >> 4]
      hex += hexDigits[c3 & 15]
    end
  end
  hex
end
def JUUID(uuid) 
  hex = uuid.gsub(/[end-]/, "") # remove extra characters
  msb = hex.slice(0, 16)
  lsb = hex.slice(16, 16)
  msb = msb.slice(14, 2) + msb.slice(12, 2) + msb.slice(10, 2) + msb.slice(8, 2) + msb.slice(6, 2) + msb.slice(4, 2) + msb.slice(2, 2) + msb.slice(0, 2)
  lsb = lsb.slice(14, 2) + lsb.slice(12, 2) + lsb.slice(10, 2) + lsb.slice(8, 2) + lsb.slice(6, 2) + lsb.slice(4, 2) + lsb.slice(2, 2) + lsb.slice(0, 2)
  hex = msb + lsb
  base64 = HexToBase64(hex)
  base64
end

def toJUUID(base64) 
  hex = Base64ToHex(base64) # don't use BinData's hex def because it has bugs in older versions of the shell
  msb = hex.slice(0, 16)
  lsb = hex.slice(16, 16)
  msb = msb.slice(14, 2) + msb.slice(12, 2) + msb.slice(10, 2) + msb.slice(8, 2) + msb.slice(6, 2) + msb.slice(4, 2) + msb.slice(2, 2) + msb.slice(0, 2)
  lsb = lsb.slice(14, 2) + lsb.slice(12, 2) + lsb.slice(10, 2) + lsb.slice(8, 2) + lsb.slice(6, 2) + lsb.slice(4, 2) + lsb.slice(2, 2) + lsb.slice(0, 2)
  hex = msb + lsb
  uuid = hex.slice(0, 8) + '-' + hex.slice(8, 4) + '-' + hex.slice(12, 4) + '-' + hex.slice(16, 4) + '-' + hex.slice(20, 12)
  #return 'JUUID("' + uuid + '")'
  uuid
end


ARGV.each do |args|
  final = ""
  if args.end_with? "=="
    final = toJUUID args
  else
    final = JUUID args
  end
  puts final
end
