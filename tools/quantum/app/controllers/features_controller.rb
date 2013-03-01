class FeaturesController < ApplicationController

    def index
        @features = []
        start_time = get_time_filter_value
        feature_set = Set.new
        if params[:component_filter] != nil and !params[:component_filter].empty?
            @cur_component = params[:component_filter]
            Failure.only(:feature, :component).where(:component => params[:component_filter]).each do |failure|
                feature_set.add(failure[:feature])
            end
        else
            feature_set = Failure.all.distinct(:feature)
        end
        
        feature_set.each do |feature|
            @features.push(:name => feature, :count => Failure.where(feature: feature, :timestamp.gte => start_time).count, :component => (@cur_component!=nil ? @cur_component : Failure.where(feature: feature)[0][:component]))
        end
        @features.sort!{|a,b| b[:count] <=> a[:count]} 

        respond_to do |format|
            format.html
        end
    end

    def show
        this.index()
    end

    def getComponentOptions
        Failure.all.distinct(:component)
    end
end