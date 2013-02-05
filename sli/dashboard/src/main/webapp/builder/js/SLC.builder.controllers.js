/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
/*global angular $ confirm*/

/* Profile List Controller
 * @param $scope - scope object for controller
 * @param Profiles - Service to get all the profiles
 * @param dbSharedService - Service which contains common methods shared by controllers
 */

function profileListCtrl($scope, $rootScope, Profiles, dbSharedService) {
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

	// If user click on profile link in the left column, the respective profile page will be displayed.
	$scope.goToProfile = function (profileURL) {

		// If user navigate away from profile without saving page level changes,
		// the alert message box will be displayed.
		if($rootScope.saveStatus) {

			$rootScope.profileAlert = true; // set profile alert to true
			dbSharedService.showModal("#alertModal", {mode: "alert"});

			$scope.$on("leaveProfile", function () {
				$rootScope.profileAlert = false;
				location.href = profileURL;

				dbSharedService.enableSaveButton(false);
			});

			return false;
		}

		location.href = profileURL; // the user will redirect to the selected profile page.

	};

	// if user refresh the page or close the window without saving page level changes,
	// the alert message box will be displayed.
	$(window).bind('beforeunload', function() {
		if($rootScope.saveStatus) {
			return 'Dashboard builder';
		}
	});

	$rootScope.saveStatus = false;
}

