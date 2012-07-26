/*global angular profileCtrl $*/
angular.module('SLC.builder.app', ['SLC.builder.profilesService', 'SLC.builder.profilePageService', 'SLC.builder.headerService', 'SLC.builder.sharedService']).
	config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/profiles/:profileId', {templateUrl: 'js/templates/profile.html', controller: profileCtrl}).
		otherwise({redirectTo: '/'});
	$( "input:submit, button" ).button();
	$('#myModal').modal();
}]);