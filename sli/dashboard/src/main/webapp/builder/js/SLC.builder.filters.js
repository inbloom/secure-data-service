/*global angular*/
angular.module('SLC.db.filters', []).filter('checkTab', function(item) {
  return function(item) {
    if (item.type === "TAB") {
		return item;
    }
  };
});