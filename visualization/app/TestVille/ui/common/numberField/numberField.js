"use strict";

import {NumberFieldDirective} from "./numberFieldDirective.js";

angular.module("app.testVille.ui.common.numberField",[]);

angular.module("app.testVille.ui.common.numberField").directive(
    "numberFieldDirective",
    () => new NumberFieldDirective()
);
