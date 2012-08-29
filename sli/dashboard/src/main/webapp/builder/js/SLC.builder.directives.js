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
 * Contains SLC builder header, footer, modal window, tabs directives
 */
/*global angular $*/

angular.module('SLC.builder.directives', ['SLC.builder.sharedServices'])
	.directive("ngHeader", function () {
		return {
			restrict: 'E',
			templateUrl: "js/templates/header.html",
			controller: function($scope, $element) {
				$element.find("#SLCPortalHeader").load("../s/m/header");
			}
		};
	})
	.directive("ngFooter", function () {
		return {
			restrict: 'E',
			controller: function($scope, $element) {
				$element.load("../s/m/footer");
			}
		};
	})
	.directive("ngFocus", function () {
		return {
			restrict: 'A',
			link: function (scope, element, attrs) {
				attrs.$observe('ngFocus', function(value) {
					if(value) {
						window.setTimeout(function(){
							element.focus();
						},200);
					}
				});
			}
		};
	})
	.directive('ngModal', function() {
		return {
			restrict: 'E',
			templateUrl: "js/templates/modal.html"
		};
	})
	.directive('ngAllPanelsModal', function() {
		return {
			restrict: 'E',
			templateUrl: "js/templates/allPanelList.html"
		};
	})
	.directive("tabSortable", function($rootScope){
		return {
			link: function(scope, linkElement, attrs){
				linkElement.sortable({
					axis: "x",
					update: function() {
						var model = scope.$parent.$eval(attrs.tabSortable);
						$rootScope.newPageArray = [];

						// loop through items in new order
						linkElement.children().each(function(index) {
							var item = $(this);

							// get old item index
							var oldIndex = parseInt(item.attr("tab-sortable-index"), 10);
							if(model[oldIndex] !== null && model[oldIndex] !== undefined) {
								$rootScope.newPageArray.push(model[oldIndex]);
							}
						});

						// notify angular of the change
						scope.$parent.$apply();
						scope.$parent.$broadcast("tabChanged");
					}
				});
			}
		};
	})
	.directive("panelSortable", function($rootScope){
		return {
			link: function(scope, linkElement, attrs){
				linkElement.sortable({
					axis: "y",
					update: function() {
						var model = scope.$eval(attrs.panelSortable);
						$rootScope.newPageArray = [];

						// loop through items in new order
						linkElement.children().each(function(index) {
							var item = $(this);

							// get old item index
							var oldIndex = parseInt(item.attr("panel-sortable-index"), 10);
							if(model[oldIndex] !== null && model[oldIndex] !== undefined) {
								$rootScope.newPageArray.push(model[oldIndex]);
							}
						});

						// notify angular of the change
						scope.$parent.$apply();
						scope.$parent.$broadcast("panelChanged");
					}
				});
			}
		};
	})
	.directive('tabs', function(dbSharedService) {
		return {
			restrict: 'E',
			transclude: true,
			scope: {},
			controller: function($scope, $element, dbSharedService) {
				var panes = $scope.panes = [],
					parent = $scope.$parent;

				// The selected tab will display in active mode
				$scope.select = function(pane) {
					angular.forEach(panes, function(pane) {
						pane.selected = false;
						parent.checked = false;
					});
					pane.selected = true;
				};

				this.addPane = function(pane) {
					if (panes.length === 0) {
						$scope.select(pane);
					}
					panes.push(pane);
				};

				// When the page gets removed from the profile, the associated tab will get removed from the pane
				$scope.$on("pageRemoved", function (e, index) {
					panes.splice(index, 1);
					if (panes[index]) {
						$scope.select(panes[index]);
					}
					else if (panes[index-1]) {
						$scope.select(panes[index-1]);
					} 
				});

				// Add a new page/tab into the profile
				$scope.addPage = function () {
					var pageId = dbSharedService.generatePageId(parent.pages);
					parent.pages.push({id:pageId, name:"New page", items: [], parentId:pageId, type:"TAB"});
					parent.saveProfile(function () {
						$scope.select(panes[panes.length-1]);
						parent.checked = true;
					});
				};
			},
			template:
				'<div class="tabbable">' +
					'<ul class="nav nav-tabs clearfix" tab-sortable="pages">' +
					'<li ng-repeat="pane in panes" tab-sortable-index="{{$index}}" ng-class="{active:pane.selected}">'+
					'<a href="" ng-click="select(pane)">{{pane.title}}</a>' +
					'</li>' +
					'<li class="addPageSection">' +
					'<button class="btn btn-primary" data-toggle="modal" ng-click="addPage()" >+</button>' +
					'</li>' +
					'</ul>' +
					'<div class="tab-content clearfix" ng-transclude></div>' +
					'</div>',
			replace: true
		};
	})
	.directive('pane', function() {
			return {
				require: '^tabs',
				restrict: 'E',
				transclude: true,
				scope: { title: '@' },
				link: function(scope, element, attrs, tabsCtrl) {
					tabsCtrl.addPane(scope);
				},
				template:
					'<div class="tab-pane" ng-class="{active: selected}" ng-transclude>' +
					'</div>',
				replace: true
			};
		});