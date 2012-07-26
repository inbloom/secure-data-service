/*global angular*/
angular.module('SLC.builder.profilesService', ['ngResource']).
	factory('Profile', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT', {}, {
			query: {method:'GET', isArray:true}
		});
	});

angular.module('SLC.builder.profilePageService', ['ngResource']).
	factory('ProfilePage', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT&id=:profilePageId', {}, {
			query: {method:'GET', params:{profilePageId:''}, isArray:true}
		});
	});

angular.module('SLC.builder.headerService', ['ngResource']).
	factory('Header', function($resource){
		return $resource('/dashboard/s/m/header', {}, {
			query: {method:'GET'}
		});
	});

var sharedService = angular.module('SLC.builder.sharedService', []);
sharedService.factory('dbSharedService', function(){
		var page = {};
		return page;
	});