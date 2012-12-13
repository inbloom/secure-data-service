class HealthController < ApplicationController

	def index
		@hosts = []
		Failure.all.distinct(:hostname).each do |host|
			@hosts.push(:name => host, :count => Failure.where(hostname: host).count) unless host == "local"
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