"use strict";

import "../data/data.js";
import {TreeMapService} from "./treeMapService.js";

angular.module("app.testVille.core.treemap", ["app.testVille.core.data"]);

angular.module("app.testVille.core.treemap").service("treeMapService", TreeMapService);
