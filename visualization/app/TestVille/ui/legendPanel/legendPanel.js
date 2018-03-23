"use strict";

import "../../core/core.js";
import "../../codeMap/codeMap.js";

import {LegendPanelDirective} from "./legendPanelDirective.js";
import {LegendPanelController} from "./legendPanelController.js";

angular.module("app.testVille.ui.legendPanel",["app.testVille.core.settings", "app.testVille.codeMap"]);

angular.module("app.testVille.ui.legendPanel").controller(
    "legendPanelController", LegendPanelController
);

angular.module("app.testVille.ui.legendPanel").directive(
    "legendPanelDirective", 
    () => new LegendPanelDirective()
);
