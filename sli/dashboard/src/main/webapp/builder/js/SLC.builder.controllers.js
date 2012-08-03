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
function profileListCtrl($scope, Profiles, dbSharedService) {
	var i;
	$scope.profiles = [];
	Profiles.query(function(profiles) {

		// Search profile are not user configurable so we are skipping that profile
		for (i = 0; i < profiles.length; i++) {
			var profile = profiles[i];
			if(!(profile.id.match(/search/i))) {
				$scope.profiles.push(profile);
			}
		}
	}, function(error) {
		dbSharedService.showError(error);
	});
}

profileListCtrl.$inject = ['$scope', 'Profiles', 'dbSharedService'];


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
	}, function(error) {
		dbSharedService.showError(error);
	});

	$scope.$on("tabChanged", function () {
		$scope.pages = [];
		$scope.pages = $scope.newPageArray;
		$scope.saveProfile();
	});

	$scope.savePage= function () {
		var configs = dbSharedService.getModalConfig(),
			page = dbSharedService.getPage();

		if (configs.pageTitle.length === 0) {
			$("#pageTitle").closest(".control-group").addClass("error");
			return;
		}
		
		try {
			if (configs.id === "") {
				var pageId = dbSharedService.generatePageId($scope.pages);
				$scope.pages.push({id:pageId, name:configs.pageTitle, items: $.parseJSON(configs.contentJSON), type:"TAB"});
			}
			else {
				page.name = configs.pageTitle;
				page.items = $.parseJSON(configs.contentJSON);
				dbSharedService.setPage(page);
			}
		} catch (e) {
			$("#content_json").closest(".control-group").addClass("error");
			return;
		}
		
		$.modal.close();
		$scope.saveProfile();
		configs.mode = "";
		dbSharedService.setModalConfig(configs);
	};

	$scope.saveProfile = function () {
		$scope.profile.items = [];
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server
	};

	$scope.removePagefromProfile = function (index) {
		$scope.pages.splice(index, 1);
		$scope.saveProfile();
	};

	$scope.addDialog = function () {
		dbSharedService.showModal("#pageModal", {mode: "add", id: "", modalTitle: "Add New Page", contentJSON: "[]", pageTitle: ""});
	};

	$scope.editDialog = function () {
		var page = dbSharedService.getPage();

		dbSharedService.showModal("#pageModal", {mode: "edit", id: angular.toJson(page.id), modalTitle: "Edit Page",
		contentJSON: angular.toJson(page.items), pageTitle: page.name});
	};
}
profileCtrl.$inject = ['$scope', '$routeParams', 'Profile', 'dbSharedService'];


// Page Controller
function pageCtrl($scope, dbSharedService) {

	$scope.status = "";

	$scope.editPage = function () {
		dbSharedService.setPage($scope.page);
		this.editDialog();
	};

	$scope.removePage = function () {
		this.removePagefromProfile($scope.$index);
	};

	$scope.showUpdateSection = function (page) {
		page.status = "tabHover";
	};

	$scope.hideUpdateSection = function (page) {
		page.status = "";
	};
}

pageCtrl.$inject = ['$scope', 'dbSharedService'];


// Editor Controller
function editorCtrl($scope, dbSharedService) {

	$scope.$on("modalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();

		$("#pageTitle").focus();
		$("#pageModal h3").html(configs.modalTitle);
		$("#pageTitle").val(configs.pageTitle);
		$("#content_json").val(configs.contentJSON);


	});

	$scope.save = function () {
		var configs = {};

		configs.pageTitle = $("#pageTitle").val();
		configs.contentJSON = $("#content_json").val();

		dbSharedService.setModalConfig(configs);

		this.savePage();
	};

}

editorCtrl.$inject = ['$scope', 'dbSharedService'];