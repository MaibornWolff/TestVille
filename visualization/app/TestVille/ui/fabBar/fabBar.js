"use strict";

import "../common/fab/fab.js";
import {FabBarDirective} from "./fabBarDirective.js";

angular.module("app.testVille.ui.fabBar",["app.testVille.ui.common.fab"]);

angular.module("app.testVille.ui.fabBar").directive(
    "fabBarDirective",
    () => new FabBarDirective()
);
