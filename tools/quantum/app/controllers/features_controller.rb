class FeaturesController < ApplicationController

	def index
		@features = []
#		if params[:component_filter] != nil
#			Failure.only(:feature).where(compoent: params[:component_filter]).each do |feature|
#				@features.push(:name => feature, :count => Failure.where(feature: feature).count, :component => Failure.where(feature: feature)[0][:component])
#			end
#		else
			Failure.all.distinct(:feature).each do |feature|
				@features.push(:name => feature, :count => Failure.where(feature: feature).count, :component => Failure.where(feature: feature)[0][:component])
			end
#		end
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