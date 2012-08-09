require 'mongo'
require 'json'
require 'optparse'
require 'hirb'

@collections = false
@all = false
@json = false
@tenantId = nil
@query = nil

opt_parser = OptionParser.new do |opt|
  opt.banner = "Usage: gather host OPTION [OPTION]"
  opt.separator  ""
  opt.separator  "Options"

  opt.on("-c", "--collections", "Gather per-collection metrics") do
    @collections = true
  end

  opt.on("-a", "--all", "Collect metrics for all tenants") do
    @all = true
  end

  opt.on("-t", "--tenant TENANTID", "Collect metrics for a sepecific tenant.") do |tenantId|
    @tenantId = tenantId
  end

  opt.on("-j", "--json", "Generate json output") do
    @json = true
  end

  opt.on("-q", "--query QUERY", "Collect metrics for a specific query across all collections.") do |query|
    @query = query;
  end

  opt.on("-h", "--help", "help") do
    puts opt_parser
  end
end

opt_parser.parse!

unless ARGV.length == 1
    puts opt_parser
    exit()
end

# {:tenantId => {:collection => {:entityCount => 0, :size => 0} } }
metrics = {}

if @all == true
    metrics["All Tenants"] = { "All Collections" => {:entityCount => 0, :size => 0} }
end

if @tenantId != nil
    metrics[@tenantId] = { "All Collections" => {:entityCount => 0, :size => 0} }
end

connection = Mongo::Connection.new(ARGV[0], 27017)
db = connection.db("sli")
collections = db.collections()

# Aggregate the following:
#   Total Records
entityCount = 0
#   Estimated Size
estimatedSize = 0.0

collections.each do |coll|
    if coll.name.match(/^system./)
        next
    end

    count = 0;
    avgSize = 0
    stats = coll.stats()

    if @all == true
        count = coll.count()
        if count > 0
            metrics["All Tenants"]["All Collections"][:entityCount] += count

            avgSize = (stats["avgObjSize"] + (stats["totalIndexSize"] / count)) * count
            metrics["All Tenants"]["All Collections"][:size] += avgSize;
        else
            next
        end

        if @collections == true
            if metrics["All Tenants"][coll.name] == nil
                metrics["All Tenants"][coll.name] = {:entityCount => 0, :size => 0}
            end

            metrics["All Tenants"][coll.name][:entityCount] = count
            metrics["All Tenants"][coll.name][:size] = avgSize
        end
    end

    if @tenantId != nil

        count = coll.count({:query => {"metaData.tenantId" => @tenantId}})
        if count > 0
            metrics[@tenantId]["All Collections"][:entityCount] += count

            avgSize = (stats["avgObjSize"] + (stats["totalIndexSize"] / count)) * count
            metrics[@tenantId]["All Collections"][:size] += avgSize
        else
            next
        end

        if @collections == true
            if metrics[@tenantId][coll.name] == nil
                metrics[@tenantId][coll.name] = {:entityCount => 0, :size => 0}
            end
            metrics[@tenantId][coll.name][:entityCount] = count
            metrics[@tenantId][coll.name][:size] = avgSize

        end
    end
end

def hash_to_ascii hash
    hash.collect do |tenantId, tenantData|
        puts "\Tenant: #{tenantId}"
        puts "+-------------------------------------------+------------------+--------------------+"
        puts "|               Collection                  |   Entity Count   |     Size (KB)      |"
        puts "+-------------------------------------------+------------------+--------------------+"

        tenantData.collect do |collectionName, collectionData|
            print "| " + collectionName.ljust(42)

            print "| " + collectionData[:entityCount].to_s().rjust(16) + " | "

            print (("%.2f " % (collectionData[:size] / 1024))).rjust(19)

            puts '|'
        end
        puts "+-------------------------------------------+------------------+--------------------+"

    end
  puts ''
end

if @json == true
    puts(metrics.to_json())
else
    hash_to_ascii(metrics)
end
