require 'mongo'
require 'json'

module TenantMetricsHelper

  def self.generateMetrics(tenantId = nil, host = 'localhost', port = 27017, dbName = 'sli', summary=true, json=true)

    metrics = {}

    if tenantId == nil
      all = true
    end

    # {:tenantId => {:collection => {:entityCount => 0, :size => 0} } }
    metrics = {}

    if all == true
      metrics["== Totals =="] = {"== Totals ==" => {:entityCount => 0, :size => 0}}
    end

    if tenantId != nil
      metrics[tenantId] = {"== Totals ==" => {:entityCount => 0, :size => 0}}
    end

    connection = Mongo::Connection.new(host, port)
    db = connection.db(dbName)
    collections = db.collections()

    # Aggregate the following:
    #   Total Records
    #   Estimated Size
    entityCount = 0
    estimatedSize = 0.0

    collections.each do |coll|
      if coll.name.match(/^system./)
        next
      end

      count = 0;
      avgSize = 0
      stats = coll.stats()

      if all == true
        count = coll.count()
        if count > 0
          metrics["== Totals =="]["== Totals =="][:entityCount] += count

          avgSize = (stats["avgObjSize"] + (stats["totalIndexSize"] / count)) * count
          metrics["== Totals =="]["== Totals =="][:size] += avgSize;
        else
          next
        end

        if summary == false
          if metrics["== Totals =="][coll.name] == nil
            metrics["== Totals =="][coll.name] = {:entityCount => 0, :size => 0}
          end

          metrics["== Totals =="][coll.name][:entityCount] = count
          metrics["== Totals =="][coll.name][:size] = avgSize
        end

        metrics = gather_all_tenant_metrics(db, coll, summary, metrics)

      elsif tenantId != nil
        metrics = gather_tenant_metrics(db, tenantId, coll, summary, metrics)
      end
    end

    if json == true
      metrics
    else
      hash_to_ascii(metrics)
    end
  end

  def self.gather_all_tenant_metrics(db, coll, summary, metrics)

    tenants = db['tenant']

    tenants.find({}, {:fields => ["body.tenantId"]}).each do |data|
      gather_tenant_metrics(db, data["body"]["tenantId"], coll, summary, metrics)
    end

    metrics
  end

  def self.gather_tenant_metrics(db, tenantId, coll, summary, metrics)

    stats = coll.stats()

    count = coll.count({:query => {"metaData.tenantId" => tenantId}})
    if count > 0
      if metrics[tenantId] == nil
        metrics[tenantId] = {"== Totals ==" => {:entityCount => 0, :size => 0}}
      end

      metrics[tenantId]["== Totals =="][:entityCount] += count
      avgSize = (stats["avgObjSize"] + (stats["totalIndexSize"] / count)) * count
      metrics[tenantId]["== Totals =="][:size] += avgSize
    else
      return metrics
    end

    if summary == false
      if metrics[tenantId][coll.name] == nil
        metrics[tenantId][coll.name] = {:entityCount => 0, :size => 0}
      end
      metrics[tenantId][coll.name][:entityCount] = count
      metrics[tenantId][coll.name][:size] = avgSize
    end

    metrics
  end

  def self.hash_to_ascii hash
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

end
