/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * SLC Dashboard Builder Controller
 */
/*global angular $ alert*/

// Header Controller
function headerCtrl() {
	$("#header").load("/dashboard/s/m/header");
}

// Footer Controller
function footerCtrl() {
	$("#footer").load("/dashboard/s/m/footer");
}

// Profile List Controller
function profileListCtrl($scope, Profiles) {
	var i;
	$scope.profiles = [];
	Profiles.query(function(profiles) {

		// exclude search profiles
		for (i = 0; i < profiles.length; i++) {
			var profile = profiles[i];
			if(!(profile.id.match(/search/i))) {
				$scope.profiles.push(profile);
			}
		}
	});
}

profileListCtrl.$inject = ['$scope', 'Profiles'];


// Profile Controller
function profileCtrl($scope, $routeParams, Profile, dbSharedService) {

	Profile.query({profilePageId: $routeParams.profileId}, function(profile) {
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
	});

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
		$scope.pages = [];
		$scope.pages = $scope.newPageArray;
		$scope.saveProfile();
	});

	$scope.saveProfile = function () {

		var configs = dbSharedService.getModalConfig(),
			page = dbSharedService.getPage();

		if(configs.mode === "add") {
			var pageId = $scope.generatePageId();
			$scope.pages.push({id:pageId, name:configs.pageTitle, items: $.parseJSON(configs.contentJSON), type:"TAB"});
		}
		else if(configs.mode === "edit") {
			page.name = configs.pageTitle;
			page.items = $.parseJSON(configs.contentJSON);

			dbSharedService.setPage(page);
		}

		$.modal.close();

		$scope.profile.items = [];
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server

		configs.mode = "";
		dbSharedService.setModalConfig(configs);
	};

	$scope.removePagefromProfile = function () {
		var i,
			page = dbSharedService.getPage();
		for (i = 0; i < $scope.pages.length; i++) {
			if (page.id === $scope.pages[i].id) {
				$scope.pages.splice(i, 1);
				$scope.saveProfile();
				break;
			}
		}
	};

	$scope.addDialog = function () {
		var modalConfig = {};

		modalConfig.mode = "add";
		modalConfig.modalTitle = "Add New Page";
		modalConfig.pageTitle = '';
		modalConfig.contentJSON = "[]";

		dbSharedService.showModal("#pageModal", modalConfig);

	};

	$scope.editDialog = function () {
		var page = dbSharedService.getPage(),
			modalConfig = {};

		modalConfig.mode = "edit";
		modalConfig.modalTitle = "Edit Page";
		modalConfig.contentJSON = angular.toJson(page.items);
		modalConfig.pageTitle = page.name;

		dbSharedService.showModal("#pageModal", modalConfig);
	};
}
profileCtrl.$inject = ['$scope', '$routeParams', 'Profile', 'dbSharedService'];


// Page Controller
function pageCtrl($scope, dbSharedService) {

	$scope.editPage = function () {
		dbSharedService.setPage($scope.page);
		this.editDialog();
	};

	$scope.removePage = function () {
		dbSharedService.setPage($scope.page);
		this.removePagefromProfile();
	};


	// Show/hide drag, edit, trash icons on tab mouse-hover/mouse-out using JQuery.

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

pageCtrl.$inject = ['$scope', 'dbSharedService'];


// Editor Controller
function editorCtrl($scope, dbSharedService) {

	$scope.$on("modalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();

		$("#pageModal h3").html(configs.modalTitle);
		$("#pageTitle").val(configs.pageTitle);
		$("#content_json").val(configs.contentJSON);
	});

	$scope.savePage = function () {
		var configs = {};

		configs.pageTitle = $("#pageTitle").val();
		configs.contentJSON = $("#content_json").val();

		dbSharedService.setModalConfig(configs);

		$scope.saveProfile();
	};

}

editorCtrl.$inject = ['$scope', 'dbSharedService'];