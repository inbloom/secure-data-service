class HealthController < ApplicationController

    def index
        @hosts = []
        start_time = get_time_filter_value
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