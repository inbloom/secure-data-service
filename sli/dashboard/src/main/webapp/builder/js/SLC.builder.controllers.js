/*global angular $ alert*/

/*function headerCtrl($scope, $http, Header) {
	//$scope.header = Header.query();
	$http({method: 'GET', url: '/dashboard/s/m/header'}).
		success(function(data, status, headers, config) {
			$scope.header = data;
		});
}*/

function profileListCtrl($scope, $rootScope, Profile) {
	
	$scope.profiles = [];
	Profile.query(function(profiles) {
		
		// exclude search profiles. not user-configurable.
		for (i = 0; i < profiles.length; i++) {
			var profile = profiles[i];
    		if(!(profile.id.match(/search/i))) {
	    	    $scope.profiles.push(profile);
    		}
		}
	}, function(error) {
		$(".errorMessage").removeClass("hide");
		
		if (error.status === 401) {
			$scope.errorMessage = "Access Denied: Unauthorized user";
		} else {
			$scope.errorMessage = "Error";
		}
	});
}

profileListCtrl.$inject = ['$scope', '$rootScope', 'Profile'];

function profileCtrl($scope, $routeParams, ProfilePage, dbSharedService) {

	ProfilePage.query({profilePageId: $routeParams.profileId}, function(profile) {
		var i;

		$scope.profile = profile[0];
		$scope.pages = [];
		$scope.panels = [];
		for (i = 0; i < $scope.profile.items.length; i++) {
			if($scope.profile.items[i].type === "TAB") {
				$scope.pages.push($scope.profile.items[i]);
			}
			else {
				$scope.panels.push($scope.profile.items[i]);
			}
		}
		$scope.id = $scope.profile.id;
	}, function(error) {
		$(".errorMessage").removeClass("hide");
		
		if (error.status === 401) {
			$scope.errorMessage = "Access Denied: Unauthorized user";
		} else {
			$scope.errorMessage = "Error";
		}
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
					pageNum = parseInt(pageNumStr, 10);
					if (pageNum > pageNumMax) {
						pageNumMax = pageNum;
					}
				}
			}
		}
		return "tab" + (pageNumMax + 1);
	};

	$scope.$on("tabChanged", function () {
		$scope.pages = $scope.newArray;
		$scope.saveProfile();
	});

	$scope.savePage = function () {
		var page = dbSharedService.getPage();

		if($scope.mode === "Add New") {
			var pageId = $scope.generatePageId();
			try {
			    $scope.pages.push({id:pageId, name:$scope.pageText, items: $.parseJSON($scope.panelJSON), type:"TAB"});
			} catch(e) {
				alert("JSON parse error");
			}
		}
		else if($scope.mode === "Edit") {
			page.name = $scope.pageText;
			try {
			    page.items = $.parseJSON($scope.panelJSON);
			} catch(e) {
				alert("JSON parse error");
			}
			dbSharedService.setPage(page);
		}

		$scope.saveProfile();

		$scope.pageText = '';
		$('#myModal').modal('hide');
	}, function(error) {
		
		if (error.status === 401) {
			alert("Unauthorized");
		} else {
			alert("Error");
		}
	};

	$scope.saveProfile = function () {
		$scope.profile.items = [];
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server
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
}
profileCtrl.$inject = ['$scope', '$routeParams', 'ProfilePage', 'dbSharedService', '$http'];

function editorCtrl($scope, dbSharedService) {

	$scope.editPage = function () {
		dbSharedService.setPage($scope.page);
		this.showDialog('edit');
	};

	$scope.removePage = function () {
		/*$scope.pages.splice($index, 1);
		$scope.saveProfile();*/

		var i;
		for (i = 0; i < $scope.pages.length; i++) {
			if ($scope.page.id === $scope.pages[i].id) {
				$scope.pages.splice(i, 1);
				$scope.saveProfile();
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
