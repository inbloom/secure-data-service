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
			return $resource('/dashboard/s/c/cfg?type=LAYOUT');
		})
	.factory('Profile', function($resource){
		return $resource('/dashboard/s/c/cfg?type=LAYOUT&id=:profilePageId', {}, {
			query: {method:'GET', params:{profilePageId:''}, isArray:true}
		});
	})
	.factory('dbSharedService', function($http, $rootScope){
		var page = {},
			modalConfig = {
				mode: "",
				modalTitle: "Title",
				pageTitle: "",
				contentJSON: "[]"
			};

		function getPage() {
			return page;
		}

		function setPage(item) {
			page = item;
		}

		function showModal(modalId, modalCfg) {
			setModalConfig(modalCfg);
			$(modalId).modal('show');
			$rootScope.$broadcast("modalDisplayed");
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
				url: '/dashboard/s/c/cfg',
				data: profileData
			}).success(function() {
				console.log("success");
			}).error(function() {
				console.log("fail");
				alert("Server Error");
			});
		}

		return {
			getPage: getPage,
			setPage: setPage,
			saveDataSource: saveDataSource,
			showModal: showModal,
			getModalConfig: getModalConfig,
			setModalConfig: setModalConfig
		};
	});