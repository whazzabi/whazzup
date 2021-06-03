angular.module('dashapp')
    .directive('options', function () {

        var directive = {};

        directive.templateUrl = 'app/dash/options/options.html';
        directive.controllerAs = 'optionscontroller';
        directive.bindToController = true;

        directive.controller = function ($scope, AppConfig) {
            console.info("ASDF", $scope.config.design)
            changeDesignTo($scope.config.design);

            $scope.changeDesign = function () {
                changeDesignTo($scope.config.design);
            };
        };

        return directive;
    });

function changeDesignTo(newDesign) {
    $("#design-script").remove();
    $('head').append('<link id="design-script" rel="stylesheet" type="text/css" href="design-' + newDesign + '.css">');
}