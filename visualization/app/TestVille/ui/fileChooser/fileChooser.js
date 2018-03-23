"use strict";

import "../../core/core.js";
import {FileChooserDirective} from "./fileChooserDirective.js";
import {FileChooserController} from "./fileChooserController.js";
import {FileChooserPanelDirective} from "./fileChooserPanelDirective.js";

angular.module("app.testVille.ui.fileChooser",["app.testVille.core.data"]);

angular.module("app.testVille.ui.fileChooser").controller(
    "fileChooserController", FileChooserController
);

angular.module("app.testVille.ui.fileChooser").directive(
    "fileChooserPanelDirective", 
    () => new FileChooserPanelDirective()
);

angular.module("app.testVille.ui.fileChooser").directive(
    "fileChooserDirective", 
    () => new FileChooserDirective()
);
