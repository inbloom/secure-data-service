#!/usr/bin/env ruby

# Copyright 2013 inBloom, Inc. and its affiliates.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-

# mongo_data_profile.rb
#
# This script prfiles data in a mongo database, counting documents and field occurrences

# Usage: mongo_data_profile.rb
#        mongo_data_profile.rb [<db>] ...
#        mongo_data_profile.rb [<db>:<collection1>,<collection2>] ...
#        
# With no database, lists all the databases and their collection counts.
# With a database argument, shows all collections in that database.
# Database name may be optionally followed by a colon (":") and a
# comma-separated list of collections in that database
#
# For each document, the keys and values are summarized, showing
# counts by the type of data elements that occur.  For documents of
# consistent structure, there will usually be only one data type and
# associated count for each field.
#
# The script will recognize encrypted types and string types that
# appear to be Hex IDs
#
# Where arrays occur, a count of the number of arrays occurring in the
# field is shown, together with statistics on the minumum size, maximum
# size, and total elements across all the array occurrences.  Then,
# total statistics across all the array elements is given under the
# pseudo-field "ALL_ELEMENTS"
#


require 'mongo'
require 'pp'

COUNT_TAG = "__count"
ELEMENTS_TAG = "ALL_ELEMENTS"

# Main driver
def main(argv)
  host = "localhost"
  conn = Mongo::Connection.new(host)
  dbs = argv

  # Tags to ignore throughout
  ignore_tags = {}

  # If no args, just show all databases
  if dbs.empty?
    dbs = conn.database_names.sort()
    puts "Databases on server '" + host + "':"
    for dbname in dbs
      puts "    " + db_summary(conn, dbname)
    end

    puts "\nTo data profile specific database(s) or collections, give argument(s) in any combination and order as follows:\n"
    puts "     <dbname>                    Profile all collections in <dbname>\n"
    puts "     <dbname>:<coll1>,<coll2>    Profile collections <coll1> and <coll2. in <dbname>\n"
    puts "     -<tag1> -<tag2>             Ignore <tag1>, <tag2> and all their contents\n"
    puts "\n"
    return
  end
  
  # conn.database_info.each { |info| puts info.inspect }
  for dbname_and_colls in dbs

    # See if an "ignore" tag
    if dbname_and_colls[0] == "-"
      to_ignore = dbname_and_colls[1, dbname_and_colls.length - 1]
      ignore_tags[to_ignore] = true
      next
    end

    # Split to see if specific collection(s) given
    inf = dbname_and_colls.split(/:+/)
    dbname = inf.shift()
    coll_list = inf
    colls = []
    if not coll_list.empty?
      colls = coll_list[0].split(/,+/)
    end

    puts db_summary(conn, dbname)
    db = conn.db(dbname)
    if colls.empty?
      colls = db.collection_names.sort()
    end
    for collname in colls
      coll = db[collname]
      ndoc = coll.count
      if 0 == ndoc
        countstr = "empty"
      elsif 1 == ndoc
        countstr = "1 doc"
      else
        countstr = ndoc.to_s() + " docs"
      end
      puts "    " + collname + " (" + countstr + ")"
      summary_data = analyze(coll, ignore_tags)
      # PP.pp(summary_data)
      summary_data.delete(COUNT_TAG)
      print_hash(summary_data, 2)
    end
  end
end

# Analyze data in collection and return summary data
def analyze(coll, ignore_tags)
  summary = {}
  coll.find({}, :timeout => false) do |cursor|
    cursor.each do |document|
      # Process
      # puts document.to_s()
      summarize(summary, document, ignore_tags)
    end
  end  
  return summary
end

# Accumulate summary from document
def summarize(summary, val, ignore_tags)

  # Accumulate counts by data type
  if not summary.has_key?(COUNT_TAG)
    summary[COUNT_TAG] = {}
  end
  counts = summary[COUNT_TAG]

  vtype = get_type(val)
  inc_count(counts, vtype, val)

  # Handle composite types (hash, 
  if vtype == "BSON::OrderedHash"
    # Maintain summary stats on hash sizes
    val.each do |k, v|
      if ignore_tags.has_key?(k)
        next
      end
      if not summary.has_key?(k)
        summary[k] = {}
      end
      summarize(summary[k], v, ignore_tags)
    end
  elsif vtype == "Array"
    # Maintain summary stats on array sizes

    if not summary.has_key?(ELEMENTS_TAG)
      summary[ELEMENTS_TAG] = {}
    end
    val.each do |v|
      summarize(summary[ELEMENTS_TAG], v, ignore_tags)
    end
  end
end

# Print hash, indented
def print_hash(h, indent)
  # print "print_hash called on"
  # PP.pp(h)
  # print "doing keys bnd avlues"
  h.sort.each do |k, v|
    if v.empty?
      next
    end
    # print "doing key '" + k.to_s() + "' and value '" + v.to_s() + "'"
    print "    " * indent + k + ":" + " " + counts_summary(v) + "\n"
    v.delete(COUNT_TAG)
    print_hash(v, indent + 1)
  end
end

# Counts summary
def counts_summary(v)
  h = v[COUNT_TAG]
  result = ""
  h.each do |typ, inf|
    if result.length > 0
      result += ", "
    end
    if typ == "Array"
      cnt, min, max, total = inf
      if min < 0
        minstr = ""
      else
        minstr = ", minelt=" + min.to_s()
      end
      if max < 0
        maxstr = ""
      else
        maxstr = ", maxelt=" + max.to_s()
      end
      result += "count=" + cnt.to_s() + minstr + maxstr + ", totalelt=" + total.to_s() + " [" + typ + "]"
    else
      result += inf.to_s() + " [" + typ + "]"
    end
  end
  return result
end

# Increment count and collect stats for types
def inc_count(counts, k, v)
  # Handle array
  if ( k == "Array" )
    if counts.has_key?(k)
      cnt, min, max, total = counts[k]
    else
      cnt, min, max, total = [ 0, -1, -1, 0 ]
    end
    
    cnt +=1
    n = v.length
    if min < 0 or n < min
      min = n
    end
    if max < 0 or n > max
      max = n
    end
    total += n
    counts[k] = [ cnt, min, max, total ]
  else
    if counts.has_key?(k)
      counts[k] += 1
    else
      counts[k] = 1
    end
  end
end

# Database and collection count info
def db_summary(conn, dbname)
  return dbname + " (" + conn.db(dbname).collection_names.length.to_s() + " colls)"
end

# Get data type
def get_type(val)
  typ = val.class.name
  if typ == "String"
    if starts_with?(val, "ESTRING:")
      return "Encrypted String"
    elsif starts_with?(val, "EBOOL:")
      return "Encrypted Bool"
    elsif val.length == 43 and val.match(/^[a-f0-9]{40}_id$/)
      return "Entity ID '%40x_id'"
    elsif val.length == 86 and val.match(/^[a-f0-9]{40}_id[a-f0-9]{40}_id$/)
      return "SubDoc ID '%40x_id%40x_id'"
    elsif val.length == 36 and val.match(/^[a-f0-9]{8}\-[a-f0-9]{4}\-[a-f0-9]{4}\-[a-f0-9]{4}\-[a-f0-9]{12}$/)
      return "GUID '%8x-%4x-%4x-%4x-%12x'"
    end
  end
  return typ
end    

def starts_with?(s, prefix)
  s[0, prefix.length] == prefix
end

# Run it
main(ARGV)
