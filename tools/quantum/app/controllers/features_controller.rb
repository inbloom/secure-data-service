class FeaturesController < ApplicationController

	def index
		@features = []
		Failure.all.distinct(:feature).each do |feature|
			@features.push(:name => feature, :count => Failure.where(feature: feature).count, :component => Failure.where(feature: feature)[0][:component])
		end
		@features.sort!{|a,b| b[:count] <=> a[:count]} 

		respond_to do |format|
			format.html
		end
	end

end