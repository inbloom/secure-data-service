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
				scope.$watch(attrs.ngFocus, function(newValue, oldValue){
					if(newValue) {
						window.setTimeout(function(){
							element.focus();
						},200);
					}
				}, true);
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
	.directive("ngSortable", function($rootScope){
		return {
			restrict: 'A',
			link: function(scope, linkElement, attrs){
				linkElement.sortable({
					items: "li:not(.nonSortable)",
					cancel: ".nonSortable",
					update: function() {
						var model, panelModel;

						if(scope.$eval(attrs.ngSortable)) {
							model = scope.$eval(attrs.ngSortable);
						}
						else {
							model = scope.$parent.$eval(attrs.ngSortable);

							if (attrs.ngSortable === "pages") {
								panelModel = scope.$eval("panes");
							}
						}
						$rootScope.newPageArray = [];
						$rootScope.newPaneArray = [];

						// loop through items in new order
						linkElement.children().each(function(index) {
							var item = $(this);

							// get old item index
							var oldIndex = parseInt(item.attr("ng-sortable-index"), 10);
							if(model[oldIndex] !== null && model[oldIndex] !== undefined) {
								$rootScope.newPageArray.push(model[oldIndex]);
								if (attrs.ngSortable === "pages") {
									$rootScope.newPaneArray.push(panelModel[oldIndex]);
								}
							}
						});

						// notify angular of the change
						scope.$parent.$apply();

						if (attrs.ngSortable === "pages") {
							scope.$parent.$broadcast("tabChanged");
						}
						else if (attrs.ngSortable === "pagePanels") {
							scope.$parent.$broadcast("panelChanged");
						}
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
			controller: function($scope, $rootScope, $element, dbSharedService) {
				var panes = $scope.panes = [],
					parent = $scope.$parent;

				// The selected tab will display in active mode
				$scope.select = function(pane) {

					$rootScope.profileAlert = false; // set profile alert flag to false

					// if user trying to navigate away from the selected tab without saving page-level changes,
					// the save changes confirmation box will display.
					if($rootScope.saveStatus) {
						dbSharedService.showModal("#alertModal", {mode: "alert"});

						$scope.$on("leavePage", function () {
							$scope.selectTab(pane);
							dbSharedService.enableSaveButton(false);
						});

						return false;
					}

					dbSharedService.setPage($scope.$parent.page);
					$scope.selectTab(pane);
				};


				$scope.selectTab = function(pane) {
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

				$scope.$parent.$on("tabChanged", function () {
					panes = $scope.panes = [];
					$scope.panes = $scope.$parent.newPaneArray;

					angular.forEach($scope.panes, function(pane) {
						panes.push(pane);
					});

					panes = $scope.panes;
				});

				// When the page will get removed, the associated pane will also get removed.
				$scope.$on("pageRemoved", function (e, index) {
					panes.splice(index, 1);

					if (panes[0]) {
						$scope.select(panes[0]);
					}
				});

				// Add a new page/tab into the profile
				$scope.addPage = function () {
					$rootScope.addNewPage = false;

					// if user trying to navigate away from the selected tab without saving page-level changes,
					// the save changes confirmation box will display.
					if($rootScope.saveStatus) {

						$rootScope.addNewPage = true;
						dbSharedService.showModal("#alertModal", {mode: "alert"});

						// After 'restorePageAndAddNewPage' get triggered, the new tab/page will be added to the profile
						$scope.$on("restorePageAndAddNewPage", function () {

							if($rootScope.addNewPage) {
								var pageId = dbSharedService.generatePageId(parent.pages);
								parent.pages.push({id:pageId, name:"New page", items: [], parentId:pageId, type:"TAB"});
								parent.saveProfile(function () {
									$scope.select(panes[panes.length-1]);
									parent.checked = true;
								});
							}

							$rootScope.addNewPage = false;
							return false;
						});

						return false;
					}

					var pageId = dbSharedService.generatePageId(parent.pages);
					parent.pages.push({id:pageId, name:"New page", items: [], parentId:pageId, type:"TAB"});
					parent.saveProfile(function () {
						$scope.select(panes[panes.length-1]);
						parent.checked = true;
					});
				};

				$scope.$on("tabReRendered", function () {
						this.$render();
				});
			},
			template:
				'<div class="tabbable">' +
					'<ul class="nav nav-tabs clearfix" ng-sortable="pages">' +
					'<li ng-repeat="pane in panes" ng-sortable-index="{{$index}}" ng-class="{active:pane.selected}">'+
					'<a href="" ng-click="select(pane)">{{pane.title}}</a>' +
					'</li>' +
					'<li class="addPageSection nonSortable">' +
					'<button class="btn btn-info" data-toggle="modal" ng-click="addPage()" >+</button>' +
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