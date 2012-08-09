import sys
import re
import json
import base64
from binascii import a2b_base64,hexlify

# cd sli/acceptance-tests
# find . -name *.json | xargs -I file python ~/wgen/SLI/sli/config/scripts/sharding/binary-to-string.py file

fixture_lines = []

if len(sys.argv) == 2:
    fixture_file = open(sys.argv[1])
    fixture_lines = fixture_file.read().splitlines()
    fixture_file.close()
else:
    exit(0)

def toJUUID(base64str):
    hexstr = hexlify(a2b_base64(base64str))
    msb = hexstr[:16]
    lsb = hexstr[16:32]

    msb = msb[14:16] + msb[12:14] + msb[10:12] + msb[8:10] + msb[6:8] + msb[4:6] + msb[2:4] + msb[0:2]
    lsb = lsb[14:16] + lsb[12:14] + lsb[10:12] + lsb[8:10] + lsb[6:8] + lsb[4:6] + lsb[2:4] + lsb[0:2]
    hexstr = msb+lsb

    return hexstr[0:8] + "-" + hexstr[8:12] + "-" + hexstr[12:16] + "-" + hexstr[16:20] + "-" + hexstr[20:32]


id_regex = re.compile(r'{\s*"\$binary"\s*:\s*"([a-zA-Z0-9=/+]*)"\s*,\s*"\$type"\s*:\s*"03"\s*}')
outfile = open(sys.argv[1], 'w')
for line in fixture_lines:
    match = id_regex.search(line)
    #print "%s | %s" % (match.group(1), base64.b64decode(match.group(1).encode()))
    #print "%s | %s" % (match.group(1), hexlify(a2b_base64(match.group(1))))
    #print "%s - %s" % (match.group(1), toJUUID(match.group(1)))
    if match != None:
        outfile.write(line.replace(match.group(0), '"' + toJUUID(match.group(1)) + '"') + '\n')
    if match == None:
        outfile.write(line + '\n')
outfile.close()
