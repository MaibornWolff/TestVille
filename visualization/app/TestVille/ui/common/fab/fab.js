"use strict";

import {FabDirective} from "./fabDirective.js";

angular.module("app.testVille.ui.common.fab",[]);

angular.module("app.testVille.ui.common.fab").directive(
    "fabDirective",
    () => new FabDirective()
);
