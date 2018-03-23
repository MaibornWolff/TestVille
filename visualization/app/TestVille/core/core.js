"use strict";

import "./data/data.js";
import "./settings/settings.js";
import "./url/url.js";
import "./treemap/treemap.js";
import "./scenario/scenario.js";
import "./tooltip/tooltip.js";

angular.module(
    "app.testVille.core",
    [
        "app.testVille.core.data",
        "app.testVille.core.settings",
        "app.testVille.core.url",
        "app.testVille.core.treemap",
        "app.testVille.core.scenario",
        "app.testVille.core.tooltip"
    ]
);