profileListCtrl.$inject = ['$scope', '$rootScope', 'Profiles', 'dbSharedService'];


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


		// Get the list of available panels for selected profile
		$scope.allPanels = AllPanels.query({profileId: $routeParams.profileId}, function() {}, function(error) {
			dbSharedService.showError(error.status, null);
		});

	}, function(error) {
		dbSharedService.showError(error.status, null);
	});


	// After re-ordering the tabs, save the tab order into the profile config
	$scope.$on("tabChanged", function () {
		$scope.pages = [];
		$scope.pages = $scope.newPageArray;
		$scope.saveProfile();
	});

	$scope.updatePage = function () {
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
		dbSharedService.enableSaveButton(true);
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

		dbSharedService.enableSaveButton(true);
		$scope.selectedPanels = [];
	};

	// Save profile config to the server
	$scope.saveProfile = function (callback) {
		$scope.profileItemArray = $scope.panels.concat($scope.pages);
		$scope.profile.items = $scope.profileItemArray;
		dbSharedService.saveDataSource(angular.toJson($scope.profile), callback);
	};

	$scope.removePageFromProfile = function (index, callback) {
		$scope.pages.splice(index, 1);
		$scope.saveProfile();
		if(callback) {
			callback();
		}
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
function pageCtrl($scope, $rootScope, $routeParams, Profile, Page, dbSharedService) {

	var parent = $scope.$parent;

	$scope.pageName = $scope.page.name;

	// If user choose to leave the page or adding a new tab without saving new changes,
	// then old changes will be restored
	$scope.$on("restorePage", function () {

		// The profile service will be called to restore the changes at page level
		Profile.query({profilePageId: $routeParams.profileId}, function(profile) {
			var i;

			$scope.profile = profile[0];


			for (i = 0; i < $scope.profile.items.length; i++) {
				if($scope.profile.items[i].type === "TAB" && $scope.profile.items[i].id === $scope.page.id) {
					$scope.page.name = $scope.profile.items[i].name;
					$scope.page.items = $scope.profile.items[i].items;

				}
			}

			// if user adding a new tab without saving selected page level changes,
			// the custom event 'restorePageAndAddNewPage' will be triggered
			if($rootScope.addNewPage) {
				if($scope.profile.items[$scope.profile.items.length-1].id === $scope.page.id) {
					dbSharedService.enableSaveButton(false);
					$rootScope.$broadcast("restorePageAndAddNewPage");
				}
			}

			// if user leaving the page without saving page level changes,
			// then 'leavePage' event will be triggered.
			if($rootScope.leavePageStatus) {
				$rootScope.leavePageStatus = false;
				$rootScope.$broadcast("leavePage");
			}

		}, function(error) {
			dbSharedService.showError(error.status, null);
		});

		return false;
	});

	// The panel view gets changed whenever there is a change in the page config
	$scope.$watch('page.items', function(newValue, oldValue) {
		$scope.pagePanels = newValue;
	});

	$scope.cancelPageTitle = function () {
		parent.checked = false;
		$scope.pageName = $scope.page.name;
	};

	$scope.editPageTitle = function () {
		parent.checked = true;
	};

	$scope.savePageTitle = function () {
		if ($scope.pageName.length === 0) {
			return;
		}
		$scope.page.name = $scope.pageName;

		dbSharedService.enableSaveButton(true);
		parent.checked = false;
	};

	$scope.showPageConfig = function () {
		dbSharedService.setPage($scope.page);
		parent.showPageSourceCode();
	};

	$scope.removePage = function () {

		dbSharedService.showModal("#removeTab", {mode: "removeTab", id: ""});

		$scope.$on("removeTab", function () {
			parent.removePageFromProfile($scope.$index, function () {
				$rootScope.$broadcast("pageRemoved", $scope.$index);
			});
		});
	};

	$scope.showPanels = function () {
		dbSharedService.showModal("#allPanelsModal", {mode: "panel", id: "", modalTitle: "Add A Panel"});
		dbSharedService.setPage($scope.page);
	};

	// After re-ordering the panels, save the panel order into the profile config
	$scope.$on("panelChanged", function () {
		$scope.page.items = [];
		$scope.page.items = $scope.newPageArray;

		dbSharedService.enableSaveButton(true);
	});

	// After user click 'Publish Layout' button, the changes will be saved on the server.
	$scope.publishPage = function () {
		parent.saveProfile(function () {
			$(".successMessage").show();
			window.setTimeout(function() { $(".successMessage").hide("slow"); }, 10000); // the success messages will be stayed for 10 seconds on the page
			dbSharedService.enableSaveButton(false);
		});
	};

	$scope.restore = function () {
		$rootScope.$broadcast("restorePage");
		$(".restoreMessage").show();
		window.setTimeout(function() { $(".restoreMessage").hide("slow"); }, 10000); // the restore changes messages will be stayed for 10 seconds on the page
		dbSharedService.enableSaveButton(false);

	};

}

pageCtrl.$inject = ['$scope', '$rootScope', '$routeParams', 'Profile', 'Page', 'dbSharedService'];

/* Panel controller
 * @param $scope - scope object for controller
 */

function panelCtrl($scope, dbSharedService) {

	$scope.removePanel = function () {
		$scope.pagePanels.splice($scope.$index, 1);

		dbSharedService.enableSaveButton(true);
	};


}

panelCtrl.$inject = ['$scope', 'dbSharedService'];


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
		parent.updatePage();
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


/* confirmation box modal controller
 * @param $scope - scope object for controller
 * @param $rootScope - rootScope object for controller
 * @param dbSharedService - Service which contains common methods shared by controllers
 */
function confirmBoxCtrl($scope, $rootScope, dbSharedService) {

	var configs = dbSharedService.getModalConfig();

	$("#alertModal h3").html(configs.modalTitle);

	// This function trigger custom events on profile and page level, if user leaves the page without saving the changes.
	$scope.leaveChanges = function () {
		$rootScope.saveStatus = false;
		if ($rootScope.profileAlert) {
			$rootScope.$broadcast("leaveProfile");
		}
		else {
				$rootScope.leavePageStatus = true;
				$rootScope.$broadcast("restorePage");
		}
	};

}

confirmBoxCtrl.$inject = ['$scope', '$rootScope', 'dbSharedService'];


/* remove page/tab confirmation box modal controller
 * @param $scope - scope object for controller
 * @param dbSharedService - Service which contains common methods shared by controllers
 */
function removeTabCtrl($scope, $rootScope) {

	// If user click 'remove tab' button, the 'removeTab' event will be triggered.
	$scope.removeTab = function () {
		$rootScope.$broadcast("removeTab");
	};

}

removeTabCtrl.$inject = ['$scope', '$rootScope'];

/* panelsCtrl Controller - display list of panels for the profile selected in left hand side panel view
 * @param $scope - scope object for controller
 * @param $routeParams - route parameter passed from the URL
 * @param AllPanels - Service to get all available panels for the profile
 * @param dbSharedService - Service which contains common methods shared by controllers
 */

function panelsCtrl($scope, $routeParams, AllPanels, dbSharedService) {

	$scope.allListPanels = AllPanels.query({profileId: $routeParams.profileId}, function() {}, function(error) {
		dbSharedService.showError(error.status, null);
	});

	$scope.profileTitle = $routeParams.profileId;
}

panelsCtrl.$inject = ['$scope', '$routeParams', 'AllPanels', 'dbSharedService'];
