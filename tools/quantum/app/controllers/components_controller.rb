class ComponentsController < ApplicationController

	def index
		@components = []
		Failure.all.distinct(:component).each do |component|
			totalCount = Failure.where(component: component).count
			localCount = Failure.where(component: component, hostname: "local").count
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