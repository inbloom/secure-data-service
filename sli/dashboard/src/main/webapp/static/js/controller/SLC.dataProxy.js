/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * SLC dataProxy
 * Handles all methods related to data and config objects
 */
/*global SLC $*/

SLC.namespace('SLC.dataProxy', (function () {
		var data = {},
			config = {},
			widgetConfig = {};
			
		/* 
		 * Load data into dataProxy data object
		 * @param objData - data object
		 */
		function loadData(objData) {
			$.extend(data, objData);
		}
		
		/* 
		 * Load config into dataProxy config object
		 * @param objConfig - config object
		 */
		function loadConfig(objConfig) {
			$.extend(config, objConfig);
		}
		
		/*
		 * Load widget config into dataProxy widget config object
		 * @param widgetConfigArray - widget config Array
		 */
		function loadWidgetConfig(widgetConfigArray) {
			var i = 0;
			for (; i < widgetConfigArray.length; i++) {
				widgetConfig[widgetConfigArray[i].id] = widgetConfigArray[i];
			}
		}
		
		/*
		 * Load all data including data, config and widget config into dataProxy
		 * @param dataConfigObj - config object
		 */
		function loadAll(dataConfigObj) {
			$.extend(data, dataConfigObj.data);
			$.extend(config, dataConfigObj.config);
			loadWidgetConfig(dataConfigObj.widgetConfig);
		}
		
		/* 
		 * Send ajax request and load panel data into dataProxy
		 * @param componentId - config object
		 * @param id - id
		 * @param callback - callback function
		 */
		function load(componentId, id, callback) {
			var contextRootPath = SLC.util.getContextRootPath(),
				w_studentListLoader = SLC.loadingMask.create({context:"<div></div>"});
			
			w_studentListLoader.show();
						
			$.ajax({
				async: false,
				url: contextRootPath + '/s/c/' + componentId + '/' + (id ? id : ""),
				scope: this,
				success: function(panel){
					$.extend(data, panel.data);
					$.extend(config, panel.config);
					w_studentListLoader.remove();
					  
					if ($.isFunction(callback)) {
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
				}),
				fixture: "app-loadData"
			});
		}
		
		/*
		 * Get component config
		 * @param componentId - config id (string)
		 * @return config object or false
		 */
		function getConfig(componentId) {
			if (typeof componentId === "string" && config[componentId]) {
				return config[componentId];
			}
			
			return false;
		}
		
		/*
		 * Get component data
		 * @param componentId - config id (string)
		 * @return data object or false
		 */
		function getData(componentId) {
			if (typeof componentId !== "string") {
				return false;
			}
			var config = this.getConfig(componentId);
			
			if (config && config.data && config.data.cacheKey) {
				return data[config.data.cacheKey];
			}
				
			return {};
		}
		
		
		/*
		 * Get widget config
		 * @param widget - widget config id (string)
		 * @return widget config object or false
		 */
		function getWidgetConfig(widget) {
			if (typeof widget === "string" && widgetConfig[widget]) {
				return widgetConfig[widget];
			}
			
			return false;
		}
		
		/*
		 * Get all configs
		 * @return config object
		 */
		function getAllConfig() {
			return config;
		}
		
		/* 
		 * Get the layout name from Config data
		 * @return layout name or "SLC"
		 */
		function getLayoutName() {
			var configObj = getAllConfig(),
				key,
				obj;
			
			for (key in configObj) {
				if (configObj.hasOwnProperty(key)) {
					obj = configObj[key];
					if (obj.type && obj.type === "LAYOUT" && obj.name) {
						return obj.name;
					}
				}
			}
			
			return "inBloom";
		}
		
		/* 
		 * check if tab panel is loading on the page or not
		 * @return true or false
		 */
		function checkTabPanel() {
			var configObj = getAllConfig(),
				key,
				obj,
				items,
				i;
			
			for (key in configObj) {
				if (configObj.hasOwnProperty(key)) {
					obj = configObj[key];
					if (obj.items) {
						items = obj.items;
						for (i = 0; i < items.length; i++) {
							if (items[i].type === "TAB") {
								return true;
							}
						}
					}
				}
			}
			
			return false;
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
			getLayoutName: getLayoutName,
			checkTabPanel: checkTabPanel
		};
	}())
);
