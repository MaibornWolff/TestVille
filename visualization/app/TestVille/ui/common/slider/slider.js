"use strict";

import {SliderDirective} from "./sliderDirective.js";

angular.module("app.testVille.ui.common.slider",[]);

angular.module("app.testVille.ui.common.slider").directive(
    "sliderDirective",
    () => new SliderDirective()
);
