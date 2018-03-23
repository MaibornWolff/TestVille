"use strict";

import "../../core/tooltip/tooltip.js";
import {ScenarioButtonsDirective} from "./scenarioButtonsDirective.js";
import {ScenarioButtonsController} from "./scenarioButtonsController.js";

angular.module("app.testVille.ui.scenarioButtons",["app.testVille.core.scenario", "app.testVille.core.tooltip"]);

angular.module("app.testVille.ui.scenarioButtons").directive(
    "scenarioButtonsDirective",
    () => new ScenarioButtonsDirective()
);

angular.module("app.testVille.ui.scenarioButtons").controller(
    "scenarioButtonsController",
    ScenarioButtonsController
);
