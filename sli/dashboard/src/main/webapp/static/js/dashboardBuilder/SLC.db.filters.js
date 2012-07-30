/*global angular*/
angular.module('dbFilters', []).filter('checkTab', function() {
  return function(page) {
    return page.type == "TAB";
  };
});