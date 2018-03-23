"use strict";

import {CollapsibleDirective} from "./collapsibleDirective.js";

angular.module("app.testVille.ui.common.collapsible",[]);

angular.module("app.testVille.ui.common.collapsible").directive(
    "collapsibleDirective",
    () => new CollapsibleDirective()
);
