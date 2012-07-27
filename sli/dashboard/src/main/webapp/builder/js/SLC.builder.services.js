/*global angular console*/
angular.module('SLC.builder.sharedServices', ['ngResource'])
	.factory('Header', function($resource){
		return $resource('/dashboard/s/m/header', {}, {
			query: {method:'GET'}
		});
	})
	.factory('Profile', function($resource){
			return $resource('/dashboard/s/c/cfg?type=LAYOUT');
		})
	.factory('ProfilePage', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT&id=:profilePageId', {}, {
			query: {method:'GET', params:{profilePageId:''}, isArray:true}
		});
	})
	.factory('dbSharedService', function($http){
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
			}).success(function() {
				console.log("success");
			}).error(function() {
				console.log("fail");
			});
		}

		return {
			getPage: getPage,
			setPage: setPage,
			saveDataSource: saveDataSource
		};
	});