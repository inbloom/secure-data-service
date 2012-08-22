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
	$("#header").load("../s/m/header");
}

// Footer Controller
function footerCtrl() {
	$("#footer").load("../s/m/footer");
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
			if (configs.mode === "page" && configs.id === "") {
				var pageId = dbSharedService.generatePageId($scope.pages);
				$scope.pages.push({id:pageId, name:configs.pageTitle, items: $.parseJSON(configs.contentJSON), parentId:pageId, type:"TAB"});
				$rootScope.$broadcast("tabAdded", pageId, configs.pageTitle);
			}
			else if (configs.mode === "panel" && configs.id === "") { // add a new panel into the page
				page.items.push({id:configs.pageTitle, type:"PANEL"});
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

	// Add selected panels from the list of available panels into the page
	$scope.addPanelsToPage = function () {
		var configs = dbSharedService.getModalConfig(),
			page = dbSharedService.getPage(),
			i;

		if ($scope.panelsSelected.length === 0) {
			$("#errorMessage").show().addClass("alert alert-error");
			return;
		}

		for (i = 0; i < $scope.panelsSelected.length; i++) {
			var formElem = {};

			formElem.pageTitle = $scope.panelsSelected[i].id;
			formElem.contentJSON = [];

			dbSharedService.setModalConfig(formElem);
			page.items.push({id:configs.pageTitle, parentId:configs.pageTitle, type:"PANEL"});
		}

		$.modal.close();
		$scope.saveProfile();
		configs.mode = "";
		$scope.panelsSelected = [];
		dbSharedService.setModalConfig(configs);
	};

	$scope.saveProfile = function () {
		$scope.profile.items = [];
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile)); // Save profile to the server
	};

	$scope.removePageFromProfile = function (index) {
		$scope.pages.splice(index, 1);
		$scope.saveProfile();
	};

	$scope.addPage = function () {
		dbSharedService.showModal("#modalBox", {mode: "page", id: "", modalTitle: "Add New Page", contentJSON: "[]", pageTitle: ""});
	};

	$scope.editDialog = function () {
		var page = dbSharedService.getPage();

		dbSharedService.showModal("#modalBox", {mode: "page", id: angular.toJson(page.id), modalTitle: "Edit Page",
		contentJSON: angular.toJson(page.items), pageTitle: page.name});
	};
}
profileCtrl.$inject = ['$scope', '$rootScope', '$routeParams', 'Profile', 'AllPanels', 'dbSharedService'];

// Panel controller
function panelCtrl($scope, $rootScope, dbSharedService) {
	$scope.pagePanels = $scope.page.items;
	$scope.done = false;

	$scope.pageName = $scope.page.name;

	$scope.addNewPanel = function () {
		dbSharedService.showModal("#modalBox", {mode: "panel", id: "", modalTitle: "Add New Panel", contentJSON: "[]", pageTitle: ""});
		dbSharedService.setPage($scope.page);
	};

	$scope.savePageTitle = function () {
		$scope.page.name = $scope.pageName;
		$scope.done = false;
		this.saveProfile();
	};

	$scope.removePage = function () {
		this.removePageFromProfile($scope.$index);
		$rootScope.$broadcast("pageRemoved", $scope.$index);
	};

	$scope.showPanels = function () {
		dbSharedService.showModal("#allPanelsModal", {mode: "panel", id: "", modalTitle: "Add A Panel"});
		dbSharedService.setPage($scope.page);
	};

	$scope.editPageTitle = function () {
		$scope.done = true;
		$("#allPanelsModal #pageName").focus();
	};
}

panelCtrl.$inject = ['$scope', '$rootScope', 'dbSharedService'];


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

	$scope.$parent.panelsSelected = [];
	$scope.predicate = '-educationAgencyName';

	$scope.$on("allPanelsModalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();
		$scope.$parent.panelsSelected = [];

		$("#allPanelsModal h3").html(configs.modalTitle);

		$("#panelSelectable").selectable({
			stop: function() {
				$scope.$parent.panelsSelected = [];
				$( ".ui-selected", this ).each(function() {
					var index = $( "#panelSelectable li" ).index( this );
					$scope.$parent.panelsSelected.push($scope.allPanels[index]);
				});
			}
		});

	});

	$scope.addAvailPanels = function () {
		this.addPanelsToPage();
	};

	$scope.addNewPanel = function () {
		$.modal.close();
		dbSharedService.showModal("#modalBox", {mode: "panel", id: "", modalTitle: "Add New Panel", contentJSON: "[]", pageTitle: ""});
	};
}

allPanelListCtrl.$inject = ['$scope', 'AllPanels', 'dbSharedService'];
