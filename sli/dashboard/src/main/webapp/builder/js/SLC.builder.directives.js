/*global angular $*/

angular.module('SLC.builder.directives', ['SLC.builder.sharedServices'])
	.directive('maketab',function($rootScope) {
		return function($rootScope, elm) {
			elm.tabs();
		};
	})
	.directive("ngSortable", function($rootScope){
		$rootScope.newArray = [];
		return {
			link: function(scope, linkElement, attrs){
				linkElement.sortable({
					axis: "x",
					update: function() {
						// get model
						var model = scope.$eval(attrs.ngSortable);
						$rootScope.newArray = [];
						// loop through items in new order
						linkElement.children().each(function(index) {
							var item = $(this);

							// get old item index
							var oldIndex = parseInt(item.attr("ng-sortable-index"), 10);
							if(model[oldIndex] !== null && model[oldIndex] !== undefined) {
								$rootScope.newArray.push(model[oldIndex]);
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