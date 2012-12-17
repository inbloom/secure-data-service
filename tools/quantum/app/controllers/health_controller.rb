class HealthController < ApplicationController

	def index
		@hosts = []
		start_time = 0 # Catch all... if date filter not specified, grab all events since 1970-01-01
		start_time = Time.now - params[:time_filter].to_i unless (params[:time_filter] == nil || params[:time_filter] == "0")
		Failure.all.distinct(:hostname).each do |host|
			@hosts.push(:name => host, :count => Failure.where(hostname: host, :timestamp.gte => start_time).count) unless host == "local"
		end
		@hosts.sort!{|a,b| a[:name] <=> b[:name]} 

		respond_to do |format|
			format.html
		end
	end

	def show
		this.index()
	end
end