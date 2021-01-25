angular.module('dashapp')
    .directive('navbaritem', function () {

        var directive = {};

        directive.replace = true;
        directive.templateUrl = 'app/dash/navbar/navbar-item.html';
        directive.controllerAs = 'navbaritemcontroller';
        directive.bindToController = true;

        directive.controller = function ($scope) {

        };

        return directive;
    });
