"use strict";

import "../../core/core.js";
import {RevisionChooserDirective} from "./revisionChooserDirective.js";
import {RevisionChooserController} from "./revisionChooserController.js";

angular.module("app.testVille.ui.revisionChooser",["app.testVille.core.data"]);

angular.module("app.testVille.ui.revisionChooser").controller(
    "revisionChooserController",
    RevisionChooserController
);

angular.module("app.testVille.ui.revisionChooser").directive(
    "revisionChooserDirective", 
    () => new RevisionChooserDirective()
);

