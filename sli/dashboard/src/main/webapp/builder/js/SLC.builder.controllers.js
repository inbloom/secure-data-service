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
		dbSharedService.showError(error.status, null);
	});
}

profileListCtrl.$inject = ['$scope', 'Profiles', 'dbSharedService'];


// Profile Controller
function profileCtrl($scope, $rootScope, $routeParams, Profile, AllPanels, dbSharedService) {

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

		// get all available panels for the profile
		$scope.allPanels = AllPanels.query({profileId: $routeParams.profileId});

	}, function(error) {
		dbSharedService.showError(error.status, null);
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
			if (configs.mode === "page" && configs.id === "") { // add a new page
				var pageId = dbSharedService.generatePageId($scope.pages);
				$scope.pages.push({id:pageId, name:configs.pageTitle, items: $.parseJSON(configs.contentJSON), parentId:pageId, type:"TAB"});
			}
			else if (configs.mode === "panel" && configs.id === "") { // add a new panel into the page
				page.items.push({id:configs.pageTitle, type:"PANEL"});
			}
			else { // update page
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

	// Add selected panels from the list of available panels into the page
	$scope.addPanelsToPage = function () {
		var page = dbSharedService.getPage(),
			i;

		if ($scope.selectedPanels.length === 0) {
			$("#errorMessage").show().addClass("alert alert-error");
			return;
		}

		for (i = 0; i < $scope.selectedPanels.length; i++) {
			var panel = {};

			panel.id = $scope.selectedPanels[i].id;
			panel.type = $scope.selectedPanels[i].type;
			panel.contentJSON = [];

			page.items.push({id:panel.id, parentId:panel.id, type:panel.type});
		}

		$.modal.close();
		$scope.saveProfile();
		$scope.selectedPanels = [];
	};

	$scope.saveProfile = function (callback) {
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile), callback); // Save profile to the server

	};

	$scope.removePageFromProfile = function (index) {
		$scope.pages.splice(index, 1);
		$scope.saveProfile();
	};

	$scope.showPageDetails = function () {
		var page = dbSharedService.getPage();

		dbSharedService.showModal("#modalBox", {mode: "page", id: angular.toJson(page.id), modalTitle: "Page Details",
		contentJSON: angular.toJson(page.items), pageTitle: page.name});
	};
}
profileCtrl.$inject = ['$scope', '$rootScope', '$routeParams', 'Profile', 'AllPanels', 'dbSharedService'];

// Page controller
function pageCtrl($scope, $rootScope, dbSharedService) {

	// The panel view gets changed whenever there is a change in the page config
	$scope.$watch('page.items', function(newValue, oldValue) {
		$scope.pagePanels = newValue;
	});

	$scope.pageName = $scope.page.name;
	$scope.checked = false;

	$scope.cancelPageTitle = function () {
		$scope.checked = false;
	};

	$scope.editPageTitle = function () {
		$scope.checked = true;
		$(".pageName").focus();
	};

	$scope.savePageTitle = function () {
		$scope.page.name = $scope.pageName;
		this.saveProfile();
		$scope.checked = false;
	};

	$scope.showPageConfig = function () {
		dbSharedService.setPage($scope.page);
		this.showPageDetails();
	};

	$scope.addNewPanel = function () {
		dbSharedService.showModal("#modalBox", {mode: "panel", id: "", modalTitle: "Add New Panel", contentJSON: "[]", pageTitle: ""});
		dbSharedService.setPage($scope.page);
	};

	$scope.removePage = function () {
		this.removePageFromProfile($scope.$index);
		$rootScope.$broadcast("pageRemoved", $scope.$index);
	};

	$scope.showPanels = function () {
		dbSharedService.showModal("#allPanelsModal", {mode: "panel", id: "", modalTitle: "Add A Panel"});
		dbSharedService.setPage($scope.page);
	};
}

pageCtrl.$inject = ['$scope', '$rootScope', 'dbSharedService'];


// Modal Box Controller
function modalCtrl($scope, dbSharedService) {

	$scope.$on("modalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();

		$("#pageTitle").focus();
		$("#modalBox h3").html(configs.modalTitle);
		$("#pageTitle").val(configs.pageTitle);
		$("#content_json").val(configs.contentJSON);
	});

	$scope.save = function () {
		var formElem = {};

		formElem.pageTitle = $("#pageTitle").val();
		formElem.contentJSON = $("#content_json").val();

		dbSharedService.setModalConfig(formElem);
		this.savePage();
	};

}

modalCtrl.$inject = ['$scope', 'dbSharedService'];


// All available panels list controller
function allPanelListCtrl($scope, AllPanels, dbSharedService) {

	var parent = $scope.$parent;

	parent.selectedPanels = [];

	$scope.$on("allPanelsModalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();
		parent.selectedPanels = [];

		$("#allPanelsModal h3").html(configs.modalTitle);

		$("#panelSelectable").selectable({
			stop: function() {
				parent.selectedPanels = [];
				$( ".ui-selected", this ).each(function() {
					var index = $( "#panelSelectable li" ).index( this );
					parent.selectedPanels.push($scope.allPanels[index]);
				});
			}
		});
	});

	$scope.addAvailPanels = function () {
		parent.addPanelsToPage();
	};

	$scope.addNewPanel = function () {
		$.modal.close();
		dbSharedService.showModal("#modalBox", {mode: "panel", id: "", modalTitle: "Add New Panel", contentJSON: "[]", pageTitle: ""});
	};
}

allPanelListCtrl.$inject = ['$scope', 'AllPanels', 'dbSharedService'];