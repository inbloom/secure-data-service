/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
 * SLC Dashboard Builder Directives
 */
/*global angular $*/

angular.module('SLC.builder.directives', ['SLC.builder.sharedServices'])
	.directive('ngTabs',function($rootScope) {
		return function($rootScope, elm) {
			elm.tabs();
		};
	})
	.directive("ngSortable", function($rootScope){

		return {
			link: function(scope, linkElement, attrs){
				linkElement.sortable({
					axis: "x",
					update: function() {
						var model = scope.$eval(attrs.ngSortable);
						$rootScope.newPageArray = [];
						// loop through items in new order
						linkElement.children().each(function(index) {
							var item = $(this);

							// get old item index
							var oldIndex = parseInt(item.attr("ng-sortable-index"), 10);
							if(model[oldIndex] !== null && model[oldIndex] !== undefined) {
								$rootScope.newPageArray.push(model[oldIndex]);
							}
						});

						// notify angular of the change
						scope.$apply();
						scope.$broadcast("tabChanged");
					}
				});
			}
		};
	});