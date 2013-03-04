class ComponentsController < ApplicationController

    def index
        start_time = get_time_filter_value

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