#!/usr/bin/env ruby

# Profile data in a mongo database, counting documents and field occurrences

# Usage: mongo_data_profile [<database>]

# With no database, lists all the databases.
# With a database argument, shows collections in that database

require 'mongo'
require 'pp'

# Main driver
def main(argv)
  host = "localhost"
  conn = Mongo::Connection.new(host)
  dbs = argv
  if 0 == dbs.length
    dbs = conn.database_names.sort()
    puts "Databases on server '" + host + "':"
    for dbname in dbs
      puts "    " + dbname + " (" + conn.db(dbname).collection_names.length.to_s() + " colls)"
    end
    return
  end
  
  # conn.database_info.each { |info| puts info.inspect }
  for dbname in dbs
    puts dbname
    db = conn.db(dbname)
    colls = db.collection_names.sort()
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
      print_hash(analyze(coll), 2)
    end
  end
end

# Analyze data in collection
def analyze(coll)
  summary = {}
  coll.find({}, :timeout => false) do |cursor|
    cursor.each do |document|
      # Process
      # puts document.to_s()
      summarize(summary, document)
    end
  end  
  return summary
end

# Accumulate summary from document
def summarize(summary, document)
  document.each do |k, kval|

    count_tag = "__count"
    # Count up by types
    if not summary.has_key?(k)
      summary[k] = { count_tag => {} }
    end
    counts = summary[k][count_tag]

    kvtype = kval.class.name
    inc_count(counts, kvtype)

    is_array = (kvtype == "Array")
    if is_array
      vals = kval
    else
      vals = [ kval ]
    end
    
    vals.each do |v|
      vtype = v.class.name

      if is_array
        inc_count(counts, vtype)
      end
      
      # If a hash, add subkeys in summary
      if vtype == "BSON::OrderedHash"
        summarize(summary[k], v)
      end
    end
  end
end

# Print hash, indented
def print_hash(h, indent)
  h.each do |k, v|
    print "    " * indent + k + ":" + " " + counts_summary(v) + "\n"
    count_tag = "__count"
    v.delete(count_tag)
    print_hash(v, indent + 1)
  end
end

# Counts summary
def counts_summary(v)
  count_tag = "__count"
  h = v[count_tag]
  result = ""
  h.each do |typ, cnt|
    if result.length > 0
      result += ", "
    end
    result += cnt.to_s() + " [" + typ + "]"
  end
  return result
end

# Increment count
def inc_count(counts, k)
  if counts.has_key?(k)
      counts[k] += 1
  else
    counts[k] = 1
  end
end

# Run it
main(ARGV)
