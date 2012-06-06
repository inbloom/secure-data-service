class TenantMetric < SessionResource

   attr_accessor :host, :port, :dbName, :isSummary, :useJson, :metrics

   def initialize(host = 'localhost', port = 27017, dbName = 'sli', summary=true, useJson=true)
     @host = host
     @port = port
     @dbName = dbName
     @isSummary = summary
     @useJson = useJson
   end

   def calculate_metrics(tenantId)
     @metrics = TenantMetricsHelper.generateMetrics(tenantId, @host, @port, @dbName, @isSummary, @useJson)
   end

   def metrics()
     @metrics
   end

   def persisted?
     false
   end

   def get_binding
     binding
   end

end