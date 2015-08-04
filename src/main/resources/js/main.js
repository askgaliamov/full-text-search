angular.module('search-app', ['ui.bootstrap'])

    .controller('searchController', function ($scope, $http) {

        $scope.searchQuery = "";
        $scope.searchResult = [];

        $scope.search = function () {
            $http({
                url: '/search',
                method: 'GET',
                params: {q: $scope.searchQuery}
            }).
                success(function (data) {
                    $scope.searchResult = data;
                }).
                error(function (data, status, headers, config) {
                    $scope.searchResult = [];
                })
        };

        $scope.search();

    });

