from  aggregatedriver import cond_from_bands
import json 

bands = [
   { "rank" :  0, "description" : "no score",         "abbreviation" : "-", "min" :  0, "max" :  6 },
   { "rank" :  1, "description" : "warning",          "abbreviation" : "W", "min" :  6, "max" : 14 },
   { "rank" :  2, "description" : "below standard",   "abbreviation" : "B", "min" : 15, "max" : 20 },
   { "rank" :  3, "description" : "at standard",      "abbreviation" : "S", "min" : 21, "max" : 27 },
   { "rank" :  4, "description" : "exceeds standard", "abbreviation" : "E", "min" : 28, "max" : 33 }
]


result = cond_from_bands("score", bands)
print "JSON:\n" + json.dumps(result)
