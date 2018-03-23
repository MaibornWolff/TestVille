"use strict";

import "./TestVille/testVille.js";

angular.module("app", ["app.testVille"]);

angular.module("app").config(["$locationProvider", function($locationProvider) {
    $locationProvider.hashPrefix("");
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
}]);