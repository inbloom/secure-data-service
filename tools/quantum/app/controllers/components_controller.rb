class ComponentsController < ApplicationController

	def index
		start_time = 0 # Catch all... if date filter not specified, grab all events since 1970-01-01
		start_time = Time.now - params[:time_filter].to_i unless (params[:time_filter] == nil || params[:time_filter] == "0")

		@components = []
		Failure.all.distinct(:component).each do |component|
			totalCount = Failure.where(component: component, :timestamp.gte => start_time).count
			localCount = Failure.where(component: component, :timestamp.gte => start_time, hostname: "local").count
			@components.push(:name => component, :local => localCount, :ci => totalCount - localCount)
		end
		@components.sort!{|a,b| a[:name] <=> b[:name]} 

		respond_to do |format|
			format.html
		end
	end

	def show
		this.index()
	end
end