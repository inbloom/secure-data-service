/*global angular $ alert*/

function headerCtrl($scope, $routeParams, $http, Header) {
	//$scope.header = Header.query();
	$http({method: 'GET', url: '/dashboard/s/m/header'}).
		success(function(data, status, headers, config) {
			$scope.header = data;
		});
}

function profileListCtrl($scope, $routeParams, Profile, dbSharedService) {
	$scope.profiles = Profile.query();
}

profileListCtrl.$inject = ['$scope', '$routeParams', 'Profile', 'dbSharedService'];

function profileCtrl($scope, $routeParams, ProfilePage, dbSharedService, $http) {

	ProfilePage.query({profilePageId: $routeParams.profileId}, function(profile) {
		$scope.profile = profile[0];
		$scope.pages = $scope.profile.items;
		$scope.id = $scope.profile.id;
	});

	$scope.checkTab = function (item) {
		return item.type === "TAB";
	};

	$scope.generatePageId = function () {
		var pageNumMax = 0;
		for(i=0; i<$scope.pages.length; i++) {
			var id = $scope.pages[i].id;
			if(id.indexOf("tab") === 0) {
				var pageNumStr = id.substring(3);
				if(pageNumStr.length > 0 && !isNaN(pageNumStr)) {
					var pageNum = parseInt(pageNumStr);
					if(pageNum > pageNumMax) {
						pageNumMax = pageNum;
					}
				}
			}
		}
		return "tab" + (pageNumMax + 1);
	};
	
	$scope.savePage = function () {
		if($scope.mode === "Add New") {
			var pageId = $scope.generatePageId();
			$scope.pages.push({id:pageId, name:$scope.pageText, items: $.parseJSON($scope.panelJSON), type:"TAB"});
		}
		else if($scope.mode === "Edit") {
			dbSharedService.page.name = $scope.pageText;
			dbSharedService.page.items = $.parseJSON($scope.panelJSON);
		}

		$http({
			method: 'POST',
			url: '/dashboard/s/c/saveCfg',
			data: angular.toJson($scope.profile)
		}).success(function(data, status, headers, config) {
				console.log("success");
			}).error(function(data, status, headers, config) {
				console.log("fail");
		});

		$scope.pageText = '';
		$('#myModal').modal('hide');
	};

	$scope.showJson = function () {
		alert(angular.toJson($scope.profile));
	};

	$scope.showDialog = function (mode) {
		if(mode === "add") {
			$scope.mode = "Add New";
			$scope.pageText = '';
			$scope.panelJSON = "[]";
		}
		else if(mode === "edit") {
			$scope.mode = "Edit";
			$scope.panelJSON = angular.toJson(dbSharedService.page.items);
			$scope.pageText = dbSharedService.page.name;
		}
		$('#myModal').modal('show');
	};

	$( "#tabs" ).tabs().find( ".ui-tabs-nav" ).sortable({ axis: "x" });
}
profileCtrl.$inject = ['$scope', '$routeParams', 'ProfilePage', 'dbSharedService', '$http'];

function editorCtrl($scope, dbSharedService) {

	$scope.editPage = function () {
		dbSharedService.page = $scope.page;
		this.showDialog('edit');
	};

	$scope.removePage = function ($index) {
		$scope.pages.splice($index, 1);
	};

	$(function () {
		$("#tabs ul li").hover(function(){
			$(this).find(".view").addClass("hide");
			$(this).find("div").removeClass("hide");
		}, function(){
			$(this).find(".view").removeClass("hide");
			$(this).find("div").addClass("hide");
		});
	});
}

editorCtrl.$inject = ['$scope', 'dbSharedService'];
