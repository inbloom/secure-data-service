/*global angular profileCtrl $*/
angular.module('SLC.builder.app', ['SLC.builder.sharedServices']).
	config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/profiles/:profileId', {templateUrl: 'js/templates/profile.html', controller: profileCtrl}).
		otherwise({redirectTo: '/'});
	$( "input:submit, button" ).button();
	$('#myModal').modal();
}]);