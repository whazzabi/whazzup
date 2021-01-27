angular.module('dashapp')
    .directive('navbar', function () {

        var directive = {};

        directive.templateUrl = 'app/dash/navbar/navbar.html';
        directive.controllerAs = 'navbarcontroller';
        directive.bindToController = true;

        directive.controller = function ($scope, AppConfig) {
            AppConfig.get().$promise.then(function (config) {
                $scope.title = config.pagetitle
                $scope.navbar = config.navbar
            });
        };

        return directive;
    });
