"use strict";

import "../url/url.js";
import {TooltipService} from "./tooltipService.js";

angular.module("app.testVille.core.tooltip",["app.testVille.core.url"]);

angular.module("app.testVille.core.tooltip").service(
    "tooltipService", TooltipService
);


