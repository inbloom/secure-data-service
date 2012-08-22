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
 * SLC Dashboard Builder Services
 */
/*global $ angular console*/
angular.module('SLC.builder.sharedServices', ['ngResource'])
	.factory('Profiles', function($resource){
			return $resource('../s/c/cfg?type=LAYOUT');
		})
	.factory('Profile', function($resource){
		return $resource('../s/c/cfg?type=LAYOUT&id=:profilePageId', {}, {
			query: {method:'GET', params:{profilePageId:''}, isArray:true}
		});
	})
	.factory('AllPanels', function($resource){
		return $resource('../s/c/cfg/all?layoutName=:profileId', {profileId:''});
	})
	.factory('dbSharedService', function($http, $rootScope){
		var page = {},
			modalConfig = {};

		function getPage() {
			return page;
		}

		function setPage(item) {
			page = item;
		}

		function showModal(modalId, modalCfg) {
			if(modalCfg) {
				setModalConfig(modalCfg);
			}
			$(".control-group").removeClass("error");
			$(modalId).modal({onOpen: function (dialog) {
				dialog.overlay.fadeIn('fast', function () {
					dialog.data.hide();
					dialog.container.fadeIn('fast', function () {
						dialog.data.slideDown('fast');
						if(modalId === "#allPanelsModal") {
							$rootScope.$broadcast("allPanelsModalDisplayed");
						}
						else {
							$rootScope.$broadcast("modalDisplayed");
						}
					});
				});
			}});

		}

		function closeModal(modalId) {
			$(modalId).modal({onClose: function (dialog) {
				dialog.data.fadeOut('fast', function () {
					dialog.container.hide('fast', function () {
						dialog.overlay.slideUp('fast', function () {
							$.modal.close();
						});
					});
				});
			}});
		}

		function getModalConfig() {
			return modalConfig;
		}

		function setModalConfig(modalCfg) {
			$.extend(modalConfig, modalCfg);
		}

		function saveDataSource(profileData) {
			$http({
				method: 'POST',
				url: '../s/c/cfg',
				data: profileData
			}).success(function() {
				console.log("success");
			}).error(function(data, status, headers, config) {
				console.log("fail");
				showError(status, null);
			});
		}

		function generatePageId(pages) {
			var tabIdPrefix = "tab",
				pageNumMax = 0,
				id,
				pageNumStr,
				pageNum,
				i;

			for (i = 0; i < pages.length; i++) {
				id = pages[i].id;
				if(id.indexOf(tabIdPrefix) === 0) {
					pageNumStr = id.substring(tabIdPrefix.length);
					if (pageNumStr.length > 0 && !isNaN(pageNumStr)) {
						pageNum = parseInt(pageNumStr, 10);
						if (pageNum > pageNumMax) {
							pageNumMax = pageNum;
						}
					}
				}
			}
			return tabIdPrefix + (pageNumMax + 1);
		}

		function showError(errorStatus, errorMsg) {

			// when the user session times out, the ajax request returns with
			// error status 0. when that happens, reload the page, forcing re-login
			if (errorStatus === 0) {
				location.reload();
				return;
			}

			$(".errorMessage").removeClass("hide");
			$("#banner").addClass("hide");
			$(".profileList").addClass("hide");
			$(".profilePageWrapper").addClass("hide");

			if (errorStatus === 401) {
				$(".errorMessage").html("Access Denied: Unauthorized user");
			} else {
				$(".errorMessage").html("Server Error");
			}
		}

		return {
			getPage: getPage,
			setPage: setPage,
			saveDataSource: saveDataSource,
			showModal: showModal,
			closeModal: closeModal,
			getModalConfig: getModalConfig,
			setModalConfig: setModalConfig,
			generatePageId: generatePageId,
			showError: showError
		};
	});