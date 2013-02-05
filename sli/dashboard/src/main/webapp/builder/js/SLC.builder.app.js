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
 * SLC Dashboard Builder App
 */
/*global angular profileCtrl $*/
angular.module('SLC.builder.app', ['SLC.builder.sharedServices', 'SLC.builder.directives']).
	config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/profiles/:profileId', {templateUrl: 'js/templates/profile.html', controller: profileCtrl}).
		when('/panels/:profileId', {templateUrl: 'js/templates/panels.html', controller: panelsCtrl}).
		otherwise({redirectTo: '/'});

	$( "input:submit, button" ).button();
}]);
