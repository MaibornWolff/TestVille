"use strict";

import "../../../core/tooltip/tooltip.js";
import {DropdownDirective} from "./dropdownDirective.js";
import {DropdownController} from "./dropdownController.js";

angular.module("app.testVille.ui.common.dropdown",["app.testVille.core.tooltip"]);

angular.module("app.testVille.ui.common.dropdown").directive(
    "dropdownDirective",
    () => new DropdownDirective()
);

angular.module("app.testVille.ui.common.dropdown").controller(
    "dropdownController",
    DropdownController
);
