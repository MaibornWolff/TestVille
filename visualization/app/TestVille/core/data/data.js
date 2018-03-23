"use strict";

import {DataService} from "./dataService.js";
import {DataValidatorService} from "./dataValidatorService.js";

angular.module("app.testVille.core.data",[]);

angular.module("app.testVille.core.data").service(
    "dataService", DataService
);

angular.module("app.testVille.core.data").service(
    "dataValidatorService", DataValidatorService
);
