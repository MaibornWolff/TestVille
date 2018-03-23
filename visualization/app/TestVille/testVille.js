"use strict";

import "./codeMap/codeMap.js";
import "./core/core.js";
import "./ui/ui.js";

import {TestVilleController} from "./testVilleController.js";
import {TestVilleDirective} from "./testVilleDirective.js";

angular.module(
    "app.testVille",
    ["app.testVille.codeMap", "app.testVille.core", "app.testVille.ui"]
);

angular.module("app.testVille").controller(
    "testVilleController",
    TestVilleController
);

angular.module("app.testVille").directive(
    "testVilleDirective",
    () => new TestVilleDirective()
);





