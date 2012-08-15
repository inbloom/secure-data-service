import json 
import os 
from codecs import open


def main():
    f_names = [x for x in os.listdir(".") if x.endswith(".json")]
    found_refs = set() 
    for fn in f_names: 
        lines = [x for x in open(fn, encoding='utf-8').readlines() if x.strip()]
        for l in lines: 
            rec = json.loads(l)
            rec_id = rec["_id"]
            for other in f_names:
                if other != fn: 
                    content = open(other, encoding='utf-8').read()
                    if content.find(rec_id) != -1:
                        found_refs.add((other, fn))
                        
        print fn, "done."
    print "-------------------------------------"
    for ref_from, ref_to in found_refs:
        print ref_from, "-->", ref_to

if __name__=="__main__":
    main() 
