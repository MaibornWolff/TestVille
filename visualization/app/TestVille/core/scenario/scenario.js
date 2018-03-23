"use strict";

import "../settings/settings.js";
import "../data/data.js";

import {ScenarioService} from "./scenarioService.js";

angular.module(
    "app.testVille.core.scenario",
    ["app.testVille.core.settings", "app.testVille.core.data"]
);

angular.module("app.testVille.core.scenario").service(
    "scenarioService", ScenarioService
);

