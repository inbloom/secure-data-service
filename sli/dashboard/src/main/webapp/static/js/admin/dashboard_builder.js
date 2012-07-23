angular.module('SLC.dashboardBuilder', []).

	config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/profiles/:profileId', {templateUrl: 'templates/profile.html', controller: profileCtrl}).
		otherwise({redirectTo: '/'});

	$( "input:submit, button" ).button();
}]);

function profileListCtrl($scope) {

	$scope.profiles = [
		{text:'Ed-Org', id:'edOrg'},
		{text:'School', id:'school'},
		{text:'Section', id:'section'},
		{text:'Student', id:'student'}];
}

function profileCtrl($scope, $routeParams, $http) {
	$http.get('../data/' + $routeParams.profileId + '.json').success(function(data) {
		$scope.profile = data;
		$scope.pages = data.items;
		$scope.id = data.id;
	});

	$scope.checkTab = function (item) {
		return item.type == "TAB";
	}

	$scope.addPages = function () {
		$scope.pages.push({name:$scope.pageText, type:"TAB"});
		$scope.pageText = '';
		$("#"+$scope.id+"Dialog").dialog("destroy");
	};

	$scope.showJson = function () {
		alert(angular.toJson($scope.profile));
	};

	$scope.showDialog = function () {

		$(".dialogBox").dialog("destroy");

		$("#"+$scope.id+"Dialog").dialog({
			height: 150,
			width: 200,
			modal: true,
			show: "fade",
			hide: "fade"
		});
	}

	$( "#tabs" ).tabs().find( ".ui-tabs-nav" ).sortable({ axis: "x" });
}

function editorCtrl($scope) {
	$scope.editorEnabled = false;

	$scope.editPage = function () {
		$scope.editorEnabled = true;
		$scope.editText = $scope.page.name;
	};

	$scope.disableEditor = function () {
		$scope.editorEnabled = false;
	};

	$scope.savePage = function () {
		if ($scope.editText === "") {
			return false;
		}

		$scope.page.name = $scope.editText;
		$scope.disableEditor();
	};
}
