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

/* Profile List Controller
 * @param $scope - scope object for controller
 * @param Profiles - Service to get all the profiles
 * @param dbSharedService - Service which contains common methods shared by controllers
 */

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


/* Profile Controller
 * @param $scope - scope object for controller
 * @param Profile - Service to get the profile config
 * @param AllPanels - Service to get all available panels for the profile
 * @param dbSharedService - Service which contains common methods shared by controllers
 */

function profileCtrl($scope, $routeParams, Profile, AllPanels, dbSharedService) {

	/* set flag to show view/edit mode for page title in the page.
	if checked is false, page title will be read-only and only viewable.
	if checked is true, page title will be editable. */
	$scope.checked = false;

	Profile.query({profilePageId: $routeParams.profileId}, function(profile) {
		var i;

		$scope.profile = profile[0];
		$scope.pages = [];
		$scope.panels = [];

		// Check profile items. Store all tabs into the pages array and all panels into panels array
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

	// After re-ordering the tabs, save the tab order into the profile config
	$scope.$on("tabChanged", function () {
		$scope.pages = [];
		$scope.pages = $scope.newPageArray;
		$scope.saveProfile();
	});

	$scope.savePage = function () {
		var configs = dbSharedService.getModalConfig(),
			page = dbSharedService.getPage();

		if (configs.pageTitle.length === 0) {
			$("#pageTitle").closest(".control-group").addClass("error");
			return;
		}

		try {
			if (configs.mode === "panel" && configs.id === "") {
				// add a new panel into the page
				page.items.push({id:configs.pageTitle, type:"PANEL"});
			}
			else {
				// Save the updated page source code
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

	// Save profile config to the server
	$scope.saveProfile = function (callback) {
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile), callback);

	};

	$scope.removePageFromProfile = function (index) {
		$scope.pages.splice(index, 1);
		$scope.saveProfile();
	};

	$scope.showPageSourceCode = function () {
		var page = dbSharedService.getPage();

		dbSharedService.showModal("#modalBox", {mode: "page", id: angular.toJson(page.id), modalTitle: "Page Details",
		contentJSON: angular.toJson(page.items), pageTitle: page.name});
	};
}
profileCtrl.$inject = ['$scope', '$routeParams', 'Profile', 'AllPanels', 'dbSharedService'];

/* Page controller
 * @param $scope - scope object for controller
 * @param $rootScope - root scope for the application
 * @param dbSharedService - Service which contains common methods shared by controllers
 */
function pageCtrl($scope, $rootScope, dbSharedService) {

	var parent = $scope.$parent;

	// The panel view gets changed whenever there is a change in the page config
	$scope.$watch('page.items', function(newValue, oldValue) {
		$scope.pagePanels = newValue;
	});

	$scope.pageName = $scope.page.name;

	$scope.cancelPageTitle = function () {
		parent.checked = false;
		$scope.pageName = $scope.page.name;
	};

	$scope.editPageTitle = function () {
		parent.checked = true;
		//$(".pageName").focus();
	};

	$scope.savePageTitle = function () {
		if ($scope.pageName.length === 0) {
			return;
		}
		$scope.page.name = $scope.pageName;
		parent.saveProfile();
		parent.checked = false;
	};

	$scope.showPageConfig = function () {
		dbSharedService.setPage($scope.page);
		parent.showPageSourceCode();
	};

	$scope.removePage = function () {
		if(confirm("Are you sure you want to remove the tab? There is no way to undo this action.")) {
			parent.removePageFromProfile($scope.$index);
			$rootScope.$broadcast("pageRemoved", $scope.$index);
		}
	};

	$scope.showPanels = function () {
		dbSharedService.showModal("#allPanelsModal", {mode: "panel", id: "", modalTitle: "Add A Panel"});
		dbSharedService.setPage($scope.page);
	};

	// After re-ordering the panels, save the panel order into the profile config
	$scope.$on("panelChanged", function () {
		$scope.page.items = [];
		$scope.page.items = $scope.newPageArray;
		parent.saveProfile();
	});

}

pageCtrl.$inject = ['$scope', '$rootScope', 'dbSharedService'];

/* Panel controller
 * @param $scope - scope object for controller
 */

function panelCtrl($scope) {

	var parent = $scope.$parent;

	$scope.removePanel = function () {
		if(confirm("Are you sure you want to remove the panel?")) {
			$scope.pagePanels.splice($scope.$index, 1);
			parent.saveProfile();
		}
	};


}

panelCtrl.$inject = ['$scope'];


/* Modal Box Controller
 * @param $scope - scope object for controller
 * @param dbSharedService - Service which contains common methods shared by controllers
 */

function modalCtrl($scope, dbSharedService) {

	var parent = $scope.$parent;

	// Listen the event when all modal dialog box gets displayed
	$scope.$on("modalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();

		$("#pageTitle").focus();
		$("#modalBox h3").html(configs.modalTitle);
		$("#pageTitle").val(configs.pageTitle);
		$("#content_json").val(configs.contentJSON);
	});

	$scope.save = function () {
		dbSharedService.setModalConfig({pageTitle: $scope.pageTitle, contentJSON: $scope.contentJSON});
		parent.savePage();
	};
}

modalCtrl.$inject = ['$scope', 'dbSharedService'];


/* All available panels list controller
 * @param $scope - scope object for controller
 * @param dbSharedService - Service which contains common methods shared by controllers
 */
function allPanelListCtrl($scope, dbSharedService) {

	var parent = $scope.$parent;

	parent.selectedPanels = [];

	// Listen the event when all available panels modal box gets displayed
	$scope.$on("allPanelsModalDisplayed", function () {
		var configs = dbSharedService.getModalConfig();
		parent.selectedPanels = [];

		$("#allPanelsModal h3").html(configs.modalTitle);

		// Used jquery selectable plugin to select multiple panels.
		// store all selected panels into the array
		$("#panelSelectable").selectable({
			stop: function() {
				parent.selectedPanels = [];
				$( ".ui-selected", this ).each(function() {
					if(this.tagName !== "SPAN") {
						var index = $( "#panelSelectable li" ).index( this );
						parent.selectedPanels.push($scope.allPanels[index]);
					}
				});
			}
		});
	});

	$scope.addAvailPanels = function () {
		parent.addPanelsToPage();
	};
}

allPanelListCtrl.$inject = ['$scope', 'dbSharedService'];