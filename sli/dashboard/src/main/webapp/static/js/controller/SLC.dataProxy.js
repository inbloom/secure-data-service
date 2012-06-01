/*global SLC $ jQuery*/

SLC.dataProxy = (function () {
	var data = {},
		config = {},
		widgetConfig = {};
		
	function loadData(data) {
		jQuery.extend(this.data, data);
	}
	
	function loadConfig(config) {
		jQuery.extend(this.config, config);
	}
	
	function loadWidgetConfig(widgetConfigArray) {
		for (var i in widgetConfigArray) {
			this.widgetConfig[widgetConfigArray[i].id] = widgetConfigArray[i];
		}
	}
	
	function loadAll(dataConfigObj) {
		jQuery.extend(this.data, dataConfigObj.data);
		jQuery.extend(this.config, dataConfigObj.config);
		this.loadWidgetConfig(dataConfigObj.widgetConfig);
	}
	
	function load(componentId, id, callback) {
		var prx = this,
			w_studentListLoader = $("<div></div>").loader();
		
		w_studentListLoader.show();
					
		$.ajax({
			async: false,
			url: contextRootPath + '/service/component/' + componentId + '/' + (id ? id : ""),
			scope: this,
			success: function(panel){
				jQuery.extend(prx.data, panel.data);
				jQuery.extend(prx.config, panel.config);
				w_studentListLoader.remove();
				  
				if (jQuery.isFunction(callback)) {
					callback(panel);
				}
			},
			error: $("body").ajaxError( function(event, request, settings) {
				w_studentListLoader.remove();
				if (request.responseText === "") {
					$(location).attr('href',$(location).attr('href'));
				} else {
					$(location).attr('href', contextRootPath + "/exception");
				}
			})
		});
	}
	
	function getData(componentId) {
		var config = this.getConfig(componentId);
		
		if (config && config.data && config.data.cacheKey) {
			return this.data[config.data.cacheKey];
		}
			
		return {};
	}
	
	function getConfig(componentId) {
		return this.config[componentId];
	}
	
	function getWidgetConfig(widget) {
		return this.widgetConfig[widget];
	}
	
	// The function returns the layout name
	function getLayoutName() {
		var configObj = this.config,
			key,
			obj;
		
		for (key in configObj) {
			obj = configObj[key];
			if(obj.type && obj.type === "LAYOUT" && obj.name) {
				return obj.name;
			}
		}
		
		return "SLC";
	}
	
	return {
		loadData: loadData,
		loadConfig: loadConfig,
		loadWidgetConfig: loadWidgetConfig,
		loadAll: loadAll,
		load: load,
		getData: getData,
		getConfig: getConfig,
		getWidgetConfig: getWidgetConfig,
		getLayoutName: getLayoutName
	};
}());