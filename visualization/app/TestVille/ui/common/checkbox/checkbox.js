"use strict";

import {CheckboxDirective} from "./checkboxDirective.js";

angular.module("app.testVille.ui.common.checkbox",[]);

angular.module("app.testVille.ui.common.checkbox").directive(
    "checkboxDirective",
    () => new CheckboxDirective()
);
