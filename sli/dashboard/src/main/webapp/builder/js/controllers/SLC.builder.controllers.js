/*global angular $ alert*/

function headerCtrl($scope, $routeParams, $http, Header) {
	//$scope.header = Header.query();
	$http({method: 'GET', url: '/dashboard/s/m/header'}).
		success(function(data, status, headers, config) {
			$scope.header = data;
		});
}

function profileListCtrl($scope, Profile) {
	$scope.profiles = Profile.query();
}

profileListCtrl.$inject = ['$scope', 'Profile'];

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
		var pageNumMax = 0,
			id,
			pageNumStr,
			pageNum,
			i;
		for (i = 0; i < $scope.pages.length; i++) {
			id = $scope.pages[i].id;
			if(id.indexOf("tab") === 0) {
				pageNumStr = id.substring(3);
				if (pageNumStr.length > 0 && !isNaN(pageNumStr)) {
					pageNum = parseInt(pageNumStr);
					if (pageNum > pageNumMax) {
						pageNumMax = pageNum;
					}
				}
			}
		}
		return "tab" + (pageNumMax + 1);
	};
	
	$scope.savePage = function () {
		var page = dbSharedService.getPage();

		if($scope.mode === "Add New") {
			var pageId = $scope.generatePageId();
			$scope.pages.push({id:pageId, name:$scope.pageText, items: $.parseJSON($scope.panelJSON), type:"TAB"});
		}
		else if($scope.mode === "Edit") {
			page.name = $scope.pageText;
			page.items = $.parseJSON($scope.panelJSON);

			dbSharedService.setPage(page);
		}

		dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server

		$scope.pageText = '';
		$('#myModal').modal('hide');
	};

	$scope.showDialog = function (mode) {
		if(mode === "add") {
			$scope.mode = "Add New";
			$scope.pageText = '';
			$scope.panelJSON = "[]";
		}
		else if(mode === "edit") {
			var page = dbSharedService.getPage();
			$scope.mode = "Edit";
			$scope.panelJSON = angular.toJson(page.items);
			$scope.pageText = page.name;
		}
		$('#myModal').modal('show');
	};

	$( "#tabs" ).tabs().find( ".ui-tabs-nav" ).sortable({ axis: "x" });
}
profileCtrl.$inject = ['$scope', '$routeParams', 'ProfilePage', 'dbSharedService', '$http'];

function editorCtrl($scope, dbSharedService) {

	$scope.editPage = function () {
		dbSharedService.setPage($scope.page);
		this.showDialog('edit');
	};

	$scope.removePage = function () {
		var i;
		for (i = 0; i < $scope.pages.length; i++) {
			if ($scope.page.id === $scope.pages[i].id) {
				$scope.pages.splice(i, 1);
				dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server
				return false;
			}
		}

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
