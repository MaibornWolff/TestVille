"use strict";

import "../../core/core.js";
import "../../codeMap/codeMap.js";

import {DetailPanelController} from "./detailPanelController.js";
import {DetailPanelDirective} from "./detailPanelDirective.js";

angular.module(
    "app.testVille.ui.detailPanel",
    ["app.testVille.core.settings", "app.testVille.codeMap"]
);

angular.module("app.testVille.ui.detailPanel").controller(
    "detailPanelController", DetailPanelController
);

angular.module("app.testVille.ui.detailPanel").directive(
    "detailPanelDirective",
    () => new DetailPanelDirective()
);

