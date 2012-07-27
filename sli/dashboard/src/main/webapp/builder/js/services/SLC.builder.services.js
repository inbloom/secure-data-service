/*global angular*/
var sharedServices = angular.module('SLC.builder.sharedServices', ['ngResource']);

sharedServices.factory('Header', function($resource){
		return $resource('/dashboard/s/m/header', {}, {
			query: {method:'GET'}
		});
	});
sharedServices.factory('Profile', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT');
	});

sharedServices.factory('ProfilePage', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT&id=:profilePageId', {}, {
			query: {method:'GET', params:{profilePageId:''}, isArray:true}
		});
	});

sharedServices.factory('dbSharedService', function($http){
		var page = {};

		function getPage() {
			return page;
		}

		function setPage(item) {
			page = item;
		}

		function saveDataSource(profileData) {
			$http({
				method: 'POST',
				url: '/dashboard/s/c/saveCfg',
				data: profileData
			}).success(function(data, status, headers, config) {
				console.log("success");
			}).error(function(data, status, headers, config) {
				console.log("fail");
			});
		}

		return {
			getPage: getPage,
			setPage: setPage,
			saveDataSource: saveDataSource
		};
	});